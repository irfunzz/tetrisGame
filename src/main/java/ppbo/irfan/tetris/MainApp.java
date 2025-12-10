package ppbo.irfan.tetris;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainApp extends Application {

    private static final int COLS = 10;
    private static final int ROWS = 20;
    private static final int CELL_SIZE = 40;

    private Tetromino currentTetromino;
    private Tetromino nextTetromino;
    private Board board;
    private Timeline timeline;

    private int score = 0;
    private int level = 1;
    private int totalClearedLines = 0;

    private Pane gamePane;
    private VBox sideBar;
    private Label scoreLabel;
    private Label levelLabel;
    private Pane nextPane;
    private MediaPlayer bgmPlayer;
    private MediaPlayer efcClear;


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

    @Override
    public void start(Stage stage) throws IOException {
        gamePane = createPane();

        scoreLabel = new Label("Score: " + score);
        levelLabel = new Label("Level: " + level);
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

        sideBar = new VBox(20, scoreLabel, levelLabel, nextPane);
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

        stage.setTitle("Tetris!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }

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

    private void createTimeline(double millis) {
        timeline = new Timeline(
                new KeyFrame(Duration.millis(millis), e -> gameTick())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

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

    private void updateAudioSpeed() {
        double newRate = 1.0 + (level - 1) * 0.05;
        if (newRate > 2.0) {
            newRate = 2.0;
        }

        bgmPlayer.setRate(newRate);
        efcClear.setRate(newRate);
    }

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

    private void showGameOverWindow() {
        if (timeline != null) {
            timeline.stop();
        }
        if (bgmPlayer != null) {
            bgmPlayer.pause();
        }

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over");
            alert.setContentText("Score: " + score + "\nLevel: " + level);

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

    private void resetGame() {
        board = new Board();
        score = 0;
        level = 1;
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
    }

    public static void main(String[] args) {
        launch();
    }


}