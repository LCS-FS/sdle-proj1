@echo off
rem Change to the destination directory
cd proxy || exit

rem Run the desired command
gradlew.bat bootRun

rem Optionally, you can print a message indicating the process is complete
echo Script completed successfully. Source directory: %source_dir%, Destination directory: %destination_dir%
