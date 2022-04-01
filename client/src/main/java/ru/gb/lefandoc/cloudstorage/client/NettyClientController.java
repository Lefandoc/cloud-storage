package ru.gb.lefandoc.cloudstorage.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ru.gb.lefandoc.cloudstorage.commons.model.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

@Slf4j
public class NettyClientController implements Initializable {

    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientPath;
    public TextField serverPath;
    public CheckBox checkDelete;
    public Button btnLogin;

    private Path clientDir;

    private ObjectEncoderOutputStream oos;
    private ObjectDecoderInputStream ois;

    private String login;
    private String password;

    public void download(ActionEvent actionEvent) throws IOException {
        oos.writeObject(new FileRequest(serverView.getSelectionModel().getSelectedItem()));
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        oos.writeObject(new FileMessage(clientDir.resolve(clientView.getSelectionModel().getSelectedItem())));
        if (checkDelete.isSelected()) {
            Files.delete(clientDir.resolve(clientView.getSelectionModel().getSelectedItem()));
            updateClientView();
        }
    }

    public void chooseDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(clientDir.toFile());
        clientDir = directoryChooser.showDialog(new Stage()).toPath();
        updateClientView();
    }

    public void login(ActionEvent actionEvent) throws IOException {
        AuthDialog dialog = new AuthDialog();
        dialog.DialogForm();
        dialog.showAndWait().ifPresent(str -> {
            String[] s = str.split(" ");
            login = s[0];
            password = s[1];
        });
        oos.writeObject(new AuthMessage(login, password));
        System.out.println(login + password);
    }

    @FXML
    private void updateClientView() {
        Platform.runLater(() -> {
            clientPath.setText(clientDir.toFile().getName());
            clientView.getItems().clear();
            clientView.getItems().add("...");
            clientView.getItems()
                    .addAll(clientDir.toFile().list());
        });
        log.debug("Client view updated");
    }

    private void updateServerView(ListMessage lm) {
        Platform.runLater(() -> {
            serverPath.setText("SERVER DIRECTORY");
            serverView.getItems().clear();
            serverView.getItems().addAll(lm.getFiles());
        });
        log.debug("Server view updated");
    }

    private void read() {
        try {
            while (true) {
                CloudMessage msg = (CloudMessage) ois.readObject();
                switch (msg.getMessageType()) {
                    case FILE:
                        FileMessage fm = (FileMessage) msg;
                        Files.write(clientDir.resolve(fm.getName()), fm.getBytes());
                        updateClientView();
                        break;
                    case LIST:
                        ListMessage lm = (ListMessage) msg;
                        updateServerView(lm);
                        btnLogin.setVisible(false);
                        break;
                }
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            oos = new ObjectEncoderOutputStream(socket.getOutputStream());
            ois = new ObjectDecoderInputStream(socket.getInputStream());
            Files.createDirectories(Paths.get("clientDir"));
            clientDir = Paths.get("clientDir");
            updateClientView();
            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();

            clientView.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    String item = clientView.getSelectionModel().getSelectedItem();
                    if (item != null) {
                        if (item.equals("...")) {
                            clientDir = Paths.get(clientDir.toFile().getAbsolutePath()).toFile().getParentFile().toPath();
                            updateClientView();
                        } else {
                            File selected = clientDir.resolve(item).toFile();
                            if (selected.isDirectory()) {
                                clientDir = selected.toPath();
                                updateClientView();
                            }
                        }
                    }
                }
            });

        } catch (IOException ioException) {
            log.error(String.valueOf(ioException));
        }
    }

}

