package ppbo.irfan.tetris;

public class OShape extends Tetromino{

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
