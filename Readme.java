/** @mainpage How to use
 *  

Target
------

The Code Library project creates standardized and re-usable source code
to support cross-product development and increase efficiency and speed in
product development.

Description
-----------

The Code Library enhances the GUI Library by providing
supplementary source code as an extension of the pure graphical assets. The
GUI Library developed by PDE contains user interface elements as
interlinked packages of graphical elements, documentation, and
demonstrators. The Code Library contains implementation for standard
elements of graphical user interfaces.

Development
-----------

The components are developed in collaboration with an external
software development agency. The development is based on the agile
software development methodology.

How to contribute
-----------------

Direct contribution is not supported at the moment. All git repositories
are read-only, all branches are protected. Pushing to the repository is
enabled for release managers only.

Helpdesk
---------

Please send your bug reports, change requests and other feedback to the
design helpdesk
(E-mail: design-support@telekom.de - Phone: +49 (0) 228 181-22222).

The Code Library
===================

Structure
---------

1. `PDECodeLibrary_Android` - The library, including source code, minimal
set of resources and doxygen file to generate documentation.
2. `PDECodeSamples_Android` - The sample project for Android.
3. `PDECodeLibrary_Android_Docu` - The documentation of the Android library
in html. Auto-generated from the doxygen file. The documentation is also
available online.


Getting the Library
---------------

You can clone the repositories using a git tool. Use 
commands such as

    git clone https://git.design.telekom.com/git/PDECodeLibrary_Android.git
    git clone https://git.design.telekom.com/git/PDECodeSamples_Android.git
    git clone https://git.design.telekom.com/git/PDECodeLibrary_Android_Docu.git

In addition, modern development environments support sharing source code
with git. Refer to the manual of your tool and enter the URL in the
configuration.

If you get an SSL certificaate problem, turn off the ssl verification for
git. You have two option to turn it off:

    git config --global http.sslVerify false
    export GIT_SSL_NO_VERIFY=true
    
Branches
--------

The latest version is available in the master branch. There are no other
branches specified. Branches to support specific versions of Android might be
available in the future.

Installation
============

System requirements
-------------------

There are no specific requirements for using the library and
the sample. You need a development environment for developing Android
applications. This includes the correct version of the SDK supporting your
Android target. You also need a web browser to view the
documentation online or offline.

Dependencies
------------

The library was built for Android 4.1.2 (API16). You need this version of
Android to build the library. You can include the pre-compiled library for
older versions of Android. There are no other dependencies.

The samples were build for Android 2.3.3 (API10) or higher. To build and run
the samples, you need the library. The bundle contains a project file with
the correct use of the library in Eclipse. Without the library, you can only
browse the source code.

Note for Mac-users: If you are running OS X Mountain Lion (OS 10.8.x) you may
need to additionally install the latest Java Plug-in to use the components.

The documentation has no dependencies.

Installing the sample
--------------------------

The sample does not require any installation. Just get a clone of the
sample and run it in your Eclipse.

Note: The sample will not build without getting a clone of the library as
well.

Installation in Eclipse
---------------------------

Creating a Library Project

1. Open "File", then select "New" and then "Project".
2. Unfold "Android" and select "Android Project from Existing Code".
3. Select "Next".
4. Enter the root directory: Navigate to the folder "PDECodeLibrary_Android"
and select the sub-folder "PDECodeLibrary".
5. The project appears. Select the checkbox. If the checkbox is disabled,
check if you already have a project with the same name.
6. Select "Finish".

Creating the Sample Project

1. Open "File", then "New" and then "Project".
2. Unfold "Android" and select "Android Project from Existing Code".
3. Select "Next".
4. Enter the root directory: Navigate to the folder "PDECodeSamples_Android"
and select the sub-folder "PDECodeSamples".
5. The project appears. Select the checkbox. If the checkbox is disabled,
check if you already have a project with the same name.
6. Select "Finish".

You should now be able to build and run the sample in the emulator or on
your device, if connected.

Installation in IntelliJ
------------------------

Creating the Library Project

1. Open "File" and select "New Project".
2. Select "Create project from existing sources" and select "Next".
3. Enter the project root. Select the project files location and PDECodeLibrary folder.
4. The name of the project is selected. Select "Next".
5. A list with two entries ("gen" and "src" folder) appears. Select "Next".
6. A list with two entries ("classes.jar" and "pdecodelibrary.jar") in the
library and one entry in the library contents ("classes.jar") appears. Select "Next".
7. A list with one entry in the modules ("PDECodeLibrary") appears. Select "Next".
8. Several frameworks are detected in the project
(Android - AndroidManifest.xml file)- Select "Finish".

Creating the Sample Project

1. Open "File" and select "New Project".
2. Select "Create project from existing sources" and select "Next".
3. Enter the project root. Select the project files location and PDECodeSamples folder.
4. The name of the project is selected. Select "Next".
5. A list with two entries ("gen" and "src" folder) appears. Select "Next".
6. A list with one entry ("classes.jar") in the library and one entry in
the library contents ("classes.jar") appears. Select "Next".
7. A list with one entry in the modules ("PDECodeSamples") and one entry
in the module dependencies ("classes.jar") appears. Select "Next".
8. Several frameworks are detected in the project (Android -
AndroidManifest.xml file). Select "Finish".
9. A popup appears to import Android dependencies from property files
(Import library module PDECodeLibrary from path....). Add the dependency
"PDECodeSamples" to "PDECodeLibrary". Select the checkboxes. Select "OK".

You should now be able to build and run the sample in the emulator or on
your device, if connected.

Installing the Documentation
---------------------------------

The latest version of the documentation is available online at
https://www.design.telekom.com/code-library/

For offline use, simply clone the documentation to your favorite folder. Go
to the root directory of your project (the one containing src, classes and
so on) and clone the documentation into this folder. To avoid that the folder
will be named after the repository, provide the target folder as parameter in the
clone command.

Example to clone to the folder "./doc"

    git clone https://git.design.telekom.com/git/PDECodeLibrary_Android_Docu.git doc

Uninstallation
===============

Uninstalling the Library
-----------------------------

The library does not require specific uninstallation. Simply remove the
folder PDECodeLibrary_Android from your local hard drive.

Be aware of dependencies from your projects to the library. Remove all links
to the library from your project settings.

Uninstalling the Samples
------------------------------

The samples do not require specific uninstallation. Simply remove the folder
PDECodeSamples_Android from your local hard drive.

Uninstalling the Documentation
------------------------------------

Simply remove the folder from your local hard drive.
*/