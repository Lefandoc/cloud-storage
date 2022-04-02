package ru.gb.lefandoc.cloudstorage.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

    private Boolean isAuth;
    private String login;
    private String password;

    public void download(ActionEvent actionEvent) throws IOException {
        try {
            if (isAuth) {
                String serverItem = serverView.getSelectionModel().getSelectedItem();
                oos.writeObject(new FileRequest(serverItem));
                log.info("Request {} from server", serverItem);
            }
        } catch (NullPointerException e) {
            log.error(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        try {
            if (isAuth) {
                String clientItem = clientView.getSelectionModel().getSelectedItem();
                oos.writeObject(new FileMessage(clientDir.resolve(clientItem)));
                if (checkDelete.isSelected()) {
                    Files.delete(clientDir.resolve(clientItem));
                    updateClientView();
                }
                log.info("Send {} to server", clientItem);
            }
        } catch (NullPointerException e) {
            log.error(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void chooseDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(clientDir.toFile());
        clientDir = directoryChooser.showDialog(new Stage()).toPath();
        if ("serverDir".equals(clientDir.toFile().getName())) {
            log.error("Access denied - SERVER FOLDER");
            return;
        }
        updateClientView();
    }

    public void login(ActionEvent actionEvent) throws IOException {
        AuthDialog dialog = new AuthDialog();
        dialog.DialogForm();
        dialog.showAndWait().ifPresent(userChoice -> {
            if (userChoice.startsWith("ERROR:") || userChoice.startsWith("CANCELLED:")) {
                log.warn(dialog.getResult());
                return;
            }
            String[] userInput = userChoice.split(" ");
            login = userInput[0];
            password = userInput[1];
        });
        oos.writeObject(new AuthMessage(login, password));
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
                    case AUTH_STATUS:
                        AuthStatusMessage asm = (AuthStatusMessage) msg;
                        isAuth = asm.getIsAuth();
                        break;
                }
            }
        } catch (Exception e) {
            log.error(e.getClass().getName() + ": " + e.getMessage());
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
                        } else if (item.equals("serverDir")) {
                            log.error("Access denied - SERVER FOLDER");
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
            log.error(ioException.getClass().getName() + ": " + ioException.getMessage());
        }
    }

}

