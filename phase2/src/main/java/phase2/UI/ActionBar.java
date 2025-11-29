package phase2.UI;

import phase2.UI.topPanel;
import phase2.UI.MusicManager;

public enum ActionBar {
    Zoom_In {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            panel.adjustZoom(0.1);
        }
    },
    Zoom_Out {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            panel.adjustZoom(-0.1);
        }
    },
    Button_Pause {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            panel.togglePause(gp);
        }
    },
    Button_Sound {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            panel.toggleSound(musicManager);
        }
    },
    Button_Reset {  // formerly Button_Back
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            if (gp != null) {
                gp.restartGame();
                gp.gameState = GamePanel.GameState.START_SCREEN;
                panel.resetGame();
            }
        }
    },
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
    Button_EXIT {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            System.exit(0);
        }
    },
    none {
        @Override
        public void execute(topPanel panel, GamePanel gp, MusicManager musicManager) {
            // Do nothing
        }
    };

    public abstract void execute(topPanel panel, GamePanel gp, MusicManager musicManager);
    
}
