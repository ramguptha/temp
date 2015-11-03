How to produce AM Web Admin build.

1. Define BUILD_FOLDER
2. Get contents from $/Projects/AMWEB/installer/AmWebMsi into BUILD_FOLDER.
3. Copy contents of $/Projects/AMWEB/ui.i3/client into BUILD_FOLDER\AmWebUi. Note that there are 2 files in AmWebUi already - web.config and welcome.html, these files should not be removed or changed.
4. Copy contents of $/Projects/AMWEB/api/amwebapi/view_config into BUILD_FOLDER\view_config.
5. Copy com.absolute.am.webapi.war file from \\drop\tempdrop\AMWebIF\v1.0.3 into BUILD_FOLDER\AmWebApi (confirm location with Daragh).
6. Copy $/Projects/AMWEB/docs/NOTICES.txt file into BUILD_FOLDER.
7. Copy ABT_AM_RegisterWizard.jar file from $/Projects/AMWEB/api/ABT_AM_RegisterWizard into BUILD_FOLDER\AmRegisterWizard.
8. Open installer project BUILD_FOLDER\AmWebMsi.ism with InstallShield and produce a build or create a build using ISCmdBld.exe command line. The build will reside in BUILD_FOLDER\PROJECT_ASSISTANT\SINGLE_EXE_IMAGE\DiskImages\DISK1.