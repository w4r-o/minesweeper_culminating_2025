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

public class GameController {
    // --- Member Variables ---
    private GameFrame gameFrame;
    private GameScreenPanel gameScreenPanel;
    private GameBoard gameBoard;
    private Player player;
    private Timer timer;
    private int timeElapsed;
    private boolean cheatMode=false, revealModeActive=false, bombModeActive=false;
    private Difficulty currentDifficulty;
    private MainMenuPanel mainMenuPanel;        // Reference to the main menu panel

    /**
     * Constructor for the GameController. Initializes the main frame and sets up the main menu.
     */
    public GameController() {
        this.gameFrame = new GameFrame();
        setupMainMenu();
    }

    /**
     * Makes the main application window visible, starting the game for the user.
     */
    public void start() {
        showMainMenu();
        gameFrame.setVisible(true);
    }

    /**
     * Creates the MainMenuPanel and attaches all necessary listeners to its buttons.
     */
    private void setupMainMenu() {
        mainMenuPanel = new MainMenuPanel(); // Initialize the member variable
        mainMenuPanel.addNewGameListener(e -> showDifficultySelection());
        mainMenuPanel.addDailyChallengeListener(e -> startDailyChallenge());
        mainMenuPanel.addTutorialListener(e -> showTutorial());
        mainMenuPanel.addAboutListener(e -> showAbout());
        gameFrame.addPanel(mainMenuPanel, "MainMenu");
    }

    /**
     * Switches the view to the main menu and ensures it can receive keyboard input for cheats.
     * THIS METHOD IS NOW FIXED AND ROBUST.
     */
    private void showMainMenu() {
        gameFrame.showPanel("MainMenu");
        // Must request focus AFTER panel is shown to ensure key binding works
        SwingUtilities.invokeLater(() -> {
            mainMenuPanel.requestFocusInWindow(); // Use the direct reference
        });
    }

    /**
     * Finalizes the game, stops the timer, and displays the appropriate win/loss screen.
     * THIS METHOD IS NOW FIXED.
     */
    private void endGame() {
        stopTimer();
        boolean isWin = gameBoard.getGameState() == GameBoard.GameState.WIN;
        if (isWin) {
            gameBoard.autoFlagBombsOnWin();
        }
        updateUI();

        // Create and display the final result panel
        ResultPanel resultPanel = new ResultPanel(isWin ? "You Won!" : "Game Over", isWin);
        resultPanel.addReturnToMenuListener(e -> returnToMainMenu());
        gameFrame.addPanel(resultPanel, "ResultScreen");
        gameFrame.showPanel("ResultScreen");
    }

