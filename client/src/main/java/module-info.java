module ru.gb.lefandoc.cloudstorage.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires io.netty.codec;
    requires org.slf4j;
    requires lombok;
    requires commons;

    opens ru.gb.lefandoc.cloudstorage.client to javafx.fxml;
    exports ru.gb.lefandoc.cloudstorage.client;
}