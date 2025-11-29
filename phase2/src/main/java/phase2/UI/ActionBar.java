package phase2.UI;

import phase2.UI.topPanel;
import phase2.UI.MusicManager;

/**
 * Enum representing different actions available in the game's action bar.
 * Each action defines its own behavior when executed.
 */
public enum ActionBar {

    /** Toggles the game's pause state. */
    Button_Pause {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            panel.togglePause(gp);
        }
    },

    /** Toggles the game's sound on or off. */
    Button_Sound {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            panel.toggleSound(musicManager);
        }
    },

    /** Resets the game to the start screen and restarts game logic. */
    Button_Reset {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            if (gp != null) {
                gp.restartGame();
                gp.gameState = GamePanel.GameState.START_SCREEN;
                panel.resetGame();
            }
        }
    },

    /** Displays help information to the player via the dialogue box. */
    Button_Help {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            if (gp != null) {
                gp.getDialogueBox().loadLine(
                        "Need Help?",
                        "Use WASD to move and SPACE to attack",
                        "Press E to open chests"
                );
                gp.getDialogueBox().show_Dialogue();
            }
        }
    },

    /** Exits the game application. */
    Button_EXIT {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            System.exit(0);
        }
    },

    /** Represents no action; does nothing when executed. */
    none {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            // Do nothing
        }
    };

    /**
     * Executes the action associated with the enum constant.
     *
     * @param panel        the top panel UI element
     * @param gp           the game panel instance
     * @param musicManager the music manager instance
     */
    public abstract void execute(topPanel panel, GamePanel gp, MusicManager musicManager);

}
