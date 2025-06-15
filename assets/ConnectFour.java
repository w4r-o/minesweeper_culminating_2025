import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.List;
import javax.sound.sampled.*; // Not explicitly used, but good practice for URL/AudioSystem
import javax.swing.*;

/**
 * ConnectFour class implementing the game logic, GUI, and extra features.
 * This class handles the game state, drawing the board, user input (mouse and keyboard),
 * sound effects with volume control, piece customization, and a bombing mechanic.
 *
 * @author Aaorn Jiang github.com/w4r-o
 * @version 1.7 // Final formatting pass
 */
public class ConnectFour extends JPanel implements ActionListener, MouseListener, KeyListener {

    // --- Game Constants ---
    final int BANANA_PLAYER = -1;       // Identifier for Player 1
    final int STRAWBERRY_PLAYER = 1;    // Identifier for Player 2
    final int EMPTY = 0;                // Identifier for an empty cell on the board

    // --- Game Board Dimensions ---
    int SQUARE_SIZE = 60;               // Initial/default size of each game cell, updated dynamically
    final int GRID_ROWS = 6;
    final int GRID_COLS = 7;
    final int PREVIEW_AREA_HEIGHT_IN_SQUARES = 1; // Height of the preview area above the grid
    int actualPreviewAreaHeight;        // Calculated pixel height of the preview area
    final int BORDER_THICKNESS = 4;     // Thickness of the visual border around the game grid

    // --- Game State Variables ---
    int[][] board;
    int currentPlayer;
    int currentColumnPreview;           // 0-indexed column for arrow key piece preview
    boolean gameOver;
    int player1Wins = 0;
    int player2Wins = 0;
    int player1BombsLeft = 3;
    int player2BombsLeft = 3;

    // --- Piece Images ---
    Image player1PieceImage;
    Image player2PieceImage;
    String p1ImageName = "banana.gif";  // Default image for Player 1
    String p2ImageName = "strawberry.gif"; // Default image for Player 2

    // List of all available image filenames for pieces.
    List<String> allPieceImageFiles = List.of(
            "banana.gif", "strawberry.gif", "cantoloupe.gif", "grape.gif",
            "tomato.gif", "watermelon.gif"
            // Add other .gif files here if you have more options
    );

    // --- UI Components ---
    Image offScreenImage;               // For double buffering
    Graphics offScreenBuffer;
    JLabel player1ScoreLabel, player2ScoreLabel, player1BombsLabel, player2BombsLabel, currentPlayerLabel;
    JPanel infoPanel;                   // Top panel for game information
    JTextArea howToPlayArea;
    JScrollPane instructionsScrollPane; // Right panel for instructions
    JPanel mainGamePanel;               // Center panel for the game grid
    JButton playAgainButton;            // Bottom button to restart game

    // --- Audio ---
    Clip backgroundMusicClip, dropSoundClip, gameOverSoundClip, kaboomSoundClip;
    FloatControl bgMusicVolumeControl, winSoundVolumeControl, kaboomVolumeControl, dropVolumeControl;

    // --- Debug Flag ---
    private static final boolean DEBUG = false; // Set true for console logs

