/*******************************************************************
 *         Advanced 3D Game Programming using DirectX 9.0
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * copyright (c) 2003 by Peter A Walsh and Adrian Perez
 * See license.txt for modification and distribution information
 ******************************************************************/

#include "File.h"

using namespace std;

cFile::cFile() :
	m_fp( NULL )
{
	// nothing to do
}

cFile::~cFile()
{
	// double check to make sure the user closed the file.
	if( m_fp != NULL )
	{
		Close();
	}
}

void cFile::Create( const char* filename )
{
	// Create the file handle
	//Opens an empty file for both reading 
	//and writing. If the given file exists, its contents are destroyed.
	m_fp = fopen( filename, "w+" );//

	// make sure everything went well
	if( m_fp == NULL )
	{
		throw cFileCannotCreate( filename );
	}

	m_filename = string( filename );
}

void cFile::Append( const char* filename )
{
	// Create the file handle
	m_fp = fopen( filename, "ab" ); //append binary data to the open file

	// make sure everything went well
	if( m_fp == NULL )
	{
		throw cFileCannotCreate( filename );
	}

	m_filename = string( filename );
}

void cFile::Open( const char* filename )
{
	// Create the file handle
	m_fp = fopen( filename, "r+b" );//open a binary data file for both read and write

	// make sure everything went well
	if( m_fp == NULL )
	{
		throw cFileCannotCreate( filename );
	}

	m_filename = string( filename );

}


void cFile::Close()
{
	if( m_fp == NULL )
		return; // the file is already closed

	assert( 0 == fclose( m_fp ) );

//	if( 0 != fclose( m_fp ) )
//	{
//		DP("Error in cFile::Close\n");
//	}

	m_fp = NULL;
}



bool cFile::Exists( const char* filename )
{
	// try to open the file.  if we can't, it must not exist
	cFile temp;

	temp.m_fp = fopen( filename, "r" );//we can't open a file if it not exist when use "r"

	// make sure everything went well
	if( temp.m_fp == NULL )
	{
		return false;
	}
	temp.Close();
	return true;
}


void cFile::ReadBuff( void* pBuffer, int buffSize )
{
	if( m_fp == NULL )
		return; // error

	//int numRead = fread( pBuffer, buffSize, 1, m_fp );
	int numRead = fread( pBuffer, 1, buffSize, m_fp );

	if( numRead != buffSize )
	{
		if( 0 != feof( m_fp ) ) //feof(m_fp) is true(not 0),so it is the end of file
		{
			throw cFileEOF();
		}
		else if( ferror( m_fp ) )  //error occured when operate
		{
			throw cFileReadError();
		}
		else
		{
			int foo = 0;     //is this make any sense?
		}
	}
}


void cFile::WriteBuff( void* pBuffer, int buffSize )
{
	if( m_fp == NULL )
		return; // error

	int numWritten = fwrite( pBuffer, buffSize, 1, m_fp );

	if( numWritten != buffSize )
	{
		throw cFileWriteError();
	}
}


/**
 * Not overrun safe
 */
void cFile::ReadLine( char* pBuffer )
{
	char currChar;
	bool done = false;

	int nRead = 0;
	while( !done )
	{
		try
		{
			ReadBuff( &currChar, 1 );
			nRead++;
		}
		catch( cFileEOF )
		{
			// break from the loop, we reached the end-of-file.
			if( nRead == 0 )
			{
				/**
				 * We started at the EOF and can't read a single line.
				 */
				throw;
			}
			break;
		}

		
		if( currChar == '\0' || currChar == '\n' )  //is '\0' means the end of a line? 
		{
			break;
		}
		*pBuffer++ = currChar;
	}
	// end the string with a \n\0
	*pBuffer++ = '\n';
	*pBuffer++ = '\0';
}

/**
 * Overrun safe
 */
string cFile::ReadLine()
{
	char currChar;
	bool done = false;

	int nRead = 0;

	string out;

	while( !done )
	{
		try
		{
			ReadBuff( &currChar, 1 );
			nRead++;
		}
		catch( cFileEOF )
		{
			// break from the loop, we reached the end-of-file.
			if( nRead == 0 )
			{
				/**
				 * We started at the EOF and can't read a single line.
				 */
				throw;
			}
			break;
		}

		if( currChar == '\0' || currChar == '\n' )
		{
			break;
		}

		out += currChar;
	}
	return out;
}


void cFile::ReadNonCommentedLine( char* pBuffer, char commentChar )
{
	char buff[1024];
	buff[0] = 0;
	while( 1 )
	{
		ReadLine( buff );
		if( buff[0] != commentChar )
			break;
	}
	strcpy( pBuffer, buff );
}
/*
标记下一行 
*/

void cFile::TokenizeNextNCLine( queue< string >* pList, char commentChar )
{
	string str;
	//先清空队列
	while(!pList->empty())
	{
		pList->pop();
	}
	while(1)
	{
		str = ReadLine();
		if( str[0] != commentChar )
			break;
	}

	// now curr has our string.
	// first, strip off any comments on the end.
	//DP1("parsing [%s]\n", str.c_str() );
	unsigned int commentLoc = str.find( commentChar, 0);
	if( commentLoc != str.npos )
	{
		str = str.erase( commentLoc );
	}

	char sep[] = " \t\n\r";

	unsigned int tokStart = str.find_first_not_of( (char*)sep, 0 );
	unsigned int tokEnd;
	while( tokStart != str.npos )
	{
		tokEnd = str.find_first_of( (char*)sep, tokStart );
		string token = str.substr( tokStart, tokEnd-tokStart );
		pList->push( token );

		tokStart = str.find_first_not_of( (char*)sep, tokEnd );
	}
}
