VLC Skin Editor release 0.8.6
-----------------------------------------------------------
Changelog:
-----------------------------------------------------------
0.8.6  - Bugfix: XML Output was not proper UTF-8

0.8.5  - Various  bugfixes and technical improvements
       - A toolbar was added
       - Sliders can be edited in the preview window
       - Slider background generator now supports vertical
         sliders

0.8.1  - Font rendering bugfix
       - Anchor points saving bugfix
       
0.8.0  - Performance improvements
       - Multi-language support
       - Remembering of windows' last positions & sizes
       - New parsing system for all valid skin files
       - Bug-fixes
       
0.7.0  - Preview can be saved as image
       - Improved SubBitmap editing
       - Resources/windows/layouts can be duplicated easily
       - XML output is now indented
       - Slider backgrounds can be generated in a wizard
       - Improved preview of skin (can be zoomed now)
       - Improved cross platform behaviour
       - Improved online help
       - Automatic update
       - Some (critical) bugs were fixed
       
0.6.1b - Critical bufix for the Preview Window 
       - Bugfix for slider points parsing and XML parsing
       
0.6.0b - Resources/windows/layouts/items can be edited by
         doubleclicking on them in the hiearchy trees
       - Sliders are rendered completely as in VLC
       - Cancel buttons for editing dialogs
       - Items can be moved in the preview window with the
         arrow keys when the preview window is focused and
         the CONTROL button is pressed.
       - Many bugfixes
       - Many actions can be undone/redone
       - SubBitmaps can be edited visually
       
0.5.0a - Boolean variables are interpreted in the preview
       - Global variables can be edited (boolean only atm)
       - Preview window perfomance improvements
       - Adding of an action editor
       - Some bugfixes
       - New icons
       
0.4.5a - Several bugfixes
       - Text variables are interpreted in the preview
       
0.4.0a - Items can be positioned with the mouse
       - Trees don't collapse anymore completely when an
         element in it is updated, they automatically
         expand the path to the updated item and select it
         
0.3.5b - NullPointerException fix for Playtree rendering
       - VLT files can be imported
       
0.3.5a - Preview of Layouts
       - XML parsing/generating fixes
       
0.3.0b - All items can be edited
       - Theme attributes can be edited
       - VLT packages can be exported
       - The open/save dialog will point by default to the
         VLC Skins directory on Windows and Linux
         
0.3.0a - Items can be added
       - Items can be deleted
       - All items except SliderBGs, Playlists, Playtrees
         can be edited
         
0.2.8a - Improved trees
       - Groups, Panels and Images can be edited
       - Several PNG files can be added at once
       - Confirmation of exiting if skin is unsafed
       
0.2.5a - Windows and Layouts can be edited
       - Bitmaps, SubBitmaps and Fonts can be added
       
0.2.0a - Now files can be saved
       - Font, Bitmap and SubBitmap resources can be edited
       
0.1.0b - Initial Release
       - Complete XML parsing of a skins2 file
       - Working display of the skins hierarchy
-----------------------------------------------------------
System Requirements
-----------------------------------------------------------
Java SE RE 6 or later (DL at java.sun.com)
approximately at least 256MB RAM and a 1GHz CPU
-----------------------------------------------------------
How to run the program:
-----------------------------------------------------------
On Windows simply launch the VLCSkinEditor executable.
On Linux run the VLCSkinEditor launch script.

Also you can run the skin editor by launching the jar file.
You can do this by running "java -jar VLCSkinEditor.jar".
Make sure that you run java in the same directory as the
jar file and the "lang" folder.
-----------------------------------------------------------
Support:
-----------------------------------------------------------
There exists an online help & documentation at
  http://www.videolan.org/vlc/skinedhlp
Furthermore you can ask questions in the VideoLAN forum at
  http://forum.videolan.org/viewforum.php?f=15
-----------------------------------------------------------
Copyright:
-----------------------------------------------------------
(c)2007-2010 Daniel Dreibrodt (daniel.dreibrodt@gmail.com) 
See LICENSE.TXT for further details
-----------------------------------------------------------
Special thanks to:
-----------------------------------------------------------
Xènia Albà for the Catalan translation
Joseph S. for the Spanish translation
"Alex64" and Jean-Baptiste Kempf for the French translation
"Airplanez" for the Korean translation
Myckel Habets for the Dutch translation
Michał Trzebiatowski for the Polish translation
Sidney Doria for the Brasilian Portuguese translation
Andrey Wolk for the Russian translation
Ђорђе Васиљевић for the Serbian translation
Marián Hikaník for the Slovak translation
Daniel Nylander for the Swedish translation
-----------------------------------------------------------
Further credits:
-----------------------------------------------------------
The Tango! Desktop Project for some icons (Public Domain)
Timothy Gerard Endres for the Windows Registry access and
  TAR support libraries for Java (Public Domain)
Cyril Deguet and Olivier Teulière for the Boolean Expression
 Evaluator and Bezier code (GPL 2+)