@echo off
setlocal enabledelayedexpansion

rem Define the source directory
set "source_dir=node"

rem If a node ID is provided as an argument, use it; otherwise, find the next available ID
if "%~1" neq "" (
    set "node_id=%~1"
    echo Starting node %node_id%...
) else (
    rem Find a unique destination directory name
    set "counter=1"
    :Loop
    if exist "!source_dir!_!counter!" (
        set /A "counter+=1"
        goto :Loop
    )
    set "node_id=!counter!"
    set "destination_dir=!source_dir!_!node_id!"
    xcopy /s /e /i "%source_dir%" "!destination_dir!"
    echo Starting a new node with id !node_id!
)

set "destination_dir=%source_dir%_%node_id%"

rem Check if the destination directory exists
if not exist "!destination_dir!" (
    echo Error: Node with ID %node_id% does not exist.
    exit /b 1
)

rem Change to the destination directory
cd /d "!destination_dir!" || exit /b 1

rem Run the desired command
gradlew.bat bootRun --args="%node_id%"

rem Optionally, you can print a message indicating the process is complete
echo Script completed successfully. Source directory: %source_dir%, Destination directory: !destination_dir!

endlocal
