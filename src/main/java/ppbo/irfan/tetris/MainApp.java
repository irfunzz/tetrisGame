package ppbo.irfan.tetris;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Kelas utama aplikasi permainan Tetris berbasis JavaFX.
 * Mengelola tampilan antarmuka pengguna, logika permainan, skor, level,
 * animasi, kontrol input, dan media audio.
 */
public class MainApp extends Application {

    /**
     * Jumlah kolom pada papan permainan.
     */
    private static final int COLS = 10;

    /**
     * Jumlah baris pada papan permainan.
     */
    private static final int ROWS = 20;

    /**
     * Ukuran setiap sel dalam pixel.
     */
    private static final int CELL_SIZE = 40;

    /**
     * Tetromino yang sedang aktif dan dapat dikendalikan pemain.
     */
    private Tetromino currentTetromino;

    /**
     * Tetromino berikutnya yang akan muncul setelah tetromino saat ini terkunci.
     */
    private Tetromino nextTetromino;

    /**
     * Objek papan permainan yang menyimpan status sel-sel.
     */
    private Board board;

    /**
     * Timeline untuk mengatur kecepatan jatuh tetromino secara otomatis.
     */
    private Timeline timeline;

    /**
     * Skor pemain saat ini.
     */
    private int score = 0;

    /**
     * Level permainan saat ini yang mempengaruhi kecepatan jatuh.
     */
    private int level = 1;

    /**
     * Total jumlah baris yang telah berhasil dihapus.
     */
    private int totalClearedLines = 0;

    /**
     * Panel utama tempat menggambar papan permainan dan tetromino.
     */
    private Pane gamePane;

    /**
     * Panel samping yang berisi informasi skor, level, waktu, dan preview tetromino.
     */
    private VBox sideBar;

    /**
     * Label untuk menampilkan skor pemain.
     */
    private Label scoreLabel;

    /**
     * Label untuk menampilkan level permainan.
     */
    private Label levelLabel;

    /**
     * Panel untuk menampilkan preview tetromino berikutnya.
     */
    private Pane nextPane;

    /**
     * MediaPlayer untuk memutar musik latar belakang.
     */
    private MediaPlayer bgmPlayer;

    /**
     * MediaPlayer untuk memutar efek suara penghapusan baris.
     */
    private MediaPlayer efcClear;

    /**
     * Label untuk menampilkan waktu bermain.
     */
    private Label timeLabel;

    /**
     * Flag untuk mengontrol status thread timer.
     */
    private volatile boolean timerRunning = false;

    /**
     * Thread untuk menghitung waktu bermain secara terpisah.
     */
    private Thread timerThread;

    /**
     * Waktu bermain yang telah berlalu dalam detik.
     */
    private int elapsedSeconds = 0;

