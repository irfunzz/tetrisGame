package ppbo.irfan.tetris;

import java.util.ArrayList;
import java.util.List;

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

    public List<Integer> clearLines() {
        List<Integer> fullRows = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            boolean fullLine = true;
            for (int col = 0; col < COLS; col++) {
                if (!statusBlock[row][col]) {
                    fullLine = false;
                    break;
                }
            }
            if (fullLine) {
                fullRows.add(row);
            }
        }

        if (fullRows.isEmpty()) {
            return fullRows;
        }

        boolean[][] newStatus = new boolean[ROWS][COLS];
        int dstRow = ROWS - 1;

        for (int srcRow = ROWS - 1; srcRow >= 0; srcRow--) {
            if (fullRows.contains(srcRow)) {
                continue;
            }

            for (int col = 0; col < COLS; col++) {
                newStatus[dstRow][col] = statusBlock[srcRow][col];
            }
            dstRow--;
        }

        statusBlock = newStatus;
        return fullRows;
    }

    public int clearLinesCount() {
        return clearLines().size();
    }
}
