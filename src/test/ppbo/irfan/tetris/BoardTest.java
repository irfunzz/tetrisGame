package ppbo.irfan.tetris;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

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

    @Test
    void testFillCellAndIsFilled() {
        Board board = new Board();

        assertFalse(board.isFilled(5, 5));
        board.fillCell(5, 5);
        assertTrue(board.isFilled(5, 5));

        assertFalse(board.isFilled(5, 6));
        assertFalse(board.isFilled(4, 5));
    }

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
