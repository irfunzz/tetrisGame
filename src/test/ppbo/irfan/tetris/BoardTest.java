package ppbo.irfan.tetris;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Kelas pengujian untuk memvalidasi fungsionalitas class Board.
 * Berisi unit test untuk operasi papan permainan seperti validasi sel,
 * pengisian sel, dan penghapusan baris.
 */
public class BoardTest {

    /**
     * Menguji method isValidCell untuk memastikan validasi batas papan berfungsi dengan benar.
     * Memverifikasi bahwa sel dalam batas papan dikembalikan sebagai valid
     * dan sel di luar batas dikembalikan sebagai tidak valid.
     */
    @Test
    void testIsValidCell() {
        Board board = new Board();

        assertTrue(board.isValidCell(0, 0));
        assertTrue(board.isValidCell(19, 9));

        assertFalse(board.isValidCell(-1, 0));
        assertFalse(board.isValidCell(0, -1));
        assertFalse(board.isValidCell(20, 0));
        assertFalse(board.isValidCell(0, 10));
    }

    /**
     * Menguji method fillCell dan isFilled untuk memastikan pengisian
     * dan pemeriksaan status sel berfungsi dengan benar.
     * Memverifikasi bahwa sel yang diisi ditandai sebagai terisi
     * dan sel lain tetap kosong.
     */
    @Test
    void testFillCellAndIsFilled() {
        Board board = new Board();

        assertFalse(board.isFilled(5, 5));
        board.fillCell(5, 5);
        assertTrue(board.isFilled(5, 5));

        assertFalse(board.isFilled(5, 6));
        assertFalse(board.isFilled(4, 5));
    }

    /**
     * Menguji penghapusan satu baris penuh.
     * Memverifikasi bahwa baris yang telah penuh terdeteksi dan dihapus dengan benar,
     * serta sel-sel pada baris tersebut menjadi kosong setelah dihapus.
     */
    @Test
    void testClearSingleLine() {
        Board board = new Board();

        for (int c = 0; c < 10; c++) {
            board.fillCell(19, c);
        }

        List<Integer> cleared = board.clearLines();

        assertEquals(1, cleared.size());
        assertTrue(cleared.contains(19));

        for (int c = 0; c < 10; c++) {
            assertFalse(board.isFilled(19, c));
        }
    }

    /**
     * Menguji penghapusan beberapa baris penuh sekaligus.
     * Memverifikasi bahwa semua baris penuh terdeteksi dan dihapus dengan benar,
     * serta baris-baris di atasnya turun untuk mengisi ruang kosong.
     */
    @Test
    void testClearMultipleLines() {
        Board board = new Board();

        for (int c = 0; c < 10; c++) {
            board.fillCell(18, c);
            board.fillCell(19, c);
        }

        List<Integer> cleared = board.clearLines();

        assertEquals(2, cleared.size());
        assertTrue(cleared.contains(18));
        assertTrue(cleared.contains(19));

        for (int c = 0; c < 10; c++) {
            assertFalse(board.isFilled(18, c));
            assertFalse(board.isFilled(19, c));
        }
    }
}
