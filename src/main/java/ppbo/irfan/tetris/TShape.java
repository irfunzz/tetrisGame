package ppbo.irfan.tetris;

/**
 * Kelas yang merepresentasikan tetromino berbentuk T.
 * Terdiri dari 4 sel dengan bentuk seperti huruf T.
 */
public class TShape extends Tetromino {
    /**
     * Konstruktor untuk membuat tetromino bentuk T pada posisi awal.
     *
     * @param startRow posisi baris awal
     * @param startCol posisi kolom awal
     */
    public TShape(int startRow, int startCol) {
        super(startRow, startCol);
        offsets[0] = new CellPosition(1, 1);
        offsets[1] = new CellPosition(1, -1);
        offsets[2] = new CellPosition(1, 0);
        offsets[3] = new CellPosition(2, 0);
        // start row -1
    }

    @Override
    public void rotate() {
        rotationState = (rotationState + 1) % 4;
        switch (rotationState) {
            case 0:
                offsets[0] = new CellPosition(1, 1);
                offsets[1] = new CellPosition(1, -1);
                offsets[2] = new CellPosition(1, 0);
                offsets[3] = new CellPosition(2, 0);
                break;
            case 1:
                offsets[0] = new CellPosition(0, 0);
                offsets[1] = new CellPosition(1, 1);
                offsets[2] = new CellPosition(1, 0);
                offsets[3] = new CellPosition(2, 0);
                break;
            case 2:
                offsets[0] = new CellPosition(0, 0);
                offsets[1] = new CellPosition(1, -1);
                offsets[2] = new CellPosition(1, 0);
                offsets[3] = new CellPosition(1, 1);
                break;
            case 3:
                offsets[0] = new CellPosition(0, 0);
                offsets[1] = new CellPosition(1, -1);
                offsets[2] = new CellPosition(1, 0);
                offsets[3] = new CellPosition(2, 0);
                break;
            default:
                break;
        }
    }
}
