@echo off
xcopy L:\Doc\ASwork\JavaLib\javalib2\src\main\java L:\Doc\Git\AndroidPro\JavaLib\src\ /S
xcopy L:\Doc\ASwork\JavaLib\javalib2\build\classes\main L:\Doc\Git\AndroidPro\JavaLib\cls\ /S
xcopy L:\Doc\ASwork\JavaLib\javalib2\libs L:\Doc\Git\AndroidPro\JavaLib\lib\ /S
copy L:\Doc\ASwork\JavaLib\javalib2\build.gradle L:\Doc\Git\AndroidPro\JavaLib
pause