    /**
     * Constructor: Initializes UI, game state, resources, and listeners.
     */
    public ConnectFour() {
        if (DEBUG) System.out.println("ConnectFour Constructor - Start");
        setLayout(new BorderLayout());

        // --- mainGamePanel: Core game drawing and interaction area ---
        mainGamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGameArea(g);
            }
            @Override
            public Dimension getPreferredSize() { // Helps frame.pack()
                int cs = Math.max(30, SQUARE_SIZE);
                int ph = PREVIEW_AREA_HEIGHT_IN_SQUARES * cs;
                return new Dimension(GRID_COLS * cs + 2 * BORDER_THICKNESS,
                                     (GRID_ROWS * cs) + ph + BORDER_THICKNESS);
            }
        };
        mainGamePanel.setBackground(new Color(220, 220, 220));
        mainGamePanel.addMouseListener(this);
        mainGamePanel.addKeyListener(this);
        mainGamePanel.setFocusable(true);
        add(mainGamePanel, BorderLayout.CENTER);

        // --- infoPanel: Top panel for game status ---
        infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        player1ScoreLabel = new JLabel(); player2ScoreLabel = new JLabel();
        player1BombsLabel = new JLabel(); player2BombsLabel = new JLabel();
        currentPlayerLabel = new JLabel();
        infoPanel.add(player1ScoreLabel); infoPanel.add(player1BombsLabel);
        infoPanel.add(new JLabel(" | ")); infoPanel.add(currentPlayerLabel);
        infoPanel.add(new JLabel(" | ")); infoPanel.add(player2ScoreLabel);
        infoPanel.add(player2BombsLabel);
        add(infoPanel, BorderLayout.NORTH);

        // --- instructionsScrollPane: Right panel for "How to Play" ---
        howToPlayArea = new JTextArea(
            "How to Play Connect Four:\n\n" +
            "1. Goal: Get 4 pieces in a row\n   (any direction).\n" +
            "2. Turns: Alternate dropping pieces.\n" +
            "3. Drop: Click column or use arrows\n   + Down/Enter/Space.\n" +
            "4. Bombs (3/game): Right-click\n   your own piece to clear a 3x3\n   area. Pieces above will fall."
        );
        howToPlayArea.setEditable(false); howToPlayArea.setLineWrap(true);
        howToPlayArea.setWrapStyleWord(true); howToPlayArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        howToPlayArea.setBackground(getBackground()); // Match container background
        instructionsScrollPane = new JScrollPane(howToPlayArea);
        instructionsScrollPane.setPreferredSize(new Dimension(180, 100)); // Default width
        instructionsScrollPane.setBorder(BorderFactory.createTitledBorder("How to Play"));
        add(instructionsScrollPane, BorderLayout.EAST);

        // --- playAgainButton: Bottom panel button ---
        playAgainButton = new JButton("Play Again");
        playAgainButton.setActionCommand("New"); // Reuses "New Game" action
        playAgainButton.addActionListener(this);
        playAgainButton.setVisible(false);      // Hidden until game over
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(playAgainButton);
        add(bottomPanel, BorderLayout.SOUTH);

        board = new int[GRID_ROWS][GRID_COLS]; // Initialize game board
        loadPieceImages();                     // Load initial piece images
        loadSounds();                          // Load sound effects
        newGame();                             // Setup initial game state, starts music

        mainGamePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (DEBUG) System.out.println("ConnectFour Constructor - End.");
        SwingUtilities.invokeLater(mainGamePanel::requestFocusInWindow);
    }

    /** Loads player piece images based on current selections. */
    private void loadPieceImages() {
        if (DEBUG) System.out.println("Loading piece images: P1=" + p1ImageName + ", P2=" + p2ImageName);
        boolean p1Loaded = false, p2Loaded = false;
        try {
            URL p1URL = getClass().getResource(p1ImageName);
            if (p1URL == null) { if (DEBUG) System.err.println(p1ImageName + " P1 NF"); }
            else { player1PieceImage = Toolkit.getDefaultToolkit().getImage(p1URL); p1Loaded = true; }

            URL p2URL = getClass().getResource(p2ImageName);
            if (p2URL == null) { if (DEBUG) System.err.println(p2ImageName + " P2 NF"); }
            else { player2PieceImage = Toolkit.getDefaultToolkit().getImage(p2URL); p2Loaded = true; }

            MediaTracker tracker = new MediaTracker(mainGamePanel);
            if (p1Loaded) tracker.addImage(player1PieceImage, 0);
            if (p2Loaded) tracker.addImage(player2PieceImage, 1);
            if (p1Loaded || p2Loaded) tracker.waitForAll();

            if (p1Loaded && player1PieceImage.getWidth(null) == -1) { if (DEBUG) System.err.println(p1ImageName + " P1 load fail"); p1Loaded = false; player1PieceImage = null; }
            if (p2Loaded && player2PieceImage.getWidth(null) == -1) { if (DEBUG) System.err.println(p2ImageName + " P2 load fail"); p2Loaded = false; player2PieceImage = null; }

            if (!p1Loaded || !p2Loaded) JOptionPane.showMessageDialog(this, "Error: One or more piece images did not load.", "Image Error", JOptionPane.WARNING_MESSAGE);
            else if (DEBUG) System.out.println("Images loaded. P1w:" + (p1Loaded ? player1PieceImage.getWidth(null) : -1) + ", P2w:" + (p2Loaded ? player2PieceImage.getWidth(null) : -1));
        } catch (Exception e) {
            if (DEBUG) e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Exception loading images:" + e.getMessage(), "Image Error", JOptionPane.ERROR_MESSAGE);
            player1PieceImage = null; player2PieceImage = null;
        }
        updateInfoPanel();
        if (mainGamePanel != null) mainGamePanel.repaint();
    }

    /** Loads all sound clips and retrieves their volume controls. */
    private void loadSounds() {
        if (DEBUG) System.out.println("loadSounds()");
        backgroundMusicClip = loadClip("bg_music.wav");
        dropSoundClip = loadClip("drop_sound.wav");
        gameOverSoundClip = loadClip("winningsfx.wav");
        kaboomSoundClip = loadClip("kaboom.wav");

        if (backgroundMusicClip != null && backgroundMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) bgMusicVolumeControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
        if (gameOverSoundClip != null && gameOverSoundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) winSoundVolumeControl = (FloatControl) gameOverSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
        if (kaboomSoundClip != null && kaboomSoundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) kaboomVolumeControl = (FloatControl) kaboomSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
        if (dropSoundClip != null && dropSoundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) dropVolumeControl = (FloatControl) dropSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
    }

    /** Sets initial/default volumes for sound clips. */
    private void setInitialVolumes() {
        setVolume(bgMusicVolumeControl, 0.95f);
        setVolume(winSoundVolumeControl, 0.9f);
        setVolume(kaboomVolumeControl, 0.8f);
        setVolume(dropVolumeControl, 0.65f);
    }

    /** Sets the volume of a FloatControl using a percentage (0.0 to 1.0). */
    private void setVolume(FloatControl ctrl, float pct) {
        if (ctrl == null) return;
        try {
            float min = ctrl.getMinimum();
            float max = ctrl.getMaximum();
            pct = Math.max(0f, Math.min(1f, pct)); // Clamp percent
            ctrl.setValue(min + (max - min) * pct);
        } catch (IllegalArgumentException e) {
            if (DEBUG) System.err.println("Error setting volume for " + ctrl.getType() + ": " + e.getMessage());
        }
    }

    /** Loads a sound clip from the classpath. Returns null on error. */
    private Clip loadClip(String fn) {
        try {
            URL u = getClass().getResource(fn);
            if (u == null) { System.err.println("Sound file not found: " + fn); return null; }
            AudioInputStream ain = AudioSystem.getAudioInputStream(u);
            AudioFormat fmt = ain.getFormat();
            DataLine.Info i = new DataLine.Info(Clip.class, fmt);
            if (!AudioSystem.isLineSupported(i)) { System.err.println("Audio line not supported for: " + fmt); return null; }
            Clip c = (Clip) AudioSystem.getLine(i);
            c.open(ain);
            return c;
        } catch (Exception e) {
            if (DEBUG) { System.err.println("Error loading sound " + fn + ": " + e.getMessage()); e.printStackTrace(); }
            // Optionally show JOptionPane for critical sounds, or just log for minor ones
            return null;
        }
    }

    /** Plays a sound clip, with optional looping and volume adjustment. */
    private void playSound(Clip cl, boolean loop, FloatControl vc, float vp) {
        if (cl != null) {
            if (cl.isRunning()) cl.stop();
            cl.setFramePosition(0);
            if (vc != null) setVolume(vc, vp); // Use provided volume control
            else if (cl.isControlSupported(FloatControl.Type.MASTER_GAIN)) setVolume((FloatControl) cl.getControl(FloatControl.Type.MASTER_GAIN), vp); // Use clip's own
            if (loop) cl.loop(Clip.LOOP_CONTINUOUSLY);
            else cl.start();
        }
    }

    /** Stops a playing sound clip. */
    private void stopSound(Clip cl) {
        if (cl != null && cl.isRunning()) cl.stop();
    }

    /** Creates and returns the game's JMenuBar. */
    public JMenuBar createGameMenuBar() {
        JMenuItem newOpt = new JMenuItem("New Game (Replay)");
        newOpt.setActionCommand("New"); newOpt.addActionListener(this);

        JMenu changePieceMenu = new JMenu("Change Pieces");
        String p1DispName = p1ImageName.substring(0, p1ImageName.lastIndexOf('.'));
        JMenu p1Menu = new JMenu("Player 1 (" + p1DispName + ")");
        for (String imgName : allPieceImageFiles) {
            JMenuItem item = new JMenuItem(imgName.substring(0, imgName.lastIndexOf('.')));
            item.setActionCommand("P1_PIECE_" + imgName); item.addActionListener(this);
            p1Menu.add(item);
        }
        changePieceMenu.add(p1Menu);

        String p2DispName = p2ImageName.substring(0, p2ImageName.lastIndexOf('.'));
        JMenu p2Menu = new JMenu("Player 2 (" + p2DispName + ")");
        for (String imgName : allPieceImageFiles) {
            JMenuItem item = new JMenuItem(imgName.substring(0, imgName.lastIndexOf('.')));
            item.setActionCommand("P2_PIECE_" + imgName); item.addActionListener(this);
            p2Menu.add(item);
        }
        changePieceMenu.add(p2Menu);

        JMenuItem exitOpt = new JMenuItem("Exit");
        exitOpt.setActionCommand("Exit"); exitOpt.addActionListener(this);

        JMenu gameMenu = new JMenu("Menu");
        gameMenu.add(newOpt); gameMenu.add(changePieceMenu);
        gameMenu.addSeparator(); gameMenu.add(exitOpt);

        JMenuBar mainMenu = new JMenuBar();
        mainMenu.add(gameMenu);
        return mainMenu;
    }

    /** Updates the JMenuBar to reflect current piece selections in menu titles. */
    private void updateMenuBar() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.setJMenuBar(createGameMenuBar());
            topFrame.revalidate();
            topFrame.repaint();
        }
    }

    /** Handles actions from menu items and buttons. */
    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        if (DEBUG) System.out.println("Action: " + cmd);
        if ("New".equals(cmd)) newGame();
        else if ("Exit".equals(cmd)) System.exit(0);
        else if (cmd.startsWith("P1_PIECE_")) {
            String sel = cmd.substring("P1_PIECE_".length());
            if (!sel.equals(p2ImageName)) { p1ImageName = sel; loadPieceImages(); updateMenuBar(); }
            else JOptionPane.showMessageDialog(mainGamePanel, "Player 1 cannot use Player 2's current piece!", "Piece Conflict", JOptionPane.WARNING_MESSAGE);
        } else if (cmd.startsWith("P2_PIECE_")) {
            String sel = cmd.substring("P2_PIECE_".length());
            if (!sel.equals(p1ImageName)) { p2ImageName = sel; loadPieceImages(); updateMenuBar(); }
            else JOptionPane.showMessageDialog(mainGamePanel, "Player 2 cannot use Player 1's current piece!", "Piece Conflict", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Initializes or resets the game to a new state. */
    public void newGame() {
        if (DEBUG) System.out.println("newGame()");
        stopSound(gameOverSoundClip); stopSound(backgroundMusicClip);
        currentPlayer = BANANA_PLAYER; clearBoard(); gameOver = false;
        currentColumnPreview = GRID_COLS / 2; player1BombsLeft = 3; player2BombsLeft = 3;
        setInitialVolumes(); updateInfoPanel(); calculateDynamicSizes();
        playAgainButton.setVisible(false);
        mainGamePanel.repaint();
        SwingUtilities.invokeLater(mainGamePanel::requestFocusInWindow);
        playSound(backgroundMusicClip, true, bgMusicVolumeControl, 0.9f);
    }

    /** Updates labels in the infoPanel with current game status. */
    private void updateInfoPanel() {
        String p1N = p1ImageName.substring(0, p1ImageName.lastIndexOf('.'));
        String p2N = p2ImageName.substring(0, p2ImageName.lastIndexOf('.'));
        player1ScoreLabel.setText("P1(" + p1N + ") W:" + player1Wins);
        player2ScoreLabel.setText("P2(" + p2N + ") W:" + player2Wins);
        player1BombsLabel.setText("B:" + player1BombsLeft);
        player2BombsLabel.setText("B:" + player2BombsLeft);
        String t = "Turn: " + (currentPlayer == BANANA_PLAYER ? p1N : p2N);
        currentPlayerLabel.setText(t);
    }

    /** Calculates SQUARE_SIZE based on mainGamePanel's current dimensions. */
    private void calculateDynamicSizes() {
        int pH = mainGamePanel.getHeight(), pW = mainGamePanel.getWidth();
        if (pH <= 0 || pW <= 0) SQUARE_SIZE = 60; // Default if panel not sized
        else {
            int aH = pH - BORDER_THICKNESS; // Available height in mainGamePanel
            int aW = pW - 2 * BORDER_THICKNESS; // Available width in mainGamePanel
            int sH = aH / (GRID_ROWS + PREVIEW_AREA_HEIGHT_IN_SQUARES);
            int sW = aW / GRID_COLS;
            SQUARE_SIZE = Math.max(10, Math.min(sH, sW)); // Ensure minimum size
        }
        actualPreviewAreaHeight = PREVIEW_AREA_HEIGHT_IN_SQUARES * SQUARE_SIZE;
    }

    /** Clears the game board. */
    public void clearBoard() {
        for (int r = 0; r < GRID_ROWS; r++) for (int c = 0; c < GRID_COLS; c++) board[r][c] = EMPTY;
    }

    /** Finds the lowest available row in a column. Returns -1 if full. */
    public int findNextAvailableRow(int col) {
        if (col < 0 || col >= GRID_COLS) return -1;
        for (int r = GRID_ROWS - 1; r >= 0; r--) if (board[r][col] == EMPTY) return r;
        return -1;
    }

    /** Checks for a winner from the last placed piece. */
    public int checkForWinner(int lr, int lc) {
        if (lr < 0 || lc < 0 || lr >= GRID_ROWS || lc >= GRID_COLS) return EMPTY;
        int p = board[lr][lc]; if (p == EMPTY) return EMPTY;
        int[][] D = {{0, 1}, /* Horizontal */ {1, 0}, /* Vert */ {1, 1}, /* Diagonal NW to SE */ {1, -1}}; /* NE to SW */
        for (int[] d : D) {
            int ct = 1;
            for (int i = 1; i < 4; i++) { int r = lr + d[0] * i, c = lc + d[1] * i; if (r >= 0 && r < GRID_ROWS && c >= 0 && c < GRID_COLS && board[r][c] == p) ct++; else break; }
            for (int i = 1; i < 4; i++) { int r = lr - d[0] * i, c = lc - d[1] * i; if (r >= 0 && r < GRID_ROWS && c >= 0 && c < GRID_COLS && board[r][c] == p) ct++; else break; }
            if (ct >= 4) return p;
        }
        return EMPTY;
    }

    /** Handles a player's attempt to use a bomb. */
    private void attemptBomb(int row, int col) {
        if (DEBUG) System.out.println("attemptBomb(" + row + "," + col + ") P" + (currentPlayer == BANANA_PLAYER ? 1 : 2));
        if (gameOver) return;
        if (board[row][col] != currentPlayer) {
            if (board[row][col] != EMPTY) JOptionPane.showMessageDialog(mainGamePanel, "Bomb your own pieces!", "Bomb Err", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String p1NameClean = p1ImageName.substring(0, p1ImageName.lastIndexOf('.'));
        String p2NameClean = p2ImageName.substring(0, p2ImageName.lastIndexOf('.'));

        if (currentPlayer == BANANA_PLAYER && player1BombsLeft <= 0) { JOptionPane.showMessageDialog(mainGamePanel, "P1 (" + p1NameClean + ") is out of bombs!", "Out of Bombs", JOptionPane.WARNING_MESSAGE); return; }
        else if (currentPlayer == STRAWBERRY_PLAYER && player2BombsLeft <= 0) { JOptionPane.showMessageDialog(mainGamePanel, "P2 (" + p2NameClean + ") is out of bombs!", "Out of Bombs", JOptionPane.WARNING_MESSAGE); return; }

        if (currentPlayer == BANANA_PLAYER) player1BombsLeft--; else player2BombsLeft--;

        playSound(kaboomSoundClip, false, kaboomVolumeControl, 0.8f);
        for (int ro = -1; ro <= 1; ro++) for (int co = -1; co <= 1; co++) {
            int tr = row + ro, tc = col + co;
            if (tr >= 0 && tr < GRID_ROWS && tc >= 0 && tc < GRID_COLS) board[tr][tc] = EMPTY;
        }
        dropPiecesAfterExplosion();
        updateInfoPanel();
        currentPlayer *= -1;
        updateInfoPanel();
        mainGamePanel.repaint();
    }

    /** Makes pieces fall down after an explosion. */
    private void dropPiecesAfterExplosion() {
        if (DEBUG) System.out.println("dropPiecesAfterExplosion()");
        for (int c = 0; c < GRID_COLS; c++) {
            int lowestAvail = GRID_ROWS - 1;
            for (int r = GRID_ROWS - 1; r >= 0; r--) {
                if (board[r][c] != EMPTY) {
                    if (r < lowestAvail) {
                        if (DEBUG) System.out.println(" Drop (" + r + "," + c + ")to(" + lowestAvail + "," + c + ")");
                        board[lowestAvail][c] = board[r][c];
                        board[r][c] = EMPTY;
                    }
                    lowestAvail--;
                }
            }
        }
    }

    /** Processes a player's turn when dropping a piece. */
    public void processPlayerTurn(int colToDrop) {
        if (DEBUG) System.out.println("processPlayerTurn(" + colToDrop + ") P" + (currentPlayer == BANANA_PLAYER ? 1 : 2));
        if (gameOver) { playAgainButton.setVisible(true); JOptionPane.showMessageDialog(mainGamePanel, "Game Over! Click 'Play Again'.", "Game Over", JOptionPane.INFORMATION_MESSAGE); return; }
        if (colToDrop < 0 || colToDrop >= GRID_COLS) return;
        int tr = findNextAvailableRow(colToDrop);
        if (tr == -1) { JOptionPane.showMessageDialog(mainGamePanel, "Column is full!", "Column Full", JOptionPane.WARNING_MESSAGE); return; }

        board[tr][colToDrop] = currentPlayer;
        playSound(dropSoundClip, false, dropVolumeControl, 0.65f);
        mainGamePanel.repaint(); // Draw piece before win dialog

        int winner = checkForWinner(tr, colToDrop);
        if (winner != EMPTY) {
            gameOver = true; stopSound(backgroundMusicClip); playSound(gameOverSoundClip, false, winSoundVolumeControl, 0.9f);
            if (winner == BANANA_PLAYER) player1Wins++; else player2Wins++;
            updateInfoPanel(); playAgainButton.setVisible(true);
            String wN = (winner == BANANA_PLAYER ? p1ImageName : p2ImageName).substring(0, (winner == BANANA_PLAYER ? p1ImageName : p2ImageName).lastIndexOf('.'));
            JOptionPane.showMessageDialog(mainGamePanel, wN + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        } else {
            boolean full = true; for (int c = 0; c < GRID_COLS; c++) if (board[0][c] == EMPTY) { full = false; break; }
            if (full) { gameOver = true; stopSound(backgroundMusicClip); playSound(gameOverSoundClip, false, winSoundVolumeControl, 0.7f); playAgainButton.setVisible(true); JOptionPane.showMessageDialog(mainGamePanel, "It's a Draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE); }
            else { currentPlayer *= -1; updateInfoPanel(); }
        }
        currentColumnPreview = GRID_COLS / 2;
    }

    // --- Event Handlers for Mouse and Keyboard Input ---
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver) return;
        int cX = e.getX(), cY = e.getY();
        int gridVisualTopY = actualPreviewAreaHeight + BORDER_THICKNESS; // Y where grid drawing starts

        if (cY >= 0 && cY < actualPreviewAreaHeight) { // Click in Preview Area
            if (SwingUtilities.isLeftMouseButton(e)) processPlayerTurn(currentColumnPreview);
        } else if (cY >= gridVisualTopY) { // Click in Grid Area
            int col = (cX - BORDER_THICKNESS) / SQUARE_SIZE;
            int row = (cY - gridVisualTopY) / SQUARE_SIZE;
            if (col >= 0 && col < GRID_COLS) { // Valid column clicked
                if (SwingUtilities.isLeftMouseButton(e)) processPlayerTurn(col);
                else if (SwingUtilities.isRightMouseButton(e)) {
                    if (row >= 0 && row < GRID_ROWS) attemptBomb(row, col); // Valid row for bomb
                }
            }
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) { if (!gameOver) mainGamePanel.requestFocusInWindow(); }
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void keyPressed(KeyEvent kp) {
        if (gameOver) return;
        int kc = kp.getKeyCode();
        boolean repaintNeeded = false;
        if (kc == KeyEvent.VK_LEFT) { if (currentColumnPreview > 0) { currentColumnPreview--; repaintNeeded = true; } }
        else if (kc == KeyEvent.VK_RIGHT) { if (currentColumnPreview < GRID_COLS - 1) { currentColumnPreview++; repaintNeeded = true; } }
        else if (kc == KeyEvent.VK_DOWN || kc == KeyEvent.VK_ENTER || kc == KeyEvent.VK_SPACE) processPlayerTurn(currentColumnPreview);
        if (repaintNeeded) mainGamePanel.repaint();
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    /** Custom drawing method for the game area (grid, pieces, preview). */
    private void drawGameArea(Graphics g) {
        calculateDynamicSizes();
        if (mainGamePanel.getWidth() <= 0 || mainGamePanel.getHeight() <= 0) return;
        if (offScreenImage == null || offScreenImage.getWidth(null) != mainGamePanel.getWidth() || offScreenImage.getHeight(null) != mainGamePanel.getHeight()) {
            offScreenImage = mainGamePanel.createImage(mainGamePanel.getWidth(), mainGamePanel.getHeight());
            if (offScreenImage == null) { if(DEBUG)System.err.println("DrawArea: offScreenImage null");return; }
            offScreenBuffer = offScreenImage.getGraphics();
            if (offScreenBuffer == null) { if(DEBUG)System.err.println("DrawArea: offScreenBuffer null"); return; }
        }

        offScreenBuffer.setColor(mainGamePanel.getBackground());
        offScreenBuffer.fillRect(0, 0, mainGamePanel.getWidth(), mainGamePanel.getHeight());

        int gridDrawY = actualPreviewAreaHeight + BORDER_THICKNESS; // Y where blue grid starts
        offScreenBuffer.setColor(new Color(0, 76, 153)); // Connect 4 Blue
        offScreenBuffer.fillRect(BORDER_THICKNESS, gridDrawY, GRID_COLS * SQUARE_SIZE, GRID_ROWS * SQUARE_SIZE);

        for (int r = 0; r < GRID_ROWS; r++) {
            for (int c = 0; c < GRID_COLS; c++) {
                int x = c * SQUARE_SIZE + BORDER_THICKNESS;
                int y = r * SQUARE_SIZE + gridDrawY;
                offScreenBuffer.setColor(Color.WHITE);
                offScreenBuffer.fillOval(x + 2, y + 2, SQUARE_SIZE - 4, SQUARE_SIZE - 4); // Inset circles
                if (board[r][c] != EMPTY) {
                    Image pI = (board[r][c] == BANANA_PLAYER) ? player1PieceImage : player2PieceImage;
                    if (pI != null && pI.getWidth(null) > 0) offScreenBuffer.drawImage(pI, x, y, SQUARE_SIZE, SQUARE_SIZE, mainGamePanel);
                    else if (DEBUG) System.err.println("Draw: Img null for " + board[r][c] + "@" + r + "," + c);
                }
            }
        }
        if (!gameOver) {
            int pX = currentColumnPreview * SQUARE_SIZE + BORDER_THICKNESS;
            int pY = BORDER_THICKNESS; // Preview piece at top of mainGamePanel, after its top border
            Image pI = (currentPlayer == BANANA_PLAYER) ? player1PieceImage : player2PieceImage;
            if (pI != null && pI.getWidth(null) > 0) offScreenBuffer.drawImage(pI, pX, pY, SQUARE_SIZE, SQUARE_SIZE, mainGamePanel);
            else if (DEBUG) System.err.println("Draw: Preview Img null for P" + currentPlayer);
        }
        g.drawImage(offScreenImage, 0, 0, mainGamePanel); // Blit buffer to screen
    }

    /** Overridden paintComponent for the main ConnectFour JPanel (container). */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Important for Swing to paint children correctly
    }

    /** Main method to launch the Connect Four game application. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Connect Four Deluxe");
            ConnectFour p = new ConnectFour(); // The main game container panel
            f.setJMenuBar(p.createGameMenuBar());
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(p);
            f.pack(); // Pack first to get component preferred sizes

            // Calculate a more informed minimum size after packing
            Dimension gpPS = p.mainGamePanel.getPreferredSize();
            Dimension ipPS = p.infoPanel.getPreferredSize();
            Dimension isPS = p.instructionsScrollPane.getPreferredSize();
            Dimension pbPS = p.playAgainButton.getPreferredSize();

            int frameInsetsWidth = f.getInsets().left + f.getInsets().right;
            int frameInsetsHeight = f.getInsets().top + f.getInsets().bottom;
            int playAgainHeight = (pbPS.height == 0 && p.playAgainButton.getText() != null) ? 40 : pbPS.height; // Estimate if not sized

            int minW = gpPS.width + isPS.width + frameInsetsWidth + 30; // Add some padding
            int minH = gpPS.height + ipPS.height + playAgainHeight + frameInsetsHeight + 30; // Add some padding

            f.setMinimumSize(new Dimension(minW, minH));
            f.setLocationRelativeTo(null); // Center on screen
            try { URL u = ConnectFour.class.getResource("banana.gif"); if (u != null) f.setIconImage(Toolkit.getDefaultToolkit().getImage(u)); } catch (Exception ex) {}
            f.addComponentListener(new java.awt.event.ComponentAdapter() { public void componentResized(java.awt.event.ComponentEvent ev) { p.mainGamePanel.repaint(); } });
            f.setVisible(true);
            SwingUtilities.invokeLater(p.mainGamePanel::requestFocusInWindow); // Request focus for keyboard input
        });
    }
}