[Files]
Source: share/ICE_JNIRegistry.dll; DestDir: {app}
Source: LICENSE.TXT; DestDir: {app}
Source: README.TXT; DestDir: {app}; Flags: isreadme
Source: skin.dtd; DestDir: {app}
Source: share/VLCSkinEditor.exe; DestDir: {app}
Source: VLCSkinEditor.jar; DestDir: {app}
Source: lang/*.txt; DestDir: {app}/lang
[Icons]
Name: {group}\VLC Skin Editor; Filename: {app}\VLCSkinEditor.exe; WorkingDir: {app}; IconFilename: {app}\VLCSkinEditor.exe; IconIndex: 0
Name: {group}\Uninstall VLC Skin Editor; Filename: {uninstallexe}
Name: {group}\Online Help; Filename: http://www.videolan.org/vlc/skinedhlp/
[Setup]
VersionInfoVersion=0.8.6
AppVersion=0.8.6
AppVerName=0.8.6
OutputBaseFilename=VLCSkinEditor_setup
VersionInfoCompany=VideoLAN
VersionInfoDescription=Setup for the VLC Skin Editor
VersionInfoCopyright=©2009 VideoLAN
AppCopyright=©2009 VideoLAN Team
AppName=VLC Skin Editor
LicenseFile=LICENSE.TXT
PrivilegesRequired=poweruser
DefaultDirName={pf}\VideoLAN\VLC Skin Editor
DirExistsWarning=no
DefaultGroupName=VideoLAN\VLC Skin Editor
AppendDefaultGroupName=false
AppPublisher=VideoLAN
AppPublisherURL=http://www.videolan.org
AppSupportURL=http://www.videolan.org/vlc/skineditor.html
AppUpdatesURL=http://www.videolan.org/vlc/skineditor.html
AppID={{977C5080-EA08-435D-8901-233A506E1651}}
AppReadmeFile={app}\README.TXT
UninstallDisplayIcon={app}\VLCSkinEditor.exe
UninstallDisplayName=VLC Skin Editor
ChangesAssociations=true
OutputDir=dist
[Registry]
Root: HKCR; Subkey: .vlt; ValueType: string; ValueData: VLCSkinFile; Flags: uninsdeletekey
Root: HKCR; Subkey: VLCSkinFile; ValueType: string; ValueData: Compressed VLC Skin; Flags: uninsdeletekey
Root: HKCR; Subkey: VLCSkinFile\DefaultIcon; ValueType: string; ValueData: """{app}\VLCSkinEditor.exe"",2"
Root: HKCR; Subkey: VLCSkinFile\shell\open\command; ValueType: string; ValueName: ; ValueData: """{app}\VLCSkinEditor.exe"" ""%1"""
Root: HKCR; Subkey: VLCSkinFile\shell\open; ValueType: string; ValueData: Edit with VLC Skin Editor
[InstallDelete]
Name: {app}/VLCSkinEditor.cfg; Type: files
