[CmdletBinding()]
Param(
   [Parameter(Mandatory=$True,Position=1)]
   [string]$javaFullPath="",

   [Parameter(Mandatory=$True,Position=2)]
   [string]$apiCertFolder="",

   [Parameter(Mandatory=$True,Position=3)]
   [string]$jarFullPath="",

   [Parameter(Mandatory=$True,Position=4)]
   [string]$portNumber="",

   [Parameter(Mandatory=$True,Position=5)]
   [string]$iconFullPath="",

   [Parameter(Mandatory=$True,Position=6)]
   [string]$linkName=""
)

$shell = New-Object -ComObject WScript.Shell

$startMenuAllUsers = $shell.SpecialFolders.item("AllUsersPrograms")
$shortcut=Join-Path -Path $startMenuAllUsers  -ChildPath "Absolute Manage"
New-Item $shortcut -type directory -force
$shortcut2=Join-Path -Path $shortcut  -ChildPath $linkName

$shortcutArgs = "-jar ""$jarFullPath"" WebAPICertFolder=""$apiCertFolder"" SyncSvcPortNumber=$portNumber"

$iconFullPath = $iconFullPath + ",0"

$objShortcut = $shell.CreateShortcut($shortcut2)
$objShortcut.TargetPath = $javaFullPath
$objShortcut.Arguments = $shortcutArgs
$objShortcut.IconLocation = $iconFullPath
$objShortcut.Save()