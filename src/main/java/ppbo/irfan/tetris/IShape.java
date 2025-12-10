package ppbo.irfan.tetris;

/**
 * Kelas yang merepresentasikan tetromino berbentuk I (balok panjang).
 * Terdiri dari 4 sel yang tersusun dalam satu baris vertikal atau horizontal.
 */
public class IShape extends Tetromino{

    /**
     * Konstruktor untuk membuat tetromino bentuk I pada posisi awal.
     *
     * @param startRow posisi baris awal
     * @param startCol posisi kolom awal
     */
    public IShape(int startRow, int startCol) {
        super(startRow, startCol);
        offsets[0] = new CellPosition(0, 0);
        offsets[1] = new CellPosition(1, 0);
        offsets[2] = new CellPosition(2, 0);
        offsets[3] = new CellPosition(3, 0);
        // start row -3
    }

    @Override
    public void rotate() {
        rotationState = (rotationState + 1) % 2;
        if (rotationState == 0) {
            offsets[0] = new CellPosition(0, 0);
            offsets[1] = new CellPosition(1, 0);
            offsets[2] = new CellPosition(2, 0);
            offsets[3] = new CellPosition(3, 0);
        } else {
            offsets[0] = new CellPosition(1, -1);
            offsets[1] = new CellPosition(1, 0);
            offsets[2] = new CellPosition(1, 1);
            offsets[3] = new CellPosition(1, 2);
        }
    }
}