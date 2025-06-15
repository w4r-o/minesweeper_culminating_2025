/**
 * The main controller of the application, following the MVC pattern.
 * It manages the game state, user input, and the flow between different application screens (views).
 * For the Minesweeper Culminating Project.
 *
 * @author Aaron Jiang 
 * @author Leo Tan
 * @version 2.2.0
 * @since 2025-06-14
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * The main controller of the application, following the MVC pattern.
 * It manages the game state, user input, and the flow between different application screens (views).
 * This class orchestrates interactions between the GameBoard (model), various UI panels (views),
 * and the Player.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.2.0
 * @since 2025-06-14
 */
public class GameController {
    // --- Member Variables ---
    /** The main application window frame. */
    private GameFrame gameFrame;
    /** The panel that holds the game board, top panel, and side panel. */
    private GameScreenPanel gameScreenPanel;
    /** The game board model, containing all cell logic. */
    private GameBoard gameBoard;
    /** The player model, managing health and power-ups. */
    private Player player;
    /** Timer for tracking game time. */
    private Timer timer;
    /** Stores the elapsed time in seconds for the current game. */
    private int timeElapsed;
    /** The time limit for the current game in seconds; -1 indicates no limit. */
    private int timeLimitSeconds = -1;
    /** Flag to indicate if cheat mode is active. */
    private boolean cheatMode = false;
    /** Flag to indicate if reveal power-up mode is active. */
    private boolean revealModeActive = false;
    /** Flag to indicate if bomb power-up mode is active. */
    private boolean bombModeActive = false;
    /** The difficulty level of the current game. */
    private Difficulty currentDifficulty;
    /** Reference to the main menu panel, used for navigation and focus management. */
    private MainMenuPanel mainMenuPanel;

    /**
     * Constructor for the GameController.
     * Initializes the main game frame and sets up the main menu panel.
     */
    public GameController() {
        this.gameFrame = new GameFrame(); // Create the main application window
        setupMainMenu(); // Configure and add the main menu panel
    }

    /**
     * Makes the main application window visible, starting the game for the user.
     * This is the entry point for the UI.
     */
    public void start() {
        showMainMenu(); // Display the main menu
        gameFrame.setVisible(true); // Make the game window visible
        // Background music is now started within showMainMenu()
    }

    /**
     * Creates the MainMenuPanel and attaches all necessary listeners to its buttons.
     * This method is called once during controller initialization.
     */
    private void setupMainMenu() {
        mainMenuPanel = new MainMenuPanel(); // Initialize the main menu panel
        // Attach action listeners to main menu buttons to navigate to different screens
        mainMenuPanel.addNewGameListener(e -> showDifficultySelection());
        mainMenuPanel.addDailyChallengeListener(e -> startDailyChallenge());
        mainMenuPanel.addTutorialListener(e -> showTutorial());
        mainMenuPanel.addAboutListener(e -> showAbout());
        // Add the main menu panel to the game frame with a card layout identifier
        gameFrame.addPanel(mainMenuPanel, "MainMenu");
    }

    /**
     * Switches the view to the main menu and ensures it can receive keyboard input for cheats.
     * This method is now fixed and robust, ensuring focus is correctly requested.
     */
    private void showMainMenu() {
        gameFrame.showPanel("MainMenu"); // Display the main menu panel
        // Request focus on the main menu panel after it's shown to enable key bindings (e.g., for cheats)
        SwingUtilities.invokeLater(() -> {
            mainMenuPanel.requestFocusInWindow(); // Use the direct reference to request focus
        });
        // Start playing background music when the main menu is displayed
        AudioManager.playBackgroundMusic("assets/bg_music.wav");
    }

