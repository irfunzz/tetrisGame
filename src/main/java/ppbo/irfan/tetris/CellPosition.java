package ppbo.irfan.tetris;

public class CellPosition {
    private final int rowOffset;
    private final int colOffset;

    public CellPosition(int rowOffset, int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
    }

    public int getRow() { return rowOffset; }
    public int getCol() { return colOffset; }
//    public void setRow(int row) { this.row = row; }
//    public void setCol(int colOffset) { this.colOffset = colOffset; }
}
