#####
#
# Generate dependency graph of input modules.
# 
# Author: Baiyan Huang
# Date: 01/05/2009
# 
# Usage:
# DependencyGrapher.pl <InputFile> [-f] [-s]
# <InputFile> 
# 	.sln: Show dependency graph between the dlls and exes generated by the specified solution. 
# 	.vcproj, .dll, .exe : Show successor and predecessor of the specified image. (The predecessor is searched in the same directory)
# 	.txt: Any of the above, Show dependency graph between specified images.
# [-f]
#	If -f(file) is specified, we will generate a pdf file in "C:\Temp\DependencyGrapher", rather than pop up a graph.
# [-s]
# 	if -s(size) option has been specified, we will show the dll size on the depedency graph as well.
#
#
# DependencyGrapher.pl R:\AmPm.sln
# DependencyGrapher.pl R:\InventorSolutions.txt
# DependencyGrapher.pl R:\Lib\Debug\Fw.dll
#
#####
use strict;
use File::Basename;
use Getopt::Long;
# Custom modules
BEGIN{push @INC, &dirname($0);}
use Utils::Basic;
use Utils::VS;

# Show Help
Getopt::Long::Configure("prefix_pattern=(-|\/)");
my ($options) ={};
GetOptions($options, "-help|h|?", "-file|f", "-size|s");
if ($options->{help})
{
	Syntax();
	exit;
}

# 1. Initialization + Configuration
&VS::SetConfig("Debug|Win32", "Debug");

my $OutputDir = &::GetTempDir() . "\\DependencyGrapher";
mkdir $OutputDir unless(-d $OutputDir);

my $InputFile = $ARGV[0];
my $InputName = &FileNameNoExt($InputFile);
my $Direction = "TD"; #"LR";  # Dependency graph direction, can also be "TD"
my $DllTool = "\"R:\\Tools\\Build Tools\\DllTool.exe\"";
my $GraphTool = "dotty.exe";
my $ImageListFile = "$OutputDir\\$InputName.txt";
my $DependencyFile = "$OutputDir\\$InputName.dep";

my @AllImages;

