@echo off
echo Resetting GameEngine.java from backup
copy c:\Users\mp2k5\Documents\GitHub\TMDDPBO2025\viewmodel\GameEngine_backup.java c:\Users\mp2k5\Documents\GitHub\TMDDPBO2025\viewmodel\GameEngine.java /Y

echo Making changes to Heart class in GameEngine.java
echo This will add code to make caught hearts go to player first, then to the girl

REM We can't do this with simple batch commands, so let's just tell the user what to do
echo.
echo Please open GameEngine.java and:
echo 1. Add 'private boolean returnedToPlayer;' to the Heart class's fields
echo 2. Initialize it in the constructor with 'this.returnedToPlayer = false;'
echo 3. Change the update method to have 3 states:
echo    - When not caught: move normally
echo    - When caught but not returned to player: move to player first
echo    - When caught and returned to player: move to girl
echo.
echo After making these changes, compile and run the game:
echo   - javac model\*.java view\*.java viewmodel\*.java Main.java
echo   - java -cp .;lib\mysql-connector-j-9.2.0.jar Main
echo.
echo Press any key to exit
pause
