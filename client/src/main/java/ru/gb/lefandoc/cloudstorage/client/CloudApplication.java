package ru.gb.lefandoc.cloudstorage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CloudApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CloudApplication.class.getResource("cloud-storage.fxml"));
        stage.setTitle("Cloud Storage client");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}