# 2. Get all images
#     Text File list sln, vcproj, and dll, exes.....
if(&IsTxtFile($InputFile))
{
	
	open LIST_FILE, $InputFile or die "Can't open $InputFile: $!";
	foreach my $EachFile (<LIST_FILE>)
	{
		if ($EachFile =~ /^#/ or $EachFile =~ /^\s+$/)
		{
			printf $EachFile . "\n";
			next;
		}
		my $Dir = "C:\\Program Files\\Common Files\\Autodesk Shared\\DirectConnect2012 (64-bit)\\bin\\Aruba\\";
		$EachFile = $Dir . $EachFile;
		push @AllImages, &GetTargetImages($EachFile);
	}
	close LIST_FILE;
	
}
else
{
	@AllImages = &GetTargetImages($InputFile);
}

# No duplication.
@AllImages = &UniquelizeList(@AllImages);
#print join "\n", @AllImages;
# 3. Show dependencies.
if(0 == @AllImages) # No image - Error
{
	&Report("No image file!!!\n");
}
elsif(1 == @AllImages) # One image -show predecessors and successors
{
	my $Image = @AllImages[0];
	
	# Get predecessors.
	my @Predecessors;
	my @Uses = `$DllTool \"$Image\" -uses`;
	foreach(@Uses)
	{
		if(/^\s*?(\w.*\.dll)\s*$/i)
		{
			push @Predecessors, $1;
		}
	}
	@Predecessors = &UniquelizeList(@Predecessors);
	#print join "\n", @Predecessors;
	
	# Get Successors
	my @Successors;
	my $Directory = &dirname($Image);
	my @DirImages = &GetDirImages($Directory);
	open USEDBY_LISTFILE, ">", $ImageListFile or die "Can't create file $ImageListFile: $!";
	foreach(@DirImages)
	{
		unless(/Res\.dll$/i)
		{
			unless(/\./g > 1) # C++ dlls don't have "." inside file name, it should be a .NET dll, ignore it
			{
				print USEDBY_LISTFILE $_ ;
			}
		}
	}
	close USEDBY_LISTFILE;
	my @Usedbys =  `$DllTool \"$Image\" -usedby -f $ImageListFile`;
	foreach(@Usedbys)
	{
		if(/^.*\\([^\\]+\.(dll|exe))\s*$/i)
		{
			push @Successors, $1;
		}
	}
	@Successors = &UniquelizeList(@Successors);
	#print join "\n", @Successors;
	# Write Dep File
	my $ImageName = uc &basename($Image);
	open DEP_FILE, ">", $DependencyFile or die "Can't create file $DependencyFile: $!";
	print DEP_FILE "digraph G {\n";
	print DEP_FILE "\trankdir=$Direction\n";
	foreach(@Predecessors)
	{
		print DEP_FILE "\t\"$ImageName\" -> \"$_\";\n";
	}
	foreach(@Successors)
	{
		print DEP_FILE "\t\"$_\" -> \"$ImageName\";\n";
	}
	print DEP_FILE "}\n";
	close DEP_FILE;
	
	&ShowDependency();
}
else # Multiple images -Show their dependencies.
{
	open LIST_FILE, ">", $ImageListFile or die "Can't create file $ImageListFile: $!";
	foreach(@AllImages)
	{
		print $_, "\n";
		print LIST_FILE $_, "\n";
	}
	close LIST_FILE;
	
	my $GenCmd = "$DllTool -depgraph -f $ImageListFile > $DependencyFile";
	`$GenCmd`;

	# Show the dependency use dotty
	&ShowDependency()

}

#**********************************************************Functions**********************************************************
sub ShowDependency()
{
	my $ShowCmd;
	if($options->{size}) # If we need to show the size
	{
		my $FileContent = &::GetFileContent($DependencyFile);
		#print $FileContent;
		foreach my $Dll (@AllImages)
		{	
			my $Size = &::GetFileSize($Dll);
			my $Name = &basename($Dll);
			my $NameWithSize = $Name . "\\n(" . $Size . "M" . ")"; # Add a line break between the dll name and size
			$FileContent =~ s/\"$Name\"/\"$NameWithSize\"/gi;
			#&Trace("I am here");
		}
		&::SetFileContent($DependencyFile, $FileContent);
	}
	if($options->{file})
	{
		my $PdfFile = "$OutputDir\\$InputName.pdf";
		$ShowCmd = "dot -T pdf $DependencyFile>$PdfFile";
		`$ShowCmd`;
		&::Report("\nOutput: $PdfFile\n");
	}
	else
	{
		$ShowCmd = "$GraphTool  $DependencyFile";
		`$ShowCmd`;
	}
}
sub GetSolutionImages($)
{
	my $SlnFile = $_[0];
	my @Images;
	my @Projects = &VS::GetSolutionProjects($SlnFile);
	#print join "\n", @Projects;
	foreach(@Projects)
	{
		next unless(/(?<!Res)\.vcproj/i);
		my $Output = &VS::GetProjectOutput($_);
		push @Images, $Output if(defined ($Output));
	}
	return @Images;
}

sub GetTargetImages($)
{
	my $FileName = $_[0];
	chomp $FileName;
	my @Images;
	if(&VS::IsSolutionFile($FileName))
	{	
		#&Trace($FileName);
		@Images = &GetSolutionImages($FileName);
	}
	elsif(&VS::IsImageFile($FileName))
	{
		push @Images, $FileName;
	}
	elsif(&VS::IsProjectFile($FileName))
	{
		my $Image = &VS::GetProjectOutput($FileName);
		push @Images, $Image if(defined $Image);
	}
	return @Images;
}

sub GetDirImages($)
{
	my $Dir = $_[0];
	$Dir =~ s/\\$//;
	my $AllDll = $Dir . "\\*.dll";
	#&Trace($AllDll);
	my @Dlls = `dir  \"$AllDll\" /b /s`;
	
	my $AllExe = $Dir . "\\*.exe";
	my @Exes = `dir  \"$AllExe\" /b /s`;
	
	push @Dlls, @Exes;
	return @Dlls;
}
sub IsTxtFile($)
{
	my $TxtFile = $_[0];
	if($TxtFile =~ /\.txt/i)
	{
		return 1;
	}
	else
	{
		return 0;
	}
}

#*****************************************************************Syntax*******************************************************
sub Syntax
{
    my ($Script) = ( $0 =~ /([^\\\/]*?)$/ );
    my ($Line)   = "-" x length($Script);

    print <<EOT;

$Script
$Line
Show Dependency Graph Of DLLs

Syntax:
$Script <InputFile> [-f] [-s]
1. If <InputFile> is a sln file, show dependencies of target dlls and exes
2. If <InputFile> is a txt file, show dependencies of listed dlls and exes
	You can list sln, vcproj, dll, exe files in the txt files
3. If <InputFile> is a vcproj, dll, or exe, show its predecessors and sucessors

If -f(file) is specified, we will generate a pdf file in "C:\Temp\DependencyGrapher", rather than pop up a graph.
if -s(size) option has been specified, we will show the dll size on the depedency graph as well.

Example:
$Script R:\\AmPm.sln -s
$Script R:\\InventorSolutions.txt -f
$Script R:\\Lib\\Debug\\Fw.dll

Contact Baiyan Huang for support
EOT
}