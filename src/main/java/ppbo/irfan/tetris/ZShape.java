package ppbo.irfan.tetris;

/**
 * Kelas yang merepresentasikan tetromino berbentuk Z.
 * Terdiri dari 4 sel dengan bentuk seperti huruf Z.
 */
public class ZShape extends Tetromino{
    /**
     * Konstruktor untuk membuat tetromino bentuk Z pada posisi awal.
     *
     * @param startRow posisi baris awal
     * @param startCol posisi kolom awal
     */
    public ZShape(int startRow, int startCol) {
        super(startRow, startCol);
        offsets[0] = new CellPosition(0, 0);
        offsets[1] = new CellPosition(0, -1);
        offsets[2] = new CellPosition(1, 0);
        offsets[3] = new CellPosition(1, 1);
        // start row -3
    }

    @Override
    public void rotate() {
        rotationState = (rotationState + 1) % 2;
        if (rotationState == 0) {
            offsets[0] = new CellPosition(0, 0);
            offsets[1] = new CellPosition(0, -1);
            offsets[2] = new CellPosition(1, 0);
            offsets[3] = new CellPosition(1, 1);
        } else {
            offsets[0] = new CellPosition(0, 1);
            offsets[1] = new CellPosition(1, 1);
            offsets[2] = new CellPosition(1, 0);
            offsets[3] = new CellPosition(2, 0);
        }
    }
}
