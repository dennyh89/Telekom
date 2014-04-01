/** @mainpage How to use
 *  

The Code Library
================

The Code Library project creates standardized and reusable source code to
support cross-product development and increase efficiency and speed in
product development.

Description
-----

The Code Library is a platform for sharing software components to build
graphical user interfaces. The components developed by Deutsche Telekom
Product Design implement standard elements of graphical user interfaces to
support a consistent user experience and efficient development across Telekom
products.

The Code Library is available for Android, iOS and HTML. All software
components are provided in source code with examples of application and API
documentation.

The components are developed in collaboration with an external
software-development agency. The development is based on the agile
software-development methodology.

How to use
-----

The Code Library supports user-interface developments on all levels from just
reviewing the code to full migration of your user-interface developments in
order to use the Code Library components.

### Best practice ###

Review the source code available in the Code Library to identify ways to
implement visual appearance and behavior in your user-interface development.
Reuse experience and knowledge from other developers. Keep your build path
unmodified.

Recommendation: use the Code Library as best practices if you are looking for
a solution to a small part of your user interface that is not worth the
effort of fully integrating the Code Library.

### Utilizing of code from the Code Library ###

Review the source code available in the Code Library to identify code
fragments that help you solve specific issues. Extend classes available in
the Code Library, derive your user-interface development from the components
or use modified code from the components in your user-interface developments.
Keep your build path unmodified.

Recommendation: utilize code from the Code Library into your user-interface
developments if your project has specific requirements such as a different
style or behavior.

### Use of the Code Library in conjunction with your existing implementation
###

Integrate the Code Library into your build path. Use components from the Code
Library to implement new features and behavior. Keep the components you
already have developed unmodified.

Recommendation: use the Code Library in conjunction with your developed
components if your project is already in an advanced stage but still has a
substantial set of user-interface developments open.

### Full switch to the Code Library ###

Integrate the Code Library into your build path. Use the components for all
new user-interface developments. Modify your existing source code to replace
components you have already implemented with the components from the Code
Library.

Recommendation: use all of the Code Library when you start a new project
from scratch. Because of the effort required to rewrite your existing source
code, we recommend a full migration if the development is in the early stages
with few components already implemented.

Version control system
-----

The content within the Code Library is continuously updated and expanded. The
updates and previous versions are managed by the version control system "Git."

The server is "git.design.telekom.com". The protocol is HTTPS. The Git-system
and the Experience Toolbox use Single Sign On (SSO). Please use your
Experience Toolbox e-mail and password to access the repositories.

We recommend integrating the git-repositories directly into your developer
tools like Eclipse and IntelliJ. Refer to your tool manual and enter the URL
in the configuration. If that does not work, you can use a simple Git-client
such as [Git-Extensions](http://code.google.com/p/gitextensions/) or
[Tortoise-Git](http://code.google.com/p/tortoisegit/).

Note: the system is not configured to handle access to the repositories using
a Web-browser. Zip files are also not provided because of the missing update
process after a download.

For more information, see the
[Experience Toolbox FAQ](https://www.design.telekom.com/meta/faq/).

Terms of use
-----

The content of the Code Library can be used for required software
developments for Deutsche Telekom products in accordance with the terms of
use of the Experience Toolbox Deutsche Telekom. Full compliance with these
terms is mandatory. They are available in the Experience Toolbox at:
Account > Terms of Use & Imprint.

### Licensing information - Telegrotesk Font ###

For licensing reasons, only employees of the Deutsche Telekom Group are
authorized to download the typefaces free of charge. They must not be passed
on to third parties.

All agencies, subcontractors, business partners, etc. are required to
purchase the typefaces from URW++.

URW++ Design & Development<br />
Poppenbuetteler Bogen 36<br />
22399 Hamburg, Germany<br />
Tel. +49 40 60605 0<br />
E-mail: info@urwpp.de<br />
www.urwpp.de (IdentiType)

User authorization
-----

The Git-system and the Experience Toolbox use Single Sign On (SSO). Please
use your Experience Toolbox e-mail and password to access the repositories.

Special permission is not required. All Deutsche Telekom employees and the
national companies requiring the Code Library for their work can register at
http://design.telekom.com/register/.

How to contribute
-----

A direct contribution to further develop the Code Library is currently not
possible. All repositories are read-only, and all branches are protected.
Write-access to repositories within the Git-system is only given to
authorized users.

If you want to modify or add source code considered valuable for a larger
community of user-interface developers, please contact the help desk.

Help desk
-----

Please send your bug reports, change requests and other feedback to the
design help desk.<br />
E-mail: design-support@telekom.de<br />
Phone: +49 (0) 228 181-22222

The Code Library for Android
============================
                
Getting the Code Library
---

You can clone from three repositories:

1. The library for Android, including source code and minimal set of
resources: https://git.design.telekom.com/git/PDECodeLibrary_Android.git
2. The sample project for Android:
https://git.design.telekom.com/git/PDECodeSamples_Android.git
3. The documentation of the Android library in html for offline use:
https://git.design.telekom.com/git/PDECodeLibrary_Android_Docu.git <br />
The documentation is also available online as part of the Experience Toolbox.

Modern development environments support sharing source code with Git. Please
refer to the manual of your tool and enter the URL in the configuration.
    
Branches
--------

The master branch of the Git-system contains the latest version of the software
components. Currently, the Code Library version 3.0 is available in the master
branch. Previous versions are available in specific branches.

The Code Library versions 1.0 and 2.0 are available in the branches
"release-X.Y". The branch can be cloned directly by using the option "-b".
Please replace "X" with the major release number and "Y" with the minor release
number.

Example

   git clone -b release-3.0 https://git.design.telekom.com/git/PDECodeLibrary_Android.git
   git clone -b release-3.0 https://git.design.telekom.com/git/PDECodeSamples_Android.git

Installation
============

The Code Library and code samples for Android include project files for
easy integration into Eclipse. The preceding sections list the steps to
integrate the Code Library into Eclipse and IntelliJ. For the use of other
development environments, please refer to your tool's manual on how to work
with external libraries and existing source code.

System requirements
---

There are no specific requirements for using the Code Library and samples.
You need a development environment for developing Android applications.
This includes the correct version of the SDK that supports your Android
target. You also need a web browser to view the documentation online or
offline.


Dependencies
---

The Code Library was built for Android 4.4 (API19). Since version 4.0
the PDECodeLibrary depends on ActionBar Compat, which replaces the
ActionBarSherlock used in previous versions. The ActionBar Compat is part of
the Android v7 Support Library. Please refer to
http://developer.android.com/tools/support-library/features.html#v7 for
details. You must have installed the Android v7 support library to re-compile
the Code Library for Android, v4.0.

The samples were built for Android 2.3.3 (API10) or higher. To build and run
the samples, you need the Code Library. The bundle contains a project file
with the correct use of the library in Eclipse. Without the library, you can
only browse the source code.

Note for Mac-users: if you are running OS X Mountain Lion (OS 10.8.x), you
may need to additionally install the latest Java plug-in to use the
components.

The documentation has no dependencies.

Installation in Eclipse
---------------------------

### Installing support libraries ###

Ensure you have installed the support libraries. You find the support libraries
in the SDK Manager, section "Extras". Select the checkbox "Support Library" and 
install the packages selected by the SDK Manager. Please refer to
http://developer.android.com/tools/support-library/setup.html for detailed
instructions and screenshot to install support libraries.

The library is located in the <sdk>/extras/android/support/v7/appcompat/
directory after you download the Android Support Libraries.

### Create a library project for the v7 support library ###
 
To include the v7 support library in your application project, follow the
instructions for adding libraries with resources at
http://developer.android.com/tools/support-library/setup.html#libs-with-res
from wich the following instructions are reproduced:

1. Select "File" -> "Import"
2. Select "Existing Android Code Into Workspace" and click "Next".
3. Browse to the SDK installation directory and then to the Support Library
folder, like <sdk>/extras/android/support/v7/appcompat/.
*Important note*: If you installed the sdk in a system folder, such as
C:/Program Files/Android/android-sdk, then you need to copy the library into
your workspace. Otherwise Eclipse will fail to generate the library files.
Select the checkbox "Copy projects into workspace" to do so.
4. Click "Finish" to import the project. For the v7 appcompat project, you
should now see a new project titled "android-support-v7-appcompat".
5. In the new library project, expand the "libs/" folder, right-click each
.jar file and select "Build Path" -> "Add to Build Path". For example, when
creating the the v7 appcompat project, add both the "android-support-v4.jar"
and "android-support-v7-appcompat.jar" files to the build path.
6. Right-click the library project folder and select "Build Path" ->
"Configure Build Path".
7. In the "Order and Export" tab, check the .jar files you just added to the
build path, so they are available to projects that depend on this library
project. For example, the appcompat project requires you to export both the
"android-support-v4.jar" and "android-support-v7-appcompat.jar" files.
8. Uncheck "Android Dependencies".
9. Click "Ok" to complete the changes.
    
### Creating a library project ###

1.	Open "File"; select "New" and then "Project".
2.	Expand "Android" and select "Android Project from Existing Code".
3.	Select "Next".
4.	Enter the root directory. Navigate to the folder
"PDECodeLibrary_Android", and select the subfolder "PDECodeLibrary".
5.	The project appears. Select the checkbox. If the checkbox is
disabled, check if you already have a project with the same name.
6.	Select "Finish".
7. Right click on the project and select "Properties".
8. Select section "Android" from the list on the left side.
9. Ensure that the checkbox "Is Library" is selected.
10. Click "Add" to open a selection of available libraries.
11. Select "android-support-v7-appcompat" from the list.
12. Click "Ok" to apply the selection.
13. Select a recent Project Build Target (e.g. Android 4.4.2)
14. Click "Ok" to complete the changes.

### Update the PDECodeLibrary project from v3.0 to v4.0 ###

If you have installed the PDECodeLibrary v3.0, then you need to remove the
dependency to ActionBarSherlock from your project settings.

1. Right click on the project and select "Properties".
2. Select section "Android" from the list on the left side.
3. Select the actionbarsherlock library in the right lower area.
4. Select "Remove".
5. Click "Ok" to complete the changes.

Pull the latest version of the CodeLibrary for Android, v4.0 from the master 
branch of the git. The submodule will be removed from the git-files. The
local files of the ActionBarSherlock are not removed because they might be
in use in your other projects. If this is not the case, you might want to
remove the files from the local filesystem by hand.

### Creating the sample project ###

1.	Open "File", then "New" and then "Project".
2.	Expand "Android", and select "Android Project from Existing Code".
3.	Select "Next".
4.	Enter the root directory. Navigate to the folder
"PDECodeSamples_Android", and select the subfolder "PDECodeSamples".
5.	The project appears. Select the checkbox. If the checkbox is
disabled, check if you already have a project with the same name.
6.	Select "Finish".
7.  Right click on the Project (in the Package Explorer) -> Properties
    Android: Select appcompat as reference.
    Select a recent Project Build Target (e.g. Android 4.4.2)
    Select PDECodeLibrary as reference.
    Apply 


You should now be able to build and run the sample in the emulator or on
your device, if connected. Maybe you need to do a Project -> Clean ... before.


Installation in IntelliJ
------------------------

### Creating the library project ###

1. Import Project
2. Select Directory to Import: PDECodeLibrary
3. Create project from existing sources -> Next
4. Keep "PDECodeLibrary" and the project location -> Next
5. Source files for your project have been found Dialog
Keep ...PDECodeLibrary (Android) and ...PDECodeLibrary/src (Java) both checked. -> Next
6. Libraries screen
Split the library content in individual libraries and name them according to the content. At the end you should see
2 separate library for android-support-v4.jar, android-support-v7-appcompat.jar. Selected -> Next
7. Please review suggested module structure for the project
Keep as it is -> Next
8. Please select project SDK. We suggest to use the most recent Android Platform. -> Next
9. Several frameworks are detected in the project.
Keep as it is -> Next
=> the Project will be created.
Now add Module support-v7
10. Open "File", and select "Import Module".
11.	In Select File or Directory to Import Dialog select support/v7/appcompat -> OK
12. Create project from existing sources -> Next
13. Source files for your project have been found Dialog
Keep ...appcompat (Android) checked. -> Next
14. Several frameworks are detected in the project.
Keep as it is -> Finish

15. Select "Project Structure" Button (or File->Project Structure) 
    Module -> appcompat (expand) -> Android: Make sure the check box "Library module" is selected
    Module -> PDECodeLibrary (expand) -> Android: Make sure the check box "Library module" is selected (Apply!)
    Module -> PDECodeLibrary -> Dependencies: Add - Module Dependency: appcompat
    -> Apply or OK


### Creating the sample project ###
(We assume you just created the library project as shown above)

1.	Open "File", and select "Import Module".
2.	In Select File or Directory to Import Dialog select "PDECodeSamples" -> OK
3.	Create module from existing sources -> Next
4. Source files for your project have been found Dialog
Keep ...PDECodeSamples (Android) and ...PDECodeSamples/src (Java) both checked. -> Next
5. Libraries screen
Unselect the libraries (we will add the libs from the PDECodeLibrary) selected -> Next
6. Please review suggested module structure for the project
Keep as it is -> Next
7. Several frameworks are detected in the project.
Keep as it is -> Finish
=> the Module will be created.
8. Select "Project Structure" Button (or File->Project Structure)
9. Select Module PDECodeSamples -> Dependencies: Add - Module Dependency: PDECodeLibrary 
   Add - Library: Select android-support-v4.jar & android-support-v7-appcompat.jar (the libraries you created in step 7 
   of the "Create the library project" section)

You should now be able to build and run the sample in the emulator or on
your device, if connected.

Installing the Documentation
---------------------------------
The latest version of the documentation is available online as part of the
Experience Toolbox. Please refer to
https://www.design.telekom.com/code-library/.

For offline use, simply clone the documentation repository to your favorite
folder. Go to the root directory of your project (the one containing src,
classes and so on), and clone the documentation into this folder. To avoid
the folder being named after the repository, provide the target folder as
the parameter in the clone command.

Example to clone to the folder "./doc"

    git clone https://git.design.telekom.com/git/PDECodeLibrary_Android_Docu.git doc
    
First Steps
-----------

To use the library, you must first initialize it:

1. Import the class `de.telekom.pde.codelibrary.ui.PDECodeLibrary`
2. Enter a new line into your App's or main Activity's `onCreate`-method:
			`PDECodeLibrary.getInstance().libraryInit(this);`

### Example initialization ###

	public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PDECodeLibrary.getInstance().libraryInit(this);
		
		setContentView(R.layout.activity_main);
		}
	}

You can now add the components either in your layout files or in the source
of your application.

### Example layout file ###

	<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:dt_codecomponents="http://schemas.android.com/apk/res/com.example.mystarterpackage"
    
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity" >

    <de.telekom.pde.codelibrary.ui.components.buttons.PDEButton
        android:id="@+id/pDEButton"
        android:layout_width="180dp"
        android:layout_height="@dimen/StandardButtonHeight"
        android:layout_centerHorizontal="true"
        android:layout_centerVertical="true"
        dt_codecomponents:button_color="@color/DTWhite"
        dt_codecomponents:icon_colored="true"
        dt_codecomponents:text="Selectable" >
    </de.telekom.pde.codelibrary.ui.components.buttons.PDEButton>

	</RelativeLayout>

### Example source code ###

	public class MainActivity extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PDECodeLibrary.getInstance().libraryInit(this);
		
		setContentView(R.layout.activity_main);

        final LinearLayout containerView = (LinearLayout)findViewById(R.id.buttonsample_container);

        final int buttonWidth = PDEBuildingUnits.pixelFromBU(17);
        final int buttonHeight = PDEBuildingUnits.pixelFromBU(3);
        final int marginBU = PDEBuildingUnits.pixelFromBU(1);
        

        final LinearLayout.LayoutParams btnLinearLayoutParams = new LinearLayout.LayoutParams(buttonWidth,buttonHeight);
        btnLinearLayoutParams.setMargins(marginBU,marginBU,marginBU,marginBU);
        
        final PDEButton pdeButton = new PDEButton(this);
        pdeButton.setTitle( "Coded PDEButton" );
        pdeButton.addListener(this, "dtButtonPressed", PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED);

        containerView.addView(pdeButton, btnLinearLayoutParams);
		}
	}

Uninstallation
===============

The preceding sections illustrate how to remove the Code Library, the samples
and the documentation from your development environment.

Uninstalling the Code Library
---

The library does not require specific uninstallation. Simply remove the
folder PDECodeLibrary_Android from your local hard drive.

Please consider the possible dependencies of your projects with the library.
Delete all such references to the library from your project settings when you
delete this library.

Uninstalling the samples
---

The samples do not require specific uninstallation. Simply remove the folder
PDECodeSamples_Android from your local hard drive.

Uninstalling the documentation
---

Simply remove the folder from your local hard drive.


Known issues
============

__Version 1.12.4__<br />
The icon font has problems with the visual appearance
if the size of the displayed icons varies.
   
Release notes
=============

__Current public version__<br />
4.0

__Current non-public version__<br />
4.0

Major 4.0 (public)
--------------------

__Date__<br />
01.04.2014
 
__Server__<br />
git.design.telekom.com
 
__Repositories__<br />
PDECodeLibrary_Android.git, PDECodeSamples_Android.git, PDECodeLibrary_Android_Docu.git
 
__Branch__<br />
master
 
__Content__<br />

The Code Library version 4.0 adds dialogs to the Code Library. A set of
error messages approved by language department has been added unifying
standard error dialogs.

The Code Library version 4.0 replaces the ActionBarSherlock submodule with
the use of ActionBar Compat. The submodule has been removed. You must install
Android support v7 library, e.g. by the SDK Manager.

Feature 4.0 (closed beta)
---------------------------

ActionBarSherlock removed from git and project files.

Patch 3.10.2 (non-public)
---------------------------

PDEDialog: texts updated
    
The language department has modified the dialog titles and texts. All new
strings have been added to the Dialog.string files.
    
Patch 3.10.1 (non-public)
---------------------------

PDEDialog: initializing / caching of dictionaries implemented

Feature 3.10.0 (non-public)
---------------------------

PDEDialog: Moved defines from PDEDialogActivity to PDEDialog to avoid
unnecessary imports of PDEDialogActivity.

Feature 3.9.0 (non-public)
---------------------------

PDELayerTextView renamed to PDETextView

Feature 3.8.0 (non-public)
---------------------------

ActionBar: ActionBarSherlock replaced with ActionBar Compat

The ActionBarSherlock is not supported anymore. It got replaced with
ActionBar Compat.

Patch 3.7.6 (non-public)
---------------------------

convertToPosition now uses PDEString.stringToPoint

Patch 3.7.5 (non-public)
---------------------------

Metphores onMeasure logic fixed

Patch 3.7.4 (non-public)
---------------------------
    
Removed outer shadow from dialogs.

Patch 3.7.3 (non-public)
---------------------------

PDELayerText: Support for new line \n added

Patch 3.7.2 (non-public)
---------------------------

PDELayerTextView: Basis canged from View to ImageView to enable for more
comfortable set of a background drawable behind the text.

Patch 3.7.1 (non-public)
---------------------------

Login: Use of PDEDialogs.

Feature 3.7.0 (non-public)
---------------------------

PDEDialog: ScrollView added.

The message is added to a ScrollView in order to fix visual appeareance on
small screens.

Patch 3.6.1 (non-public)
---------------------------

PDEDialog: Max width set.

Two constraints determine the width of the PDEDialog:
(a): The dialog must have a padding to the screen border of 2BU on the left
and right.
(b) The dialog must not exceed a total width of 26BUs.

The first constraint is default on most phones, the second has been added in
particular for the use on large screens and tablets.

Feature 3.6.0 (non-public)
---------------------------

PDEDialog: visual update

Special caseadded without message string.

Feature 3.5.0 (non-public)
---------------------------

PDEDialog: Custom dialog added

The custom dialog aloows to set title, text, and buttons at runtime.

Feature 3.4.0 (non-public)
---------------------------

PDEDialog: standard messages implemented

Texts, titles and standard buttons have been added for pre-defined standrad
dialogs. Localised files for 55 standard error messages added in German and
English. All texts phrases have been approved by language department.

Feature 3.4.0 (non-public)
---------------------------

Implementation of PDEDialogs.

Feature 3.3.0 (non-public)
---------------------------

OneIDMModule removed

Patch 3.2.1 (non-public)
---------------------------

PDEButton: collect hints in clearButtonLayerForLayerId 

Feature 3.2.0 (non-public)
---------------------------

Slider: handler is now set automatically when content is set. Redundant calls
of setHandler removed from examples.

Patch 3.1.1 (non-public)
---------------------------

PDELayerTextView: BU logic fixed

Feature 3.1.0 (non-public)
---------------------------

Metaphors: Remodeled multilayer.

Major 3.0 (public)
--------------------

__Date__<br />
13.12.2013
 
__Server__<br />
git.design.telekom.com
 
__Repositories__<br />
PDECodeLibrary_Android.git, PDECodeSamples_Android.git, PDECodeLibrary_Android_Docu.git
 
__Branch__<br />
release-3.0
 
__Content__<br />

The Code Library version 3 implements the major update of the visual design
for Deutsche Telekom Apps. The components are available in the styles "flat"
and "haptic" The other stiles of previous versions are deprecated.

Patch 2.0.4 (non-public)
------------------------

Minor list improvements and list documentation updates.

Patch 2.0.3 (non-public)
------------------------

Minor slider improvements.

Patch 2.0.2 (public)
--------------------

__Date__<br />
27.08.2013

__Server__<br />
git.design.telekom.com

__Repositories__<br />
PDECodeLibrary_Android.git, PDECodeSamples_Android.git

__Branch__<br />
release-2.0

__Content__<br />

hotfix: colors of dark style and text layer fixed

Some colors of dark style were not set, some were not correkt. Switching
from light to dark style would have lead to incorrect visual
appearenace. The colors have been fixed, in code as well as XML settings.

The handling of long strings inside the text components has been fixed.

Patch 2.0.1 (public)
-------------------

__Date__<br />
01.08.2013

__Server__<br />
git.design.telekom.com

__Repositories__<br />
PDECodeLibrary_Android.git, PDECodeLibrary_Android_Docu.git

__Branch__<br />
release-2.0

__Content__<br />

hotfix-2.0.1: corner radius of buttons fixed

The feature of setting the corner radius of the buttons was not
implemented correctly.

In XML, it was available as float instead of dimension. In code, corner
radius was implemented using a String. This could lead to crashes in
several APIs.

The corner radius has been corrected for XML as well as in code.

Major 2.0 (public)
-------------------

__Date__<br />
30.07.2013

__Server__<br />
git.design.telekom.com

__Repositories__<br />
PDECodeLibrary_Android.git, PDECodeSamples_Android.git,
PDECodeLibrary_Android_Docu.git

__Branch__<br />
release-2.0

__Content__<br />

The Code Library version 2.0 contains the __components__ for Buttons,
Checkboxes, Radiobuttons, Inputfields, and Lists.

It also contains __activities__ for splash screen, actionbar, and login
(app-login and login connetected with OneIDM-service).

Additionally, it contains the __graphical elements__ to build Boxes,
Notification frames (Tooltip), , Listheader, Scrollbars, Progressbars,
Methaphors, Icons and Tele IconFont, Text, Delimiters, Shadows, and Areas.

Feature 1.12.11 (closed beta)
----------------------------

lists: Two line list added

The two line list has been added as PDEListPlainGraphicTwoLine.

Feature 1.12.10 (non-public)
---------------------------

list: More lists added.

A set of single line lists have been added.
PDEList Plain Graphic Single Line Small / Medium / Large

Feature 1.12.9 (non-public)
---------------------------

login: progress indicator

The dialog has been removed from the progress indicator.
The progress indicator is just the wheel to display that the system is working.
The user has the option to abort the progress indicator with the back button.
The user returns to the login screen activity then. If a token request is still
active, then the imput fields and login button are disabled.

Feature 1.12.8 (non-public)
---------------------------

login: exception handling improved

Exception handling for MalformedURLException, JSONException, and IOException
added.

The first two displey the default error dialog, the latter one display the
network connection error dialog.

Feature 1.12.7 (non-public)
---------------------------

login: behavior modified

The OneIDM Login screen has been reworked. The link "Telekom Dienste" has been
changed to a tooltip. The label of the id-input field has been changed to
"Benutzername". A ttoltip has been added to the id-input field to explain the
different variants of "Benutzername" to the user.

Feature 1.12.6 (non-public)
---------------------------

error messages: dialags added to display message to the user

A set of dialogs has been added to the login module. After pressing the Login-
Button, a progress indocator appears. The progress indicator has been
implemented as dialog with title, text, and Android-wheel. The error messages
have been implemented as Ok-Dialogs with title, text and Call-to-Action
Button "Schliessen".

Feature 1.12.5 (non-public)
---------------------------

samples: order changed

Order of samples changed to alphabetically.

Feature 1.12.4 (closed beta)
----------------------------

Input field: clear function added

Remove text function of input fields fixed. The field uses a gray square
marked with an "x" icon from the iconfont to remove the text in an input
field.

Feature 1.12.3 (non-public)
----------------------------

login: responsive behavior

The login now adapts to the screen size. If the screen is smaller than 30BUs,
then the width of the login view is removed. If the screen is larger, then
the width is set to 30BU and the view is central aligned on the screen.


Feature 1.12.2 (non-public)
----------------------------

icon font: workaround

Workaround for icon fonts added.

Feature 1.12.1 (non-public)
----------------------------

viasual: dark-style added

Dark style added for the samples.

Feature 1.12.0 (non-public)
---------------------------

structure: the login moved to the library

The login module has been moved from the experimental screens in the samples
to the library.

Feature 1.11.1 (non-public)
-----------------------

actionbar: SlidingMenu library removed

The sliding menu using the 3rd party library was removed with Google's native
sliding menu.

Feature 1.11.0 (non-public)
---------------------------

structure: actionbar moved to the library

The actionbars have been integrated into the library.

Feature 1.10.5 (non-public)
---------------------------

components: text link

The text link is a text element that behaves like a button. When the text
element is activated, the text link creates an event in order to trigger the
handler to process the user input.

Feature 1.10.4 (non-public)
-----------------------

components: list header

The list header is a composition of a centralized header text and a
horizontal delimiter. It is available in the graphical elements.

Feature 1.10.3 (non-public)
---------------------------

actionbar: spinner actionbar styled

The drop down panel used by the spinner, the overflow and the context menu
received a frame. In addition, the spinner has been extracted as a single
component for the library.

Feature 1.10.2 (non-public)
---------------------------

actionbar: behavior and visual appearance updated

All controls used for the Android action bar of Deutsche Telekom provide
interaction feedback to the use by reduction of brightness of the background
color of the control; at time of implementation by 15%.

All text elements used for Android action bar for Deutsche Telekom use the
default font of Deutsche Telekom; at time of implementation Telekgrotesk Nor.

The action overflow of the Android action bar of Deutsche Telekom uses
standard colors, delimiters, separation from content area, interaction
feedback, font and drop down.

The action bar uses the overflow icon provided by Google; at time of
implementation three squares stacked on top of each other.

All vertical delimiters used in the Android action bar of Deutsche Telekom
use standard color and thickness.

Feature 1.10.1 (non-public)
---------------------------

actionbar: visual appearance modified

The Android action bar of Deutsche Telekom uses color, gradient and shadow
equal to the definition of the Medien center app of Deutsche Telekom.

All delimiters use color and thickness according to Styleguide of Deutsche
Telekom; at time of implementation RGB 208208208 and 1px.

The action bar is separated from the content area by a horizontal standard
delimiter.

All drop downs use background color according to Styleguide of Deutsche
Telekom; at time of implementation 237237237. Additionally it uses the
standard font and horizontal delimiter between items.

Feature 1.10.0 (non-public)
-----------------------

activities: actionbars

A set of actionbars has been implemented: Standard actionbar, splitted
actionbar, spinner actionbar, overflow actionbar, sliding menu, and multi-
select listview.

The implementation reproduces the look&feel and behavior of the standard
actionbar to large extend. The implementation uses XML-styling as much as
possible. Implementation and adaptation in Java is second option if
realization with XML is not sufficient in terms of design, style or
behavior; or not feasible in terms of economic, legal or time issues.

Feature 1.9.0 (non-public)
-----------------------

activities: splash screen

The splash screen is the first screen of the application. It is
shown right after activation, the launch of the application is 
performed in background. The splash screen informs the user that
the application has successfully been started. The design of the splash
screen enables the user to link the application with Deutsche Telekom.

The splash screen of Deutsche Telekom uses standard colors, font and layout.

Feature 1.8.0 (non-public)
--------------------------

sample: telegrotesk test screen

The central vertical alignment of font Telegrotesk 2.7 is
incorrect. A test screen has been added to illustrate
* the placement of native text in native components,
* the placement of text in telegrotesk in native components, and
* the placement of text in telegrotesk in PDE components.

The test screen will also enable the font developers to test fixes.

Patch 1.7.3 (non-public)
------------------------

Inner and outer shadows added to the section `Graphical Elements Samples`.

Patch 1.7.2 (non-public)
------------------------

Improvements on photo, music and video metaphors.

Patch 1.7.1 (non-public)
------------------------

Icon font wrapper completed.

Feature 1.7.0 (non-public)
--------------------------

experimental: login module

The current components have been used to implement a login module. It is a
composition of list header, text labels, text links, text input fields,
checkbox, button, horizontal delimiter.

An example of the login module has been added to the PDECodeSamples_Android
app in an new area called `Experimental`.

Improvement 1.6.0 (non-public)
------------------------------

common: font update to Telegrotesk 2.7

The font in version 1.0 and higher was behind the Telegrotesk from
developments by 1 minor revision. The font of all elements and components has
been updated to Telegrotesk 2.7.

Feature 1.5.0 (non-public)
--------------------------

components: input fields added

A set of input fields have been added to the library: Text input field,
search input field, password input field, an email input field. The
components are available inside the package
`de.telekom.pde.codelibrary.ui.components.inputfields`.

Samples of the input fields are available in the area `PDE Text Samples` of
the PDECodeSample_Android app. Four samples are implemented: the illustration
of the events, the sample comparing the PDE input fields with native Android
version, the showcase with the four different fields, and an example of
resizing the input fields.
 
Feature 1.4.0 (non-public)
--------------------------

components: list added

The list is added as a single component. It is a complex component containing
different components such as texts, buttons, checkboxes or icons.

The list is available in the samples inside the developer area as sample
`Styled Button List`. It will move to the samples after completion.

Feature 1.3.0 (non-public)
--------------------------

common: iconfont wrapper added

A wrapper for the icon font has been implemented. The icon font is a set of
default icons implemented as font. The wrapper enables to use the
icon font in texts. Opposed to graphical icons, the icons used from the icon
font automatically scale to the size of a text like other fonts do. 

The wrapper is illustrated in the samples in the section `Graphical
Elements Samples`.

Feature 1.2.0 (non-public)
--------------------------

common: set of elements extended

Implementations of graphical elements for music, file, video and photo
metaphors have been added.

The elements are illustrated in the samples in the section `Graphical
Elements Samples`.

Feature 1.1.0 (non-public)
--------------------------

common: set of elements extended

The set of graphical elements has been extended with implementations for
several bars. The elements for scrollbar indicative/interactive (both
horizontal as well as vertical), and progressbar have been added. The
handles for scrollbars and progressbars have been additionally implemented.
The new elements are available in the package
`de.telekom.pde.codelibrary.ui.elements.complex`.

The elements are illustrated in the samples in the section `Graphical
Elements Samples`.

Patch 1.0.2  (public)
---------------------

__Date__<br />
14.12.2012

__Server__<br />
git.design.telekom.com

__Repositories__<br />
PDECodeLibrary_Android.git, PDECodeSamples_Android.git,
PDECodeLibrary_Android_Docu.git

__Branch__<br />
release-1.0.2

config: project.properties added

The file project properties were added to support creation of a new
project with the library. The properties to set are:

	android.library=true
	target=android-16
    
Patch 1.0.1 (public)
--------------------

__Date__<br />
14.12.2012

__Server__<br />
git.design.telekom.com

__Repositories__<br />
PDECodeLibrary_Android.git, PDECodeSamples_Android.git,
PDECodeLibrary_Android_Docu.git

__Branch__<br />
release-1.0.2

hotfix: fix of setAlpha for checkboxes

The call of setAlpha(float) was introduced in Android API11. The right call
for API10 is setAlpha(int). Without the fix, the app will crash on Android 2.3
devices.

In addition, the call of setAlpha(int) was deprecated in API17. Another
fix has been implemented in order to be future save.

Major 1.0 (public)
------------------

__Date__<br />
14.12.2012

__Server__<br />
git.design.telekom.com

__Repositories__<br />
PDECodeLibrary_Android.git, PDECodeSamples_Android.git,
PDECodeLibrary_Android_Docu.git

__Branch__<br />
release-1.0.2

__Content__<br />

Version 1.0 of the code library contains all buttons, checkboxes and
radiobuttons.

Feature 0.9.0 (non-public) 
--------------------------

components: radiobuttons added

The implementation for radiobuttons added. Like checkboxes, the radiobuttons
are implemented as an overlay on the standard button. The realization fully
complies with the specification of the Styleguide of Deutsche Telekom.

The radiobuttons, its usage and connection with event handler is shown in
the sample called `Button Showcase 2`.

Feature 0.8.0 (non-public)
--------------------------

components: checkboxes added

The implementation for checkboxes added. The checkbox is implemented as an
overlay on the standard button. It fully complies with the specification of
the Styleguide of Deutsche Telekom.

The checkbox, its usage and connection with event handler is shown in the
sample called `Button Showcase 2`.

Feature 0.7.0 (non-public)
--------------------------

components: buttons added

A set of buttons has been added to the library: beveled button, embossed
button, flat button, indicative button. All buttons entirely comply with the 
Styleguide of Deutsche Telekom.

A comparison between the buttons according to Styleguide specification and
the default Android button is shown in the sample called `Button Sample` (two
variants for implementation in source code and XML).

The events sent by the buttons are illustrated in the sample called `Button
Selector`.

All buttons and usage of the buttons are illustrated in the sample called
`Button Showcase` (two variants for implementation in source code and XML).

Feature 0.6.0 (non-public)
--------------------------

common: set of elements extended

The set of common elements has been extended with implementations of several
boxes, such as rounded box, corner box, and notification frame. The new
elements are available in the package
`de.telekom.pde.codelibrary.ui.elements.boxes`.

The elements are illustrated in the samples in the section `Graphical
Elements Samples`.

Feature 0.5.0 (non-public)
--------------------------

common: set of common elements added

A set of common elements are added in the package
`de.telekom.pde.codelibrary.ui.elements.common`, such as
shapes, delimiter, lines, shadows, image views.

The elements are illustrated in the samples in the section `Graphical
Elements Samples`.

Feature 0.4.0 (non-public)
--------------------------

common: state machine defined for realization of agent states

A state machine has been defined to be in control of the agent states of
the components. The agent states have been added to the package
`de.telekom.pde.codelibrary.ui.agents`.

Feature 0.3.0 (non-public)
--------------------------

common: definition and arithmetic of building units

A set of classes has been added to handle the complex building unit
specification. It defines the basic grid dependent on screen resolution, and
provides functions to calculate and transform building for specific usage,
such as 1/6 or 2/3 derivates. The handling has been added to the package
`de.telekom.pde.codelibrary.ui.buildingunits`.

Feature 0.2.0 (non-public)
--------------------------

common: creation of comprehensive color mappings

A comprehensive color mask has been added. The colors are defined inside
the package `de.telekom.pde.codelibrary.ui.color` and the XML files in 
`res/xml`.

Init 0.1.0 (non-public)
-----------------------

init: project and structure defined

The project has been initialized. The project structure has been defined.
The development environment has been created. A set of constants has been
added. The distribution environment has been created.

*/