    /**
     * Membuat panel permainan dengan grid kotak-kotak untuk papan Tetris.
     *
     * @return objek Pane yang berisi grid permainan
     */
    private Pane createPane() {
        Pane root = new Pane();
        root.setPrefSize(COLS * CELL_SIZE, ROWS * CELL_SIZE);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle cell = new Rectangle(
                        col * CELL_SIZE,
                        row * CELL_SIZE,
                        CELL_SIZE,
                        CELL_SIZE
                );
                cell.setFill(Color.BLACK);
                cell.setStroke(Color.DARKGRAY);

                root.getChildren().add(cell);
            }
        }
        return root;
    }

    /**
     * Metode inisialisasi dan peluncuran aplikasi JavaFX.
     * Menginisialisasi papan permainan, komponen UI, media audio,
     * event handler untuk input keyboard, dan timeline untuk game loop.
     *
     * @param stage stage utama aplikasi JavaFX
     * @throws IOException jika terjadi kesalahan dalam memuat resource
     */
    @Override
    public void start(Stage stage) throws IOException {
        gamePane = createPane();

        scoreLabel = new Label("Score: " + score);
        levelLabel = new Label("Level: " + level);
        timeLabel = new Label("Time: 0s");
        timeLabel.setTextFill(Color.WHITE);

        Media media = new Media(
                getClass().getResource("/ppbo/irfan/tetris/audio/theme.mp3").toExternalForm()
        );
        Media medias = new Media(
                getClass().getResource("/ppbo/irfan/tetris/audio/clearEffect.mp3").toExternalForm()
        );

        bgmPlayer = new MediaPlayer(media);
        bgmPlayer.setCycleCount(Timeline.INDEFINITE);
        bgmPlayer.setVolume(0.15);
        bgmPlayer.play();

        efcClear = new MediaPlayer(medias);
        efcClear.setVolume(0.6);

        nextPane = new Pane();
        nextPane.setPrefSize(4 * CELL_SIZE, 4 * CELL_SIZE);
        nextPane.setStyle("-fx-background-color: #333");

        sideBar = new VBox(20, scoreLabel, levelLabel, timeLabel, nextPane);
        sideBar.setStyle("-fx-padding: 10; -fx-background-color: #222; -fx-text-fill: white;");
        scoreLabel.setTextFill(Color.WHITE);
        levelLabel.setTextFill(Color.WHITE);

        HBox root = new HBox(20, gamePane, sideBar);
        Scene scene = new Scene(root, COLS * CELL_SIZE + 200, ROWS * CELL_SIZE);
        board = new Board();

        scene.setOnKeyPressed(event -> {
            int dRow = 0;
            int dCol = 0;

            if (event.getCode() == KeyCode.LEFT) {
                dCol = -1;
            } else if (event.getCode() == KeyCode.RIGHT) {
                dCol = 1;
            } else if (event.getCode() == KeyCode.DOWN) {
                dRow = 1;
            } else if (event.getCode() == KeyCode.UP) {
                tryRotateTetromino();
                return;
            }

            if (dRow != 0 || dCol != 0) {
                if (canMove(dRow, dCol)) {
                    if (dCol == -1) {
                        currentTetromino.moveLeft();
                    } else if (dCol == 1) {
                        currentTetromino.moveRight();
                    } else if (dRow == 1) {
                        currentTetromino.moveDown();
                    }
                }
            }
            drawTetromino(gamePane);
        });

        currentTetromino = createRandomTetromino();
        nextTetromino = createRandomTetromino();
        drawTetromino(gamePane);
        drawNextTetomino();
        createTimeline(500);
        startTimerThread();;

        stage.setTitle("Tetris!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Menggambar tetromino yang sedang aktif pada panel permainan.
     * Menghapus gambar tetromino sebelumnya dan menggambar ulang
     * pada posisi terbaru.
     *
     * @param root panel tempat tetromino akan digambar
     */
    public void drawTetromino(Pane root) {
        clearTetrominoFromPane();
        CellPosition[] cells = currentTetromino.getCells();
        for (int i = 0; i < cells.length; i++) {
            int row = cells[i].getRow();
            int col = cells[i].getCol();

            double x = col * CELL_SIZE;
            double y = row * CELL_SIZE;

            Rectangle cell = new Rectangle(x, y, CELL_SIZE, CELL_SIZE);
            cell.setFill(Color.GREEN);
            cell.setStroke(Color.DARKGREEN);
            root.getChildren().add(cell);
        }
    }

    /**
     * Menggambar preview tetromino berikutnya pada panel sidebar.
     * Menampilkan bentuk tetromino yang akan muncul setelah
     * tetromino saat ini terkunci.
     */
    private void drawNextTetomino() {
        nextPane.getChildren().clear();

        CellPosition[] cells = nextTetromino.getCells();

        int minRow = Integer.MAX_VALUE;
        int minCol = Integer.MAX_VALUE;

        for (CellPosition cell : cells) {
            int r = cell.getRow();
            int c = cell.getCol();

            if (r < minRow) minRow = r;
            if (c < minCol) minCol = c;
        }

        int previewSize = CELL_SIZE / 2;
        int offsetX = (int) 3f;
        int offsetY = (int) 3.5f;
        for (CellPosition cell : cells) {
            int normalizedRow = cell.getRow() - minRow;
            int normalizedCol = cell.getCol() - minCol;

            double x = (normalizedCol + offsetX) * previewSize;
            double y = (normalizedRow + offsetY) * previewSize;

            Rectangle rect = new Rectangle(x, y, previewSize, previewSize);
            rect.setFill(Color.LIGHTGREEN);
            rect.setStroke(Color.DARKGREEN);

            nextPane.getChildren().add(rect);
        }
    }

    /**
     * Menghapus gambar tetromino yang sedang aktif dari panel permainan.
     * Menghapus semua Rectangle yang memiliki warna hijau (tetromino aktif).
     */
    private void clearTetrominoFromPane() {
        for (int i = gamePane.getChildren().size() - 1; i >= 0 ; i--) {
            if (gamePane.getChildren().get(i) instanceof Rectangle) {
                Rectangle r = (Rectangle) gamePane.getChildren().get(i);
                if (r.getFill() == Color.GREEN && r.getStroke() == Color.DARKGREEN) {
                    gamePane.getChildren().remove(i);
                }
            }
        }
    }

    /**
     * Memeriksa apakah tetromino dapat bergerak ke posisi baru.
     * Validasi dilakukan terhadap batas papan dan sel yang sudah terisi.
     *
     * @param dRow perubahan posisi baris (delta row)
     * @param dCol perubahan posisi kolom (delta column)
     * @return true jika tetromino dapat bergerak, false jika tidak
     */
    private boolean canMove(int dRow, int dCol) {
        CellPosition[] cells = currentTetromino.getCells();
        for (int i = 0; i < cells.length; i++) {
            int newRow = cells[i].getRow() + dRow ;
            int newCol = cells[i].getCol() + dCol ;

            if (newRow < 0) continue;

            if (!board.isValidCell(newRow, newCol)) return false;

            if (!board.isAvailable(newRow, newCol)) return false;
        }
        return true;
    }

    /**
     * Mengunci tetromino saat ini pada papan permainan.
     * Menandai sel-sel yang ditempati tetromino sebagai terisi,
     * menggambar ulang blok yang terkunci, dan memeriksa baris
     * yang dapat dihapus.
     */
    private void lockCurrentTetromino() {

        CellPosition[] cells = currentTetromino.getCells();
        for (CellPosition cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();

            if (row < 0) continue;

            board.fillCell(row, col);
        }
        redrawLockedBlocks();
        List<Integer> clearedRows = board.clearLines();
        int cleared = clearedRows.size();
        updateScoreAndLevel(cleared);
        if (cleared > 0) {
            animateLineClear(clearedRows);
        }
    }

    /**
     * Memperbarui skor dan level berdasarkan jumlah baris yang dihapus.
     * Skor dihitung berdasarkan jumlah baris yang dihapus sekaligus dan level saat ini.
     * Level meningkat setiap 2 baris yang berhasil dihapus.
     *
     * @param clearedLines jumlah baris yang berhasil dihapus
     */
    private void updateScoreAndLevel(int clearedLines) {
        if (clearedLines <= 0) return;

        switch (clearedLines) {
            case 1:
                score += 100 * level;
                break;
            case 2:
                score += 300 * level;
                break;
            case 3:
                score += 500 * level;
                break;
            case 4:
                score += 800 * level;
                break;
            default:
                score += 100 * clearedLines * level;
        }

        totalClearedLines += clearedLines;
        int newLevel = 1 + totalClearedLines / 2;
        if (newLevel != level) {
            level = newLevel;
            updateSpeed();
            updateAudioSpeed();
        }

        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level: " + level);
    }

    /**
     * Membuat timeline untuk game loop yang mengatur kecepatan jatuh tetromino.
     *
     * @param millis interval waktu dalam milidetik untuk setiap tick permainan
     */
    private void createTimeline(double millis) {
        timeline = new Timeline(
                new KeyFrame(Duration.millis(millis), e -> gameTick())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Memperbarui kecepatan jatuh tetromino berdasarkan level saat ini.
     * Semakin tinggi level, semakin cepat tetromino jatuh.
     */
    private void updateSpeed() {
        double baseMillis = 500;
        double newMillis = baseMillis - (level - 1) * 50;
        if (newMillis < 100) {
            newMillis = 100;
        }

        timeline.stop();
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(newMillis), e -> gameTick())
        );
        timeline.play();
    }

    /**
     * Memperbarui kecepatan pemutaran audio background dan efek suara
     * sesuai dengan level permainan saat ini.
     */
    private void updateAudioSpeed() {
        double newRate = 1.0 + (level - 1) * 0.05;
        if (newRate > 2.0) {
            newRate = 2.0;
        }

        bgmPlayer.setRate(newRate);
        efcClear.setRate(newRate);
    }

    /**
     * Melakukan satu iterasi game loop.
     * Menggerakkan tetromino ke bawah, atau mengunci tetromino
     * dan membuat yang baru jika tidak dapat bergerak.
     * Memeriksa kondisi game over jika tetromino baru tidak dapat ditempatkan.
     */
    private void gameTick() {
        if (canMove(1, 0)) {
            currentTetromino.moveDown();
            drawTetromino(gamePane);
        } else {
            lockCurrentTetromino();

            currentTetromino = nextTetromino;
            nextTetromino = createRandomTetromino();

            if (!canMove(0, 0)) {
                showGameOverWindow();
                return;
            }

            drawTetromino(gamePane);
            drawNextTetomino();
        }
    }

    /**
     * Membuat tetromino secara acak dari tujuh bentuk yang tersedia.
     * Posisi awal tetromino disesuaikan dengan bentuknya.
     *
     * @return objek Tetromino dengan bentuk acak
     */
    private Tetromino createRandomTetromino() {
        Random r = new Random();
        int random = r.nextInt(7) + 1;
        switch (random) {
            case 1:
                return new OShape(-1, COLS / 2);
            case 2:
                return new IShape(-3, COLS / 2);
            case 3:
                return new JShape(-2, COLS / 2);
            case 4:
                return new LShape(-2, COLS / 2);
            case 5:
                return new SShape(-2, COLS / 2);
            case 6:
                return new ZShape(-2, COLS / 2);
            case 7:
                return new TShape(-2, COLS / 2);
            default:
                return new OShape(-1, COLS / 2);
        }
    }

    /**
     * Mencoba merotasi tetromino saat ini.
     * Jika rotasi menyebabkan tabrakan, rotasi dibatalkan
     * dan tetromino dikembalikan ke posisi semula.
     */
    private void tryRotateTetromino() {
        CellPosition[] backupOffsets = currentTetromino.getOffsetsCopy();
        int bakcupState = currentTetromino.getRotationState();

        currentTetromino.rotate();

        if(!canMove(0, 0)) {
            currentTetromino.restoreOffsets(backupOffsets);
            currentTetromino.setRotationState(bakcupState);
            return;
        }

        drawTetromino(gamePane);
    }

    /**
     * Menghapus semua blok yang terkunci dari panel permainan.
     * Menghapus Rectangle berwarna biru yang merepresentasikan blok terkunci.
     */
    private void clearLockedBlocksFromPane() {
        for (int i = gamePane.getChildren().size() - 1; i >= 0 ; i--) {
            if (gamePane.getChildren().get(i) instanceof Rectangle) {
                Rectangle r = (Rectangle) gamePane.getChildren().get(i);
                if (r.getFill() == Color.BLUE && r.getStroke() == Color.DARKBLUE) {
                    gamePane.getChildren().remove(i);
                }
            }
        }
    }

    /**
     * Menampilkan animasi penghapusan baris yang telah penuh.
     * Blok pada baris yang dihapus diubah menjadi putih sebelum dihapus,
     * disertai dengan efek suara.
     *
     * @param clearedRows daftar indeks baris yang akan dihapus
     */
    private void animateLineClear(List<Integer> clearedRows) {
        List<Rectangle> toFlash = new ArrayList<>();

        for (int i = 0; i < gamePane.getChildren().size(); i++) {
            if (gamePane.getChildren().get(i) instanceof Rectangle) {
                Rectangle r = (Rectangle) gamePane.getChildren().get(i);

                if (r.getFill() == Color.BLUE && r.getStroke() == Color.DARKBLUE) {
                    int row = (int) (r.getY() / CELL_SIZE);
                    if (clearedRows.contains(row)) {
                        r.setFill(Color.WHITE);
                        r.setStroke(Color.DARKGRAY);
                        toFlash.add(r);
                    }
                }
            }
        }

        PauseTransition pause = new PauseTransition(Duration.millis(250));
        pause.setOnFinished(e -> {
            efcClear.stop();
            efcClear.seek(Duration.ZERO);
            efcClear.setRate(bgmPlayer.getRate());
            efcClear.play();
            gamePane.getChildren().removeAll(toFlash);
            redrawLockedBlocks();
        });
        pause.play();
    }

    /**
     * Menggambar ulang semua blok yang telah terkunci pada papan permainan.
     * Membaca status papan dan menggambar Rectangle biru untuk setiap sel yang terisi.
     */
    private void redrawLockedBlocks() {
        clearLockedBlocksFromPane();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board.isFilled(row, col)) {
                    double x = col * CELL_SIZE;
                    double y = row * CELL_SIZE;

                    Rectangle block = new Rectangle(x, y, CELL_SIZE, CELL_SIZE);
                    block.setFill(Color.BLUE);
                    block.setStroke(Color.DARKBLUE);

                    gamePane.getChildren().add(block);
                }
            }
        }
    }

    /**
     * Menampilkan dialog game over dengan informasi skor, level, dan waktu bermain.
     * Memberikan opsi untuk bermain lagi atau keluar dari aplikasi.
     */
    private void showGameOverWindow() {
        if (timeline != null) {
            timeline.stop();
        }
        if (bgmPlayer != null) {
            bgmPlayer.pause();
        }
        timerRunning = false;

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over");
            alert.setContentText("Score: " + score + "\nLevel: " + level + "\nTime: " + elapsedSeconds + "s");

            ButtonType playAgain = new ButtonType("Play Again");
            ButtonType exit = new ButtonType("Exit");

            alert.getButtonTypes().setAll(playAgain, exit);

            alert.showAndWait().ifPresent(result -> {
                if (result == playAgain) {
                    resetGame();
                } else {
                    Platform.exit();
                }
            });
        });
    }

    /**
     * Mereset permainan ke kondisi awal.
     * Menginisialisasi ulang papan, skor, level, timer, dan tetromino.
     */
    private void resetGame() {
        board = new Board();
        score = 0;
        level = 1;
        elapsedSeconds = 0;
        totalClearedLines = 0;
        clearLockedBlocksFromPane();
        clearTetrominoFromPane();
        currentTetromino = createRandomTetromino();
        nextTetromino = createRandomTetromino();
        drawTetromino(gamePane);
        drawNextTetomino();
        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level: " + level);
        if (timeline != null) {
            timeline.stop();
        }
        createTimeline(500);
        if (bgmPlayer != null) {
            bgmPlayer.play();
        }
        startTimerThread();
    }

    /**
     * Memulai thread untuk menghitung waktu bermain.
     * Timer berjalan di thread terpisah dan memperbarui label waktu setiap detik.
     */
    private void startTimerThread() {
        timerRunning = true;
        elapsedSeconds = 0;
        if (timeLabel != null) {
            timeLabel.setText("Time: 0 s");
        }
        timerThread = new Thread(() -> {
            while (timerRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                if (!timerRunning) break;
                elapsedSeconds++;
                int display = elapsedSeconds;

                Platform.runLater(() -> {
                    timeLabel.setText("Time: " + display + " s");
                });
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    /**
     * Metode utama untuk menjalankan aplikasi.
     *
     * @param args argumen command line
     */
    public static void main(String[] args) {
        launch();
    }


}