    /**
     * Finalizes the game, stops the timer, and displays the appropriate win/loss screen.
     * This method is now fixed to correctly handle game end conditions.
     */
    private void endGame() {
        stopTimer(); // Stop the game timer
        AudioManager.stopBackgroundMusic(); // Stop background music
        // Determine if the game was won or lost
        boolean isWin = gameBoard.getGameState() == GameBoard.GameState.WIN;
        if (isWin) {
            gameBoard.autoFlagBombsOnWin(); // Automatically flag remaining bombs on win
            AudioManager.playSoundEffect("assets/winningsfx.wav"); // Play winning sound effect
        }
        updateUI(); // Update the UI to reflect the final game state

        // Create and display the final result panel (win or loss screen)
        ResultPanel resultPanel = new ResultPanel(isWin ? "You Won!" : "Game Over", isWin);
        // Add a listener to the result panel's return button to go back to the main menu
        resultPanel.addReturnToMenuListener(e -> GameController.this.returnToMainMenu());
        // Add the result panel to the game frame and display it
        gameFrame.addPanel(resultPanel, "ResultScreen");
        gameFrame.showPanel("ResultScreen");
    }

    /**
     * Displays the tutorial panel.
     */
    private void showTutorial() {
        // Create a new TutorialPanel and add a listener to return to the main menu
        gameFrame.addPanel(new TutorialPanel(e -> showMainMenu()), "Tutorial");
        gameFrame.showPanel("Tutorial"); // Display the tutorial panel
    }

    /**
     * Displays the about panel.
     */
    private void showAbout() {
        // Create a new AboutPanel and add a listener to return to the main menu
        gameFrame.addPanel(new AboutPanel(e -> showMainMenu()), "About");
        gameFrame.showPanel("About"); // Display the about panel
    }

    /**
     * Displays the difficulty selection panel, allowing the user to choose game settings.
     */
    private void showDifficultySelection() {
        DifficultySelectionPanel difficultyPanel = new DifficultySelectionPanel();
        // Attach listeners to difficulty buttons to start a new game with selected settings
        difficultyPanel.addEasyListener(e -> GameController.this.startNewGame(Difficulty.EASY, difficultyPanel.isNoiseGenSelected(), difficultyPanel.getSelectedTimeLimitSeconds()));
        difficultyPanel.addMediumListener(e -> GameController.this.startNewGame(Difficulty.MEDIUM, difficultyPanel.isNoiseGenSelected(), difficultyPanel.getSelectedTimeLimitSeconds()));
        difficultyPanel.addHardListener(e -> GameController.this.startNewGame(Difficulty.HARD, difficultyPanel.isNoiseGenSelected(), difficultyPanel.getSelectedTimeLimitSeconds()));
        // Attach listener for the custom game option
        difficultyPanel.addCustomListener(e -> GameController.this.showCustomDialog(difficultyPanel.isNoiseGenSelected(), difficultyPanel.getSelectedTimeLimitSeconds()));
        // Add the difficulty selection panel to the game frame and display it
        gameFrame.addPanel(difficultyPanel, "DifficultySelection");
        gameFrame.showPanel("DifficultySelection");
    }

