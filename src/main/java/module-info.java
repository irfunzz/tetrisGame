/**
 * Modul aplikasi permainan Tetris berbasis JavaFX.
 * Modul ini berisi implementasi lengkap permainan Tetris termasuk
 * logika permainan, antarmuka pengguna, dan pengelolaan media.
 */
module ppbo.irfan.tetris {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires javafx.media;

    opens ppbo.irfan.tetris to javafx.fxml;
    exports ppbo.irfan.tetris;
}