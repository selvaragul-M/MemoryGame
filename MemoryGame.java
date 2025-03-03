import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGame extends JFrame {
    private static final int ROWS = 4;
    private static final int COLS = 4;
    private static final int TILE_SIZE = 100;
    private static final int TOTAL_PAIRS = (ROWS * COLS) / 2;

    private JButton[][] tiles = new JButton[ROWS][COLS];
    private int[][] values = new int[ROWS][COLS];
    private boolean[][] revealed = new boolean[ROWS][COLS];
    private int firstRow = -1;
    private int firstCol = -1;
    private int pairsFound = 0;
    private boolean canClick = true;

    private JLabel statusLabel;
    private int moves = 0;

    public MemoryGame() {
        setTitle("Memory Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize game board
        JPanel boardPanel = new JPanel(new GridLayout(ROWS, COLS));
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                tiles[i][j] = createTile(i, j);
                boardPanel.add(tiles[i][j]);
            }
        }
        // Status panel
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("Moves: 0");
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> initNewGame());

        statusPanel.add(statusLabel);
        statusPanel.add(newGameButton);

        add(boardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        initNewGame();

        setSize(COLS * TILE_SIZE, ROWS * TILE_SIZE + 50);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private JButton createTile(int row, int col) {
        JButton tile = new JButton();
        tile.setFont(new Font("Arial", Font.BOLD, 24));

        tile.addActionListener(e -> {
            if (canClick && !revealed[row][col]) {
                flipTile(row, col);
            }
        });
        return tile;
    }

    private void flipTile(int row, int col) {
        // First card flipped
        if (firstRow == -1) {
            firstRow = row;
            firstCol = col;
            updateTile(row, col, true);
            return;
        }

        // Same tile clicked twice
        if (row == firstRow && col == firstCol) {
            return;
        }
        // Second card flipped
        moves++;
        updateTile(row, col, true);
        statusLabel.setText("Moves: " + moves);

        // Check if the two cards match
        if (values[firstRow][firstCol] == values[row][col]) {
            // Match found
            pairsFound++;
            firstRow = -1;
            firstCol = -1;

            // Check if game is over
            if (pairsFound == TOTAL_PAIRS) {
                JOptionPane.showMessageDialog(this, "You won in " + moves + " moves!");
            }
        } else {
            // No match, flip cards back after delay
            canClick = false;
            Timer timer = new Timer(800, e -> {
                updateTile(firstRow, firstCol, false);
                updateTile(row, col, false);
                firstRow = -1;
                firstCol = -1;
                canClick = true;
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void updateTile(int row, int col, boolean isRevealed) {
        revealed[row][col] = isRevealed;
        tiles[row][col].setText(isRevealed ? String.valueOf(values[row][col]) : "");
        tiles[row][col].setBackground(isRevealed ? new Color(200, 220, 255) : UIManager.getColor("Button.background"));
    }

    private void initNewGame() {
        // Generate and shuffle pairs
        List<Integer> valueList = new ArrayList<>();
        for (int i = 0; i < TOTAL_PAIRS; i++) {
            valueList.add(i + 1);
            valueList.add(i + 1);
        }
        Collections.shuffle(valueList);

        // Assign values to grid
        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                values[i][j] = valueList.get(index++);
                revealed[i][j] = false;
                updateTile(i, j, false);
            }
        }

        // Reset game state
        firstRow = -1;
        firstCol = -1;
        pairsFound = 0;
        moves = 0;
        canClick = true;
        statusLabel.setText("Moves: 0");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemoryGame());
    }
}
