package ppbo.irfan.tetris;


public abstract class Tetromino {
    protected int row;
    protected int col;
    protected CellPosition[] offsets = new CellPosition[4];
    protected int rotationState = 0;
    public Tetromino(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
    }

    public CellPosition[] getCells() {
        CellPosition[] result = new CellPosition[4];
        for (int i = 0; i < 4; i++) {
            int globalRow = row + offsets[i].getRow();
            int globalCol = col + offsets[i].getCol();
            result[i] = new CellPosition(globalRow, globalCol);
        }
        return result;
    }

    public void moveDown() {
        row++;
    }

    public void moveLeft() {
        col--;
    }

    public void moveRight() {
        col++;
    }

    public CellPosition[] getOffsetsCopy() {
        CellPosition[] copy = new CellPosition[offsets.length];
        for (int i = 0; i < offsets.length; i++) {
            copy[i] = new CellPosition(offsets[i].getRow(), offsets[i].getCol());
        }
        return copy;
    }

    public void restoreOffsets(CellPosition[] backup) {
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = backup[i];
        }
    }

    public int getRotationState() {
        return rotationState;
    }

    public void setRotationState(int rotationState) {
        this.rotationState = rotationState;
    }

    public abstract void rotate();
}