    /**
     * Displays the custom game settings dialog.
     *
     * @param useNoiseGen If true, the bomb count field is hidden in the dialog.
     * @param timeLimitSeconds The selected time limit from the difficulty panel, passed to the dialog.
     */
    private void showCustomDialog(boolean useNoiseGen, int timeLimitSeconds) {
        CustomGameDialog dialog = new CustomGameDialog(gameFrame, useNoiseGen);
        dialog.setVisible(true); // Make the dialog visible and modal
        // If the user confirmed the settings in the dialog
        if (dialog.isConfirmed()) {
            try {
                // Retrieve and validate user input for rows, columns, and bombs
                int rows = Math.min(24, Math.max(9, dialog.getRows()));
                int cols = Math.min(40, Math.max(9, dialog.getCols()));
                // If noise generation is used, bombs are 0; otherwise, validate bomb count
                int bombs = useNoiseGen ? 0 : Math.min(rows * cols - 9, Math.max(1, dialog.getBombs()));
                // Start a new game with custom settings
                GameController.this.startNewGame(rows, cols, bombs, useNoiseGen, timeLimitSeconds);
            } catch (NumberFormatException e) {
                // Show an error message if input is not a valid number
                JOptionPane.showMessageDialog(gameFrame, "Invalid input. Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Starts a new game with a preset difficulty.
     *
     * @param difficulty The selected Difficulty enum (EASY, MEDIUM, HARD).
     * @param useNoiseGen True if noise-based map generation should be used.
     * @param timeLimitSeconds The time limit for the game in seconds (-1 for no limit).
     */
    private void startNewGame(Difficulty difficulty, boolean useNoiseGen, int timeLimitSeconds) {
        this.currentDifficulty = difficulty; // Set the current difficulty
        this.timeLimitSeconds = timeLimitSeconds; // Store the selected time limit

        // Create the game board based on whether noise generation is selected
        if (useNoiseGen) {
            this.gameBoard = DailyChallengeGenerator.generateNoiseBoard(difficulty.getRows(), difficulty.getCols(), new Random().nextLong(), difficulty);
        } else {
            this.gameBoard = new GameBoard(difficulty.getRows(), difficulty.getCols(), difficulty.getBombCount(), difficulty);
        }
        initializeGame(); // Initialize and display the game screen
    }

    /**
     * Starts a new game with custom dimensions and bomb count.
     *
     * @param rows The number of rows for the custom game.
     * @param cols The number of columns for the custom game.
     * @param bombs The number of bombs for the custom game.
     * @param useNoiseGen True if noise-based map generation should be used.
     * @param timeLimitSeconds The time limit for the game in seconds (-1 for no limit).
     */
    private void startNewGame(int rows, int cols, int bombs, boolean useNoiseGen, int timeLimitSeconds) {
        this.currentDifficulty = Difficulty.CUSTOM; // Set difficulty to CUSTOM
        this.timeLimitSeconds = timeLimitSeconds; // Store the selected time limit

        // Create the game board based on whether noise generation is selected
        if (useNoiseGen) {
            this.gameBoard = DailyChallengeGenerator.generateNoiseBoard(rows, cols, new Random().nextLong(), currentDifficulty);
        } else {
            this.gameBoard = new GameBoard(rows, cols, bombs, currentDifficulty);
        }
        initializeGame(); // Initialize and display the game screen
    }

    /**
     * Starts the daily challenge game.
     * The daily challenge uses a fixed board generated by DailyChallengeGenerator.
     */
    private void startDailyChallenge() {
        this.currentDifficulty = Difficulty.HARD; // Daily challenge is always Hard difficulty
        this.timeLimitSeconds = -1; // No time limit for daily challenge
        this.gameBoard = DailyChallengeGenerator.generate(); // Generate the daily challenge board
        initializeGame(); // Initialize and display the game screen
    }

    /**
     * Initializes the game components and sets up the game screen.
     * This method is called after a new game board has been created.
     */
    private void initializeGame() {
        this.player = new Player(); // Create a new player for the game
        // Reset cheat and power-up modes
        this.cheatMode = false;
        this.revealModeActive = false;
        this.bombModeActive = false;
        // Reset cursor to default
        GameController.this.setCursor(Cursor.getDefaultCursor());

        // Create the game screen panel with the game board, player, and this controller
        gameScreenPanel = new GameScreenPanel(gameBoard, player, this);
        // Add the game screen panel to the game frame and display it
        gameFrame.addPanel(gameScreenPanel, "GameScreen");
        gameFrame.showPanel("GameScreen");
        // Get the GamePanel component from the GameScreenPanel
        GamePanel panel = gameScreenPanel.getGamePanel();

        // Add mouse listener for handling clicks on the game grid
        panel.addGridMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleGridClick(e); // Delegate click handling to a separate method
            }
        });
        // Add mouse motion listener for hover effects in power-up modes
        panel.addGridMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (revealModeActive || bombModeActive) {
                    panel.setMouseHoverPoint(e.getPoint()); // Update hover point for visual feedback
                }
            }
        });

        // Refactored 'C' cheat key handling to use Key Bindings on the gameFrame's root pane
        // This ensures the cheat key works regardless of which component has focus
        InputMap im = gameFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = gameFrame.getRootPane().getActionMap();

        // Bind 'c' and 'C' keys to the "toggleCheat" action
        im.put(KeyStroke.getKeyStroke('c'), "toggleCheat");
        im.put(KeyStroke.getKeyStroke('C'), "toggleCheat");
        // Define the action to be performed when "toggleCheat" is triggered
        am.put("toggleCheat", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleCheatMode(); // Toggle cheat mode
            }
        });

        startTimer(); // Start the game timer
    }

    /**
     * Handles mouse clicks on the game grid.
     * Determines the clicked cell, applies game logic (reveal, flag, power-up use),
     * and updates the UI.
     *
     * @param e The MouseEvent generated by the click.
     */
    private void handleGridClick(MouseEvent e) {
        // Do nothing if the game is not in PLAYING state
        if (gameBoard.getGameState() != GameBoard.GameState.PLAYING) return;

        GamePanel panel = gameScreenPanel.getGamePanel();
        // Calculate cell size and offsets to determine which cell was clicked
        int cellSize = Math.min(panel.getWidth() / gameBoard.getCols(), panel.getHeight() / gameBoard.getRows());
        int xOffset = (panel.getWidth() - cellSize * gameBoard.getCols()) / 2;
        int yOffset = (panel.getHeight() - cellSize * gameBoard.getRows()) / 2;
        int c = (e.getX() - xOffset) / cellSize; // Column index
        int r = (e.getY() - yOffset) / cellSize; // Row index

        PowerUp foundPowerUp = null; // To store any power-up collected

        // Handle power-up modes or regular clicks
        if (revealModeActive) {
            if (player.useReveal()) { // If player has reveal power-up and uses it
                gameBoard.revealArea(r, c); // Reveal a 3x3 area
            }
            toggleRevealMode(); // Deactivate reveal mode after use
        } else if (bombModeActive) {
            if (player.useBomb()) { // If player has bomb power-up and uses it
                gameBoard.clearArea(r, c, player); // Clear a 3x3 area
            }
            toggleBombMode(); // Deactivate bomb mode after use
        } else {
            // Handle left-click (reveal cell) or right-click (toggle flag)
            if (SwingUtilities.isLeftMouseButton(e)) {
                foundPowerUp = gameBoard.clickCell(r, c, player); // Perform a regular cell click
            } else if (SwingUtilities.isRightMouseButton(e)) {
                gameBoard.toggleFlag(r, c); // Toggle flag on the cell
            }
        }

        // If a power-up was found during the click, display a message
        if (foundPowerUp != null) {
            JOptionPane.showMessageDialog(gameFrame, "You collected a " + foundPowerUp.getType().toString() + " Power-Up!", "Power-Up Collected!", JOptionPane.INFORMATION_MESSAGE);
        }
        updateUI(); // Update the UI to reflect changes
        // Check if the game has ended after the click
        if (gameBoard.getGameState() != GameBoard.GameState.PLAYING) {
            endGame(); // Transition to end game screen
        }
    }

    /**
     * Returns the user to the main menu.
     * Stops the current game timer and displays the main menu panel.
     */
    public void returnToMainMenu() {
        stopTimer(); // Stop the game timer
        showMainMenu(); // Display the main menu (music will restart there)
    }

    /**
     * Allows the player to use a Heal Power-Up.
     * If the player's health is not at maximum, a heal is applied and UI updated.
     */
    public void usePlayerHeal() {
        if (player.getHealth() < Player.MAX_HEALTH) { // Check if healing is possible
            player.useHeal(); // Apply heal
            updateUI(); // Update health display
            AudioManager.playSoundEffect("assets/drop_sound.wav"); // Play sound effect
        }
    }

    /**
     * Activates or deactivates the Reveal Power-Up mode.
     * If bomb mode is active, it will be deactivated first.
     * The cursor changes to indicate the active mode.
     */
    public void activateRevealMode() {
        if (bombModeActive) toggleBombMode(); // Deactivate bomb mode if active
        // Toggle reveal mode if player has reveal power-ups or if it's already active
        if (player.getRevealCount() > 0 || revealModeActive) {
            toggleRevealMode();
            AudioManager.playSoundEffect("assets/drop_sound.wav"); // Play sound effect
        }
    }

    /**
     * Activates or deactivates the Bomb Power-Up mode.
     * If reveal mode is active, it will be deactivated first.
     * The cursor changes to indicate the active mode.
     */
    public void activateBombMode() {
        if (revealModeActive) toggleRevealMode(); // Deactivate reveal mode if active
        // Toggle bomb mode if player has bomb power-ups or if it's already active
        if (player.getBombCount() > 0 || bombModeActive) {
            toggleBombMode();
            AudioManager.playSoundEffect("assets/drop_sound.wav"); // Play sound effect
        }
    }

    /**
     * Toggles the reveal power-up mode on/off.
     * Updates the game panel's state and changes the cursor.
     */
    private void toggleRevealMode() {
        revealModeActive = !revealModeActive; // Toggle the mode flag
        gameScreenPanel.getGamePanel().setRevealMode(revealModeActive); // Inform the game panel
        // Change cursor to crosshair if active, default otherwise
        GameController.this.setCursor(revealModeActive ? Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) : Cursor.getDefaultCursor());
    }

    /**
     * Toggles the bomb power-up mode on/off.
     * Updates the game panel's state and changes the cursor.
     */
    private void toggleBombMode() {
        bombModeActive = !bombModeActive; // Toggle the mode flag
        gameScreenPanel.getGamePanel().setBombMode(bombModeActive); // Inform the game panel
        // Change cursor to move cursor if active, default otherwise
        GameController.this.setCursor(bombModeActive ? Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) : Cursor.getDefaultCursor());
    }

    /**
     * Starts the game timer.
     * The timer updates every second and checks for time limits.
     */
    private void startTimer() {
        timeElapsed = 0; // Reset elapsed time
        timer = new Timer(1000, e -> { // Create a new timer that fires every 1000ms (1 second)
            timeElapsed++; // Increment elapsed time
            if (timeLimitSeconds != -1) { // If a time limit is set
                int timeRemaining = timeLimitSeconds - timeElapsed; // Calculate remaining time
                gameScreenPanel.getTopPanel().updateTimer(timeRemaining, timeLimitSeconds); // Update timer display
                if (timeRemaining <= 0) {
                    gameBoard.setGameState(GameBoard.GameState.LOSS); // Set game state to loss if time runs out
                    endGame(); // End the game
                }
            } else {
                gameScreenPanel.getTopPanel().updateTimer(timeElapsed, timeLimitSeconds); // Update timer display (no limit)
            }
        });
        timer.start(); // Start the timer
    }

    /**
     * Stops the game timer if it is running.
     */
    private void stopTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop(); // Stop the timer
        }
    }

    /**
     * Updates the UI components (side panel, top panel, game panel) to reflect the current game state.
     */
    private void updateUI() {
        if (gameScreenPanel != null) {
            gameScreenPanel.getSidePanel().update(player); // Update player stats on side panel
            gameScreenPanel.getTopPanel().updateFlags(gameBoard.getFlagsPlaced(), gameBoard.getBombCount()); // Update flag count on top panel
            gameScreenPanel.repaint(); // Repaint the entire game screen panel
        }
    }

    /**
     * Toggles cheat mode on or off.
     * When active, the game panel might display additional debug information.
     */
    private void toggleCheatMode() {
        cheatMode = !cheatMode; // Toggle cheat mode flag
        gameScreenPanel.getGamePanel().setCheatMode(cheatMode); // Inform the game panel about cheat mode status
    }

    /**
     * Sets the cursor for the main game frame.
     * @param cursor The Cursor object to set.
     */
    private void setCursor(Cursor cursor) {
        gameFrame.setCursor(cursor);
    }

    /**
     * Returns the current Player object.
     * @return The Player instance.
     */
    public Player getPlayer() {
        return this.player;
    }
}
