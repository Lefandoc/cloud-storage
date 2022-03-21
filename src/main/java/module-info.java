module cloudstorage {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires log4j;
    requires org.slf4j;
    requires io.netty.codec;
    requires io.netty.transport;

    exports ru.gb.lefandoc.cloudstorage.server.model;
    exports ru.gb.lefandoc.cloudstorage.client;
    opens ru.gb.lefandoc.cloudstorage.client to javafx.fxml;
}