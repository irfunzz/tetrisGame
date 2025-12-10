package ppbo.irfan.tetris;

/**
 * Kelas yang merepresentasikan tetromino berbentuk O (kotak).
 * Terdiri dari 4 sel yang membentuk kotak 2x2.
 * Bentuk ini tidak mengalami perubahan saat dirotasi.
 */
public class OShape extends Tetromino{

    /**
     * Konstruktor untuk membuat tetromino bentuk O pada posisi awal.
     *
     * @param startRow posisi baris awal
     * @param startCol posisi kolom awal
     */
    public OShape(int startRow, int startCol) {
        super(startRow, startCol);
        offsets[0] = new CellPosition(0, 0);
        offsets[1] = new CellPosition(0, 1);
        offsets[2] = new CellPosition(1, 0);
        offsets[3] = new CellPosition(1, 1);
        // start row -1
    }

    @Override
    public void rotate() {}
}
