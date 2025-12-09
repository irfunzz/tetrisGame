package ppbo.irfan.tetris;

public class LShape extends Tetromino {
    public LShape(int starRow, int starCol) {
        super(starRow, starCol);
        offsets[0] = new CellPosition(0, 0);
        offsets[1] = new CellPosition(1, 0);
        offsets[2] = new CellPosition(2, 0);
        offsets[3] = new CellPosition(2, 1);
        // start row -2
    }

    @Override
    public void rotate() {
        rotationState = (rotationState + 1) % 4;
        switch (rotationState) {
            case 0:
                offsets[0] = new CellPosition(0, 0);
                offsets[1] = new CellPosition(1, 0);
                offsets[2] = new CellPosition(2, 0);
                offsets[3] = new CellPosition(2, 1);
                break;
            case 1:
                offsets[0] = new CellPosition(1, 0);
                offsets[1] = new CellPosition(2, -1);
                offsets[2] = new CellPosition(2, 0);
                offsets[3] = new CellPosition(2, -2);
                break;
            case 2:
                offsets[0] = new CellPosition(2, -1);
                offsets[1] = new CellPosition(3, 0);
                offsets[2] = new CellPosition(2, 0);
                offsets[3] = new CellPosition(4, 0);
                break;
            case 3:
                offsets[0] = new CellPosition(2, 1);
                offsets[1] = new CellPosition(2, 2);
                offsets[2] = new CellPosition(2, 0);
                offsets[3] = new CellPosition(3, 0);
            break;
            default:
                break;
        }
    }
}
