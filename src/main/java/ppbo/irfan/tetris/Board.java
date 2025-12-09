package ppbo.irfan.tetris;

public class Board {

    private static final int COLS = 10;
    private static final int ROWS = 20;
    private boolean[][] statusBlock;

    public Board() {
        statusBlock = new boolean[ROWS][COLS];

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                statusBlock[row][col] = false;
            }
        }
    }

    public boolean isValidCell(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    public boolean isAvailable(int row, int col) {
        if (isValidCell(row, col) && !statusBlock[row][col]) {
            return true;
        }
        return false;
    }

    public void fillCell(int row, int col) {
        if (isAvailable(row, col)) {
            statusBlock[row][col] = true;
        }
    }

    public boolean isFilled(int row, int col) {
        if (!isValidCell(row, col)) return false;
        return statusBlock[row][col];
    }

    public int clearLines() {
        int clearedLines = 0;

        for (int row = ROWS - 1; row >= 0; row--) {
            boolean fullLine = true;
            for (int col = 0; col < COLS; col++) {
                if (!statusBlock[row][col]) {
                    fullLine = false;
                    break;
                }
            }

            if (fullLine) {
                clearedLines++;
                for (int r = row; r > 0; r--) {
                    for (int c = 0; c < COLS; c++) {
                        statusBlock[r][c] = statusBlock[r - 1][c];
                    }
                }

                for (int c = 0; c < COLS; c++) {
                    statusBlock[0][c] = false;
                }

                row++;
            }
        }
        return clearedLines;
    }
}
