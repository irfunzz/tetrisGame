package ppbo.irfan.tetris;

/**
 * Kelas abstrak yang merepresentasikan tetromino (blok jatuh) dalam permainan Tetris.
 * Setiap tetromino memiliki posisi, offset sel, dan kemampuan untuk bergerak serta berputar.
 */
public abstract class Tetromino {
    /**
     * Posisi baris saat ini dari tetromino pada papan permainan.
     */
    protected int row;

    /**
     * Posisi kolom saat ini dari tetromino pada papan permainan.
     */
    protected int col;

    /**
     * Array yang menyimpan offset posisi relatif dari keempat sel yang membentuk tetromino.
     */
    protected CellPosition[] offsets = new CellPosition[4];

    /**
     * State rotasi saat ini dari tetromino (0-3 untuk sebagian besar bentuk).
     */
    protected int rotationState = 0;

    /**
     * Konstruktor untuk membuat tetromino baru pada posisi awal tertentu.
     *
     * @param startRow posisi baris awal tetromino
     * @param startCol posisi kolom awal tetromino
     */
    public Tetromino(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
    }

    /**
     * Mendapatkan posisi absolut dari semua sel yang membentuk tetromino.
     *
     * @return array berisi posisi setiap sel dalam koordinat global
     */
    public CellPosition[] getCells() {
        CellPosition[] result = new CellPosition[4];
        for (int i = 0; i < 4; i++) {
            int globalRow = row + offsets[i].getRow();
            int globalCol = col + offsets[i].getCol();
            result[i] = new CellPosition(globalRow, globalCol);
        }
        return result;
    }

    /**
     * Menggerakkan tetromino satu langkah ke bawah.
     */
    public void moveDown() {
        row++;
    }

    /**
     * Menggerakkan tetromino satu langkah ke kiri.
     */
    public void moveLeft() {
        col--;
    }

    /**
     * Menggerakkan tetromino satu langkah ke kanan.
     */
    public void moveRight() {
        col++;
    }

    /**
     * Membuat salinan dari array offset sel tetromino.
     * Digunakan untuk menyimpan state sebelum rotasi.
     *
     * @return salinan array offset sel
     */
    public CellPosition[] getOffsetsCopy() {
        CellPosition[] copy = new CellPosition[offsets.length];
        for (int i = 0; i < offsets.length; i++) {
            copy[i] = new CellPosition(offsets[i].getRow(), offsets[i].getCol());
        }
        return copy;
    }

    /**
     * Mengembalikan offset sel ke state sebelumnya.
     * Digunakan untuk membatalkan rotasi yang tidak valid.
     *
     * @param backup array offset yang akan dikembalikan
     */
    public void restoreOffsets(CellPosition[] backup) {
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = backup[i];
        }
    }

    /**
     * Mendapatkan state rotasi saat ini dari tetromino.
     *
     * @return indeks state rotasi
     */
    public int getRotationState() {
        return rotationState;
    }

    /**
     * Mengatur state rotasi tetromino.
     *
     * @param rotationState indeks state rotasi yang akan diatur
     */
    public void setRotationState(int rotationState) {
        this.rotationState = rotationState;
    }

    /**
     * Merotasi tetromino searah jarum jam.
     * Implementasi rotasi berbeda untuk setiap bentuk tetromino.
     */
    public abstract void rotate();
}
