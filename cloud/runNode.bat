@echo off
setlocal enabledelayedexpansion

rem Define the source directory
set "source_dir=node"

rem Find a unique destination directory name
set "counter=1"
:Loop
if exist "!source_dir!_!counter!" (
    set /A "counter+=1"
    goto :Loop
)
set "destination_dir=!source_dir!_!counter!"

rem Copy the source directory to the destination directory
xcopy /s /e /i "%source_dir%" "%destination_dir%"

rem Change to the destination directory
cd "%destination_dir%" || exit

rem Run the desired command
gradlew.bat bootRun

rem Optionally, you can print a message indicating the process is complete
echo Script completed successfully. Source directory: %source_dir%, Destination directory: %destination_dir%

endlocal
