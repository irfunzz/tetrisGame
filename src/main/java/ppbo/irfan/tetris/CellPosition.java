package ppbo.irfan.tetris;

/**
 * Kelas yang merepresentasikan posisi relatif suatu sel terhadap titik pusat tetromino.
 * Digunakan untuk menyimpan offset baris dan kolom dari setiap sel dalam tetromino.
 */
public class CellPosition {
    /**
     * Offset baris relatif terhadap posisi pusat tetromino.
     */
    private final int rowOffset;

    /**
     * Offset kolom relatif terhadap posisi pusat tetromino.
     */
    private final int colOffset;

    /**
     * Konstruktor untuk membuat objek posisi sel dengan offset tertentu.
     *
     * @param rowOffset offset baris relatif terhadap posisi pusat tetromino
     * @param colOffset offset kolom relatif terhadap posisi pusat tetromino
     */
    public CellPosition(int rowOffset, int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
    }

    /**
     * Mendapatkan offset baris dari posisi sel.
     *
     * @return offset baris
     */
    public int getRow() { return rowOffset; }

    /**
     * Mendapatkan offset kolom dari posisi sel.
     *
     * @return offset kolom
     */
    public int getCol() { return colOffset; }
//    public void setRow(int row) { this.row = row; }
//    public void setCol(int colOffset) { this.colOffset = colOffset; }
}
