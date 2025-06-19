@echo off
echo ===== MP3 to WAV Converter for Azzam Love Game =====
echo This script will convert MP3 files to WAV files for better compatibility with Java Sound API
echo.

REM Check if ffmpeg is installed
where ffmpeg >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: ffmpeg is not installed or not in your PATH.
    echo Please download ffmpeg from https://ffmpeg.org/download.html and install it
    echo or use an online converter to convert your MP3 files to WAV format.
    echo.
    echo Place the WAV files in the sounds folder with these names:
    echo - game_start.wav
    echo - game_soundtrack.wav
    echo - game_bonus.wav
    echo - game_over.wav
    echo.
    pause
    exit /b
)

echo Found ffmpeg, will convert MP3 files to WAV format.
echo.

REM Create sounds directory if it doesn't exist
if not exist "sounds" mkdir sounds

REM Convert game start sound
if exist "assets\sound game start.mp3" (
    echo Converting game start sound...
    ffmpeg -y -i "assets\sound game start.mp3" -acodec pcm_s16le -ar 44100 "sounds\game_start.wav"
    echo Game start sound converted!
) else (
    echo Warning: Could not find "assets\sound game start.mp3"
)

REM Convert in-game music
if exist "assets\sound ingame.mp3" (
    echo Converting in-game music...
    ffmpeg -y -i "assets\sound ingame.mp3" -acodec pcm_s16le -ar 44100 "sounds\game_soundtrack.wav"
    echo In-game music converted!
) else (
    echo Warning: Could not find "assets\sound ingame.mp3"
)

REM Convert achievement sound
if exist "assets\sound achivement.mp3" (
    echo Converting achievement sound...
    ffmpeg -y -i "assets\sound achivement.mp3" -acodec pcm_s16le -ar 44100 "sounds\game_bonus.wav"
    echo Achievement sound converted!
) else (
    echo Warning: Could not find "assets\sound achivement.mp3"
)

REM Convert character change sound
if exist "assets\sound berubah.mp3" (
    echo Converting character change sound...
    ffmpeg -y -i "assets\sound berubah.mp3" -acodec pcm_s16le -ar 44100 "sounds\game_over.wav"
    echo Character change sound converted!
) else (
    echo Warning: Could not find "assets\sound berubah.mp3"
)

echo.
echo Conversion complete! WAV files have been placed in the sounds directory.
echo.
echo If you don't see any conversion messages, make sure your MP3 files exist in the assets folder
echo or manually convert them using an online converter.
echo.
pause