    // --- All other methods are unchanged but provided for completeness ---
    private void showTutorial() { gameFrame.addPanel(new TutorialPanel(e -> showMainMenu()), "Tutorial"); gameFrame.showPanel("Tutorial"); }
    private void showAbout() { gameFrame.addPanel(new AboutPanel(e -> showMainMenu()), "About"); gameFrame.showPanel("About"); }
    private void showDifficultySelection() { DifficultySelectionPanel difficultyPanel = new DifficultySelectionPanel(); difficultyPanel.addEasyListener(e -> startNewGame(Difficulty.EASY, difficultyPanel.isNoiseGenSelected())); difficultyPanel.addMediumListener(e -> startNewGame(Difficulty.MEDIUM, difficultyPanel.isNoiseGenSelected())); difficultyPanel.addHardListener(e -> startNewGame(Difficulty.HARD, difficultyPanel.isNoiseGenSelected())); difficultyPanel.addCustomListener(e -> showCustomDialog(difficultyPanel.isNoiseGenSelected())); gameFrame.addPanel(difficultyPanel, "DifficultySelection"); gameFrame.showPanel("DifficultySelection"); }
    private void showCustomDialog(boolean useNoiseGen) { CustomGameDialog dialog = new CustomGameDialog(gameFrame, useNoiseGen); dialog.setVisible(true); if (dialog.isConfirmed()) { try { int rows = Math.min(24, Math.max(9, dialog.getRows())); int cols = Math.min(40, Math.max(9, dialog.getCols())); int bombs = useNoiseGen ? 0 : Math.min(rows * cols - 9, Math.max(1, dialog.getBombs())); startNewGame(rows, cols, bombs, useNoiseGen); } catch (NumberFormatException e) { JOptionPane.showMessageDialog(gameFrame, "Invalid input. Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE); } } }
    private void startNewGame(Difficulty difficulty, boolean useNoiseGen) { this.currentDifficulty = difficulty; if (useNoiseGen) { this.gameBoard = DailyChallengeGenerator.generateNoiseBoard(difficulty.getRows(), difficulty.getCols(), new Random().nextLong(), difficulty); } else { this.gameBoard = new GameBoard(difficulty.getRows(), difficulty.getCols(), difficulty.getBombCount(), difficulty); } initializeGame(); }
    private void startNewGame(int rows, int cols, int bombs, boolean useNoiseGen) { this.currentDifficulty = Difficulty.CUSTOM; if (useNoiseGen) { this.gameBoard = DailyChallengeGenerator.generateNoiseBoard(rows, cols, new Random().nextLong(), currentDifficulty); } else { this.gameBoard = new GameBoard(rows, cols, bombs, currentDifficulty); } initializeGame(); }
    private void startDailyChallenge() { this.currentDifficulty = Difficulty.HARD; this.gameBoard = DailyChallengeGenerator.generate(); initializeGame(); }
    private void initializeGame() { this.player = new Player(); this.cheatMode = false; this.revealModeActive = false; this.bombModeActive = false; setCursor(Cursor.getDefaultCursor()); gameScreenPanel = new GameScreenPanel(gameBoard, player, this); gameFrame.addPanel(gameScreenPanel, "GameScreen"); gameFrame.showPanel("GameScreen"); GamePanel panel = gameScreenPanel.getGamePanel(); panel.addGridMouseListener(new MouseAdapter() { @Override public void mousePressed(MouseEvent e) { handleGridClick(e); } }); panel.addGridMouseMotionListener(new MouseMotionAdapter() { @Override public void mouseMoved(MouseEvent e) { if (revealModeActive || bombModeActive) panel.setMouseHoverPoint(e.getPoint()); } }); panel.addKeyListener(new KeyAdapter() { @Override public void keyTyped(KeyEvent e) { if (e.getKeyChar() == 'c' || e.getKeyChar() == 'C') { toggleCheatMode(); } } }); panel.setFocusable(true); panel.requestFocusInWindow(); startTimer(); }
    private void handleGridClick(MouseEvent e) { if (gameBoard.getGameState() != GameBoard.GameState.PLAYING) return; GamePanel panel = gameScreenPanel.getGamePanel(); int cellSize = Math.min(panel.getWidth() / gameBoard.getCols(), panel.getHeight() / gameBoard.getRows()); int xOffset = (panel.getWidth() - cellSize * gameBoard.getCols()) / 2; int yOffset = (panel.getHeight() - cellSize * gameBoard.getRows()) / 2; int c = (e.getX() - xOffset) / cellSize; int r = (e.getY() - yOffset) / cellSize; PowerUp foundPowerUp = null; if (revealModeActive) { if (player.useReveal()) gameBoard.revealArea(r, c); toggleRevealMode(); } else if (bombModeActive) { if (player.useBomb()) gameBoard.clearArea(r, c, player); toggleBombMode(); } else { if (SwingUtilities.isLeftMouseButton(e)) { foundPowerUp = gameBoard.clickCell(r, c, player); } else if (SwingUtilities.isRightMouseButton(e)) { gameBoard.toggleFlag(r, c); } } if (foundPowerUp != null) { JOptionPane.showMessageDialog(gameFrame, "You collected a " + foundPowerUp.getType().toString() + " Power-Up!", "Power-Up Collected!", JOptionPane.INFORMATION_MESSAGE); } updateUI(); if (gameBoard.getGameState() != GameBoard.GameState.PLAYING) endGame(); }
    public void returnToMainMenu() { stopTimer(); showMainMenu(); }
    public void usePlayerHeal() { if (player.getHealth() < Player.MAX_HEALTH) { player.useHeal(); updateUI(); } }
    public void activateRevealMode() { if (bombModeActive) toggleBombMode(); if(player.getRevealCount() > 0 || revealModeActive) toggleRevealMode(); }
    public void activateBombMode() { if (revealModeActive) toggleRevealMode(); if(player.getBombCount() > 0 || bombModeActive) toggleBombMode(); }
    private void toggleRevealMode() { revealModeActive = !revealModeActive; gameScreenPanel.getGamePanel().setRevealMode(revealModeActive); setCursor(revealModeActive ? Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) : Cursor.getDefaultCursor()); }
    private void toggleBombMode() { bombModeActive = !bombModeActive; gameScreenPanel.getGamePanel().setBombMode(bombModeActive); setCursor(bombModeActive ? Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) : Cursor.getDefaultCursor()); }
    private void startTimer() { timeElapsed = 0; timer = new Timer(1000, e -> { timeElapsed++; gameScreenPanel.getTopPanel().updateTimer(timeElapsed); }); timer.start(); }
    private void stopTimer() { if (timer != null && timer.isRunning()) timer.stop(); }
    private void updateUI() { if (gameScreenPanel != null) { gameScreenPanel.getSidePanel().update(player); gameScreenPanel.getTopPanel().updateFlags(gameBoard.getFlagsPlaced(), gameBoard.getBombCount()); gameScreenPanel.repaint(); } }
    private void toggleCheatMode() { cheatMode = !cheatMode; gameScreenPanel.getGamePanel().setCheatMode(cheatMode); }
    private void setCursor(Cursor cursor) { gameFrame.setCursor(cursor); }
    public Player getPlayer() { return this.player; }
}
