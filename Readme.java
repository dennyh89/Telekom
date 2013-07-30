/** @mainpage How to use
 *  

The Code Library
================

Target
------

The Code Library project creates standardized and re-usable source code
to support cross-product development and increase efficiency and speed in
product development.

Description
-----------

The Code Library is a platform for sharing software components to build
graphical user interfaces. The components developed by Deutsche Telekom
Product Design implement standard elements of graphical user interfaces to
support a consistent user experience and efficient development across Telekom
products.

Content
-------

The Code Library is available for Android, iOS, and HTML. All software
components are provided in source code with examples of application and API
documentation.

Version Control System
----------------------

The content within the Code Library is continuously updated and extended.
The updates and previous versions are managed by the version control system
'git', the use of which is well supported by developer tools like Eclipse,
IntelliJ and Xcode.

The server is `git.design.telekom.com`. The protocol is HTTPS. Note, that
the system is not configured to handle access to the repositories using a
Web-browser. Zip files are also not provided because of the missing update
process after download. 

We recommend to integrate the git repositories directly into your developer
tools. If that does not work you can use a simple git client like
[GitExtensions](code.google.com/p/gitextensions/) or
[TortoiseGit](code.google.com/p/tortoisegit/).

For more information go to the Experience Toolbox
[FAQ](https://www.design.telekom.com/meta/faq/).

Terms of use
------------

The content of the Code Library can be used for required software
developments for Telekom products in line with the terms of use of Experience
Toolbox Deutsche Telekom. Full compliance with these terms is mandatory. They
are available in the Experience Toolbx at: Account > Terms of use & legal
notices.

Licensing for Telegrotesk Font
------------------------------

For licensing reasons, only employees of the Deutsche Telekom Group are
authorized to download the typefaces free of charge. They must not be passed
to third parties.

All agencies, subcontractors, business partners, etc. are required to purcha
the typefaces from URW++.

URW++ Design & Development
PoppenbÎáÎ÷Î÷ÎáÎùÎåtteler Bogen 36
22399 Hamburg, Germany
Tel. +49 40 60605 0
E-mail: info@urwpp.de
www.urwpp.de (IdentiType)

User Authorization
------------------

The git and the Experience Toolbox use Single Sign On (SSO). Please use your
login and password from the Experience Toolbox for accessing the
repositories.

Special permission is not required. All employees of Deutsche Telekom and the
national companies requiring the Code Library for their work can register
under the following link: (design.telekom.com/register/).

Development
-----------

The components are developed in collaboration with an external
software development agency. The development is based on the agile
software development methodology.

How to contribute
-----------------

A direct contribution to further developments of the Code Library is
currently not possible. All repositories are read-only, all branches are
protected. Write-access to repositories within the git system is only given
to authorized users.

If you want to modify or add source code considered to be valuable for a
larger community of user interfaces developers, please contact the Helpdesk.

Helpdesk
--------

Please send your bug reports, change requests and other feedback to the
design helpdesk
(E-mail: design-support@telekom.de - Phone: +49 (0) 228 181-22222).

The Code Library for Android
============================
                
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

If you get an SSL certificate problem, turn off the SSL verification for
git. You have two options to turn it off:

    git config --global http.sslVerify false
    export GIT_SSL_NO_VERIFY=true
    
Branches
--------

The master branch of the git system contains the latest version of the
software components. Previous versions will be available in specific branches
named after the version of the underlying operating system. Software
components of a specific branch can be cloned directly by using the option
“-b”.

At present the Code Library only contains one version of software components.
No other branches are specified.

Versioning
-------------------

A version number takes the form `X.Y.Z` where `X`, `Y`, and `Z` are
non-negative integers. X is the major version, Y is the minor version, and Z
is the patch version.

Each element increases numerically by increments of one. The patch version Z
(x.y.Z) is incremented if bug fixes are introduced. A bug fix is an internal
change that fixes incorrect behavior. The minor version Y (x.Y.z) is
incremented if substantial new functionality or improvements are introduced.
Patch version is reset to 0 when minor version is incremented. Major version
X (X.y.z) is incremented if dedicated milestones are completed that change
the API of the library. Changing the major version might request modification
in the code using the library. Patch and minor version are reset to 0 when
major version is incremented.

	For instance: 1.9.0 -> 1.9.1 -> 1.10.0 -> 1.11.0 -> 1.11.1 -> 1.11.2 ->
	2.0.0 -> 2.1.0. 

Major version zero (0.y.z) is for initial development. The code should not
be considered stable. Version 1.0.0 defines the initial release to the public. 

A pre-release version may be denoted by appending a dash and an identifier
immediately following the patch version. Identifiers are comprised of only
ASCII alphanumerics and dash [0-9A-Za-z-]. Pre-release versions satisfy but
have a lower precedence than the associated normal version. Use dedicated
branches. They do not appear at the master branch.

	For instance: 1.0.0-alpha, 1.3.7-rc1.

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
(Android - AndroidManifest.xml file). Select "Finish".

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
    
First Steps
-----------

To use the library it is required to initialize the library first:
1. Import the class `de.telekom.pde.codelibrary.ui.PDECodeLibrary`
2. Enter a new line into your App's or main Activity's `onCreate`-method:
			`PDECodeLibrary.getInstance().libraryInit(this);`

Example:

	public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PDECodeLibrary.getInstance().libraryInit(this);
		
		setContentView(R.layout.activity_main);
		}
	}

You can now add the components either in your layout files or in the source of
your application.

Example layout file:

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

Example source code:

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

Known issues
============

+ Version 1.12.9: The progress indicator is not styled to meet the
requirements of Deutsche Telekom.
+ Version 1.12.4: The icon font has problems with the visual appearance
if the size of the displayed icons varies.
+	Version 1.10.3
++ The height of the action bar does not conform to the Styleguide of
Deutsche Telekom.
++ The action bar does not support vertical divider between icons. It only
provides a styled vertical divider to be used between a text and an icon.
The styling uses standard color and thickness.
++ The width of the areas in the bottom bar on the phone do not conform to
the Styleguide of Deutsche Telekom. The width is set by the Android system,
according to the number of the items. The case of a single item uses the full
screen width for the single area. The cases of 2 and 3 items use areas of
rather small width placed at the corners and the central position. Cases of
more items scale the width of the areas to fill the full screen width. The
right and left corner item are misplaced. It runs out of the screen,
therefore it looks smaller and the icon does not appear central aligned
(actually it is, but using invisible screen parts).
++ All tooltips should appear close to the control it relates to. Currently,
the tooltips in the split action bar appear horizontal centralized.
++ The drop down panels for spinner, context menu and overflow should have a
shadow. The default shadow would be fine, it is getting lost when switching
to the Telekom-theme. It is not possible to re-activate the shadow because
it uses an implementation based on default 9-patch files. There are two
options: (a) Telekom creates 9-patches of just 1px in background color and a
second one of 1px for the line. These will be stretched over the areas; or
(b) Telekom accepts to simply use a frame of 1px in Telekom color.
++ The grey color of the squares in the default icon to launch the overflow
menu does not conform the Styleguide of Deutsche Telekom. The color cannot be
modified – the icon needs to be replaced by an own one. Telekom must provide
the icon to be used, or accept the use of the default icon. We will use the
icon “more” and turn it by 90 degree: https://www.design.telekom.com/icon-library/#/detail/204/hst// 
+	Version 1.7.0: The components for list header, text labels, and text
links have not been implemented yet. The login module uses simple elements
from standard Android UI Kit.
+	Version 1.6.0: Central vertical alignment of font Telegrotesk 2.7 is
incorrect. The distance to the top is one pixel too small, whereas the
distance to the bottom is one pixel too large. The font will be fixed in
another development. The new version of the font will request an additional
improvement of the library.
+	Version 1.4.0: List is work in progress.
   
Release Notes
=============

+	Current public version: 1.2.0
+	Current non-public version: 1.12.11

Major 2.0 (planned)
-------------------

### date: 2nd quarter 2013
### server: git.design.telekom.com
### repositories: PDECodeLibrary_Android.git, PDECodeSamples_Android.git, PDECodeLibrary_Android_Docu.git
### branch: master

Release: version 2.0
--------------------
The second release will extend the code library with new components for
lists, input fields, scrollbars, progressbars, list header, and text link.

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
the application has successfulyy been started. The design of the splash
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

### date: 14.12.2012
### server: git.design.telekom.com
### repositories: PDECodeLibrary_Android.git
### branch: master

config: project.properties added

The file project properties were added to support creation of a new
project with the library. The properties to set are:

	android.library=true
	target=android-16
    
Patch 1.0.1 (public)
--------------------

### date: 14.12.2012
### server: git.design.telekom.com
### repositories: PDECodeLibrary_Android.git, PDECodeSamples_Android.git
### branch: master

hotfix: fix of setAlpha for checkboxes

The call of setAlpha(float) was introduced in Android API11. The right call
for API10 is setAlpha(int). Without the fix, the app will crash on Android 2.3
devices.

In addition, the call of setAlpha(int) was deprecated in API17. Another
fix has been implemented in order to be future save.

Major 1.0 (public)
------------------

### date: 14.12.2012
### server: git.design.telekom.com
### repositories: PDECodeLibrary_Android.git, PDECodeSamples_Android.git, PDECodeLibrary_Android_Docu.git
### branch: master

release: version 1.0

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

###### init: project and structure defined

The project has been initialized. The project structure has been defined.
The development environment has been created. A set of constants has been
added. The distribution environment has been created.

*/