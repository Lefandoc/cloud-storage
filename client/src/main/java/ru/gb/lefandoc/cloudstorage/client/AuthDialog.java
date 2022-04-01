package ru.gb.lefandoc.cloudstorage.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.controlsfx.control.action.Action;

public class AuthDialog extends Dialog<String> {

    final TextField username = new TextField();
    final PasswordField password = new PasswordField();


    public void DialogForm() {
        setTitle("Login");
        username.setPromptText("Login");
        password.setPromptText("Password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        ButtonType btnOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(btnOK, btnCancel);

        getDialogPane().setContent(grid);

        Platform.runLater(username::requestFocus);

        setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == btnOK.getButtonData()) {
                if (!username.getText().equals("") && !password.getText().equals("")) {
                    return username.getText() + " " + password.getText();
                } else return "error";
            }
            if (dialogButton.getButtonData() == btnCancel.getButtonData()) {
                return null;
            }
            return null;
        });
    }
}