# Sound Files for Azzam Love Game

## Missing Sound Files
The game needs the following WAV format sound files:

1. `game_start.wav` - Sound played when the game starts
2. `game_soundtrack.wav` - Background music that plays during gameplay
3. `game_bonus.wav` - Achievement sound when a heart reaches the girl
4. `game_over.wav` - Sound played when the game ends

## How to Get Sound Files
To make the sound system work correctly:

1. **Convert MP3 to WAV**: If you have the MP3 files in the assets folder, convert them to WAV format using a tool like Audacity or an online converter.

2. **Place the files in this folder** with the exact names mentioned above.

3. **Audio Requirements**: Java Sound API works best with:
   - 16-bit WAV files
   - 44.1 kHz sample rate
   - Mono or stereo

## Alternative Sound Libraries
If you prefer to keep using MP3 files:

1. Add the MP3SPI library (supports MP3 in Java Sound API)
   - Download from: https://github.com/umjammer/mp3spi or https://mvnrepository.com/artifact/com.googlecode.soundlibs/mp3spi
   - Add the JAR files to your lib folder and classpath

2. Or use JLayer library
   - Download from: https://mvnrepository.com/artifact/javazoom/jlayer
   - Add to your project dependencies
