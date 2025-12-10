package ppbo.irfan.tetris;

import java.util.ArrayList;
import java.util.List;

/**
 * Kelas yang merepresentasikan papan permainan Tetris.
 * Mengelola status sel-sel pada papan, validasi posisi,
 * dan penghapusan baris yang telah penuh.
 */
public class Board {

    /**
     * Jumlah kolom pada papan permainan.
     */
    private static final int COLS = 10;

    /**
     * Jumlah baris pada papan permainan.
     */
    private static final int ROWS = 20;

    /**
     * Array 2 dimensi yang menyimpan status terisi/kosong untuk setiap sel pada papan.
     */
    private boolean[][] statusBlock;

    /**
     * Konstruktor untuk membuat papan permainan baru.
     * Menginisialisasi array 2 dimensi untuk menyimpan status setiap sel.
     */
    public Board() {
        statusBlock = new boolean[ROWS][COLS];

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                statusBlock[row][col] = false;
            }
        }
    }

    /**
     * Memeriksa apakah posisi sel berada dalam batas papan permainan.
     *
     * @param row indeks baris yang akan diperiksa
     * @param col indeks kolom yang akan diperiksa
     * @return true jika sel berada dalam batas papan, false jika tidak
     */
    public boolean isValidCell(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    /**
     * Memeriksa apakah sel tersedia untuk ditempati.
     * Sel tersedia jika berada dalam batas papan dan belum terisi.
     *
     * @param row indeks baris yang akan diperiksa
     * @param col indeks kolom yang akan diperiksa
     * @return true jika sel tersedia, false jika tidak
     */
    public boolean isAvailable(int row, int col) {
        if (isValidCell(row, col) && !statusBlock[row][col]) {
            return true;
        }
        return false;
    }

    /**
     * Menandai sel sebagai terisi jika sel tersebut tersedia.
     *
     * @param row indeks baris yang akan diisi
     * @param col indeks kolom yang akan diisi
     */
    public void fillCell(int row, int col) {
        if (isAvailable(row, col)) {
            statusBlock[row][col] = true;
        }
    }

    /**
     * Memeriksa apakah sel telah terisi.
     *
     * @param row indeks baris yang akan diperiksa
     * @param col indeks kolom yang akan diperiksa
     * @return true jika sel terisi, false jika tidak atau sel tidak valid
     */
    public boolean isFilled(int row, int col) {
        if (!isValidCell(row, col)) return false;
        return statusBlock[row][col];
    }

    /**
     * Menghapus baris-baris yang telah penuh dan menurunkan baris di atasnya.
     * Baris penuh adalah baris yang semua selnya telah terisi.
     *
     * @return daftar indeks baris yang telah dihapus
     */
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

    /**
     * Menghitung jumlah baris yang dapat dihapus.
     *
     * @return jumlah baris yang telah penuh
     */
    public int clearLinesCount() {
        return clearLines().size();
    }
}
