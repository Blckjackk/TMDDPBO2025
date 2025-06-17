# Sound Files for Azzam Love Game

Place the following sound files in this directory:

1. `game_soundtrack.wav` - Background music that plays during gameplay
2. `game_bonus.wav` - Achievement sound that plays when a heart reaches the girl
3. `game_over.wav` - Sound that plays when the game ends
4. `game_start.wav` - Sound that plays when the game starts

## Sound Effects Triggers

- `game_soundtrack.wav`: Plays continuously in a loop during gameplay
- `game_bonus.wav`: Plays when a heart reaches the girl character
- `game_over.wav`: Plays when the game ends (time runs out or player presses SPACE/ESC)
- `game_start.wav`: Plays when the game first starts

## Sound File Format

All sound files should be in WAV format. If you have sounds in other formats, you can convert them to WAV using free tools like Audacity.

## Troubleshooting

If sounds are not playing:
1. Make sure the files exist in this directory with exactly the names mentioned above
2. Check that the files are valid WAV audio files
3. Verify your computer's sound is working and not muted
4. Java Sound API requires the proper audio system setup - ensure your Java installation supports audio playback
