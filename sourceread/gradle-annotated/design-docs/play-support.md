
This specification outlines the work that is required to use Gradle to build applications that use the [Play framework](http://www.playframework.com).

# Use cases

There are 3 main use cases:

- A developer builds a Play application.
- A developer runs a Play application.
- A deployer runs a Play application. That is, a Play application is run in a production environment.

## Out of scope

The following features are currently out of scope for this spec, but certainly make sense for later work:

- Building a Play application for multiple Scala versions. For now, the build for a given Play application will target a single Scala version.
  It will be possible to declare which version of Scala to build for.
- Using continuous mode with JVMs older than Java 7. For now, this will work only with Java 7 and later will be supported. It will be possible to build and run for Java 6.
- Any specific IDE integration, beyond Gradle's current general purpose IDE integration for Java and Scala.
- Any specific support for publishing and resolving Play applications, beyond Gradle's current general purpose capabilities.
- Installing the Play tools on the build machine.
- Bootstrapping a Play project.
- Migrating or importing SBT settings for a Play project.

# Implementation plan

## Developer compiles Java and Scala source for Play application

Developer uses the standard build lifecycle, such as `gradle assemble` or `gradle build` to compile the Java and Scala source for a Play application.

- Introduce a 'play' plugin.
- Adapt language plugins conventions to Play conventions
    - need to include unmanaged dependencies from `lib/` as compile dependencies
    - sub-projects have a slightly different layout to root project

TBD - support the new language plugins?

## Developer compiles route and template source for Play application

Extend the standard build lifecycle to compile the route and template source files to bytecode.

- Compile routes and templates
- Define source sets for each type of source file
- Compilation should be incremental and remove stale outputs

## Developer compiles assets for Play application

Extend the standard build lifecycle to compile the front end assets to CSS and Javascript.

- Compiled assets
    - Coffeescript -> Javascript
    - LESSCSS -> CSS
    - Javascript > Javascript via Google Closure
    - Javascript minification, requirejs optimization
- Include the `public/` assets in the Jar
- Define source sets for each type of source file
- Compilation should be incremental and remove stale outputs
- Expose some compiler options

TBD - integration with Gradle javascript plugins.

## Developer builds and runs Play application

Introduce some lifecycle tasks to allow the developer to run or start the Play application. For example, the
developer may run `gradle run` to run the application or `gradle start` to start the application.

- Add basic server + service domain model and some lifecycle tasks
- Model Play application as service
- Lifecycle to run in foreground, or start in background, as per `play run` and `play start`

## Developer builds Play application distribution

Introduce some lifecycle tasks to allow the developer to package up the Play application. For example, the
developer may run `gradle stage` to stage the local application, or `gradle dist` to create a standalone distribution.

- Build distribution image and zips, as per `play stage` and `play dist`
- Integrate with the distribution plugin.

## Long running compiler daemon

Reuse the compiler daemon across builds to keep the Scala compiler warmed up. This is also useful for the other compilers.

## Keep running Play application up-to-date when source changes

Add a general-purpose mechanism which is able to keep the output of some tasks up-to-date when source files change. For example,
a developer may run `gradle --watch <tasks>`.

- Gradle runs tasks, then watches files that are inputs to a task but not outputs of some other task. When a file changes, repeat.
- Monitor files that are inputs to the model for changes too.
- When the tasks start a service, stop the service(s) before rebuilding.

## Developer views compile and other build failures in Play application

Adapt compiler output to the format expected by Play:

- Model configuration problems
- Java and scala compilation failures
- Asset compilation failures
- Other verification task failures?
