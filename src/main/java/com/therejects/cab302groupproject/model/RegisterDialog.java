package com.therejects.cab302groupproject.model;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;

/**
 * JavaFX dialog for registering a new user account.
 * Polished with rounded corners, transparent stage, and consistent theme.
 */
public class RegisterDialog extends Dialog<RegisterResult> {

    public RegisterDialog() {
        setTitle("Create Account");

        // === Stage & Scene polish (remove white corners) ===
        initStyle(StageStyle.TRANSPARENT);
        DialogPane pane = getDialogPane();
        pane.setBackground(new Background(
                new BackgroundFill(Color.web("#3BA8FF"), new CornerRadii(20), Insets.EMPTY)
        ));
        pane.setBorder(new Border(new BorderStroke(
                Color.web("#3BA8FF"), BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT
        )));

        // make the dialog itself transparent (so rounded corners don't show white)
        pane.getScene().setFill(Color.TRANSPARENT);

        // apply a clipping mask so corners are actually rounded
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(pane.widthProperty());
        clip.heightProperty().bind(pane.heightProperty());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        pane.setClip(clip);

        // === Buttons ===
        ButtonType createBtnType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(createBtnType, ButtonType.CANCEL);

        // === Fields ===
        TextField username = new TextField();
        TextField email = new TextField();
        TextField grade = new TextField();

        PasswordField pwHidden = new PasswordField();
        TextField pwShown = new TextField();
        pwShown.setManaged(false);
        pwShown.setVisible(false);
        pwShown.textProperty().bindBidirectional(pwHidden.textProperty());

        PasswordField pw2Hidden = new PasswordField();
        TextField pw2Shown = new TextField();
        pw2Shown.setManaged(false);
        pw2Shown.setVisible(false);
        pw2Shown.textProperty().bindBidirectional(pw2Hidden.textProperty());

        CheckBox show = new CheckBox("Show");
        show.selectedProperty().addListener((obs, was, is) -> {
            pwShown.setManaged(is); pwShown.setVisible(is);
            pwHidden.setManaged(!is); pwHidden.setVisible(!is);
            pw2Shown.setManaged(is); pw2Shown.setVisible(is);
            pw2Hidden.setManaged(!is); pw2Hidden.setVisible(!is);
        });

        // === Layout ===
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        int r = 0;
        grid.add(new Label("Username"), 0, r); grid.add(username, 1, r++);
        grid.add(new Label("Student Email"), 0, r); grid.add(email, 1, r++);
        grid.add(new Label("Grade/Year Level"), 0, r); grid.add(grade, 1, r++);
        grid.add(new Label("Password"), 0, r); grid.add(pwHidden, 1, r); grid.add(pwShown, 1, r++);
        grid.add(new Label("Confirm Password"), 0, r); grid.add(pw2Hidden, 1, r); grid.add(pw2Shown, 1, r++);
        grid.add(show, 1, r);

        pane.setContent(grid);

        // === Enable/Disable "Create" dynamically ===
        Node createBtn = pane.lookupButton(createBtnType);
        Runnable enableCheck = () -> {
            boolean ok = !username.getText().trim().isEmpty()
                    && !email.getText().trim().isEmpty()
                    && !grade.getText().trim().isEmpty()
                    && !pwHidden.getText().isEmpty()
                    && !pw2Hidden.getText().isEmpty();
            createBtn.setDisable(!ok);
        };
        username.textProperty().addListener((o,a,b)->enableCheck.run());
        email.textProperty().addListener((o,a,b)->enableCheck.run());
        grade.textProperty().addListener((o,a,b)->enableCheck.run());
        pwHidden.textProperty().addListener((o,a,b)->enableCheck.run());
        pw2Hidden.textProperty().addListener((o,a,b)->enableCheck.run());
        enableCheck.run();

        // === Validation and Result ===
        setResultConverter(btn -> {
            if (btn != createBtnType) return null;

            if (!pwHidden.getText().equals(pw2Hidden.getText())) {
                new Alert(Alert.AlertType.WARNING, "Passwords do not match.").showAndWait();
                return null;
            }

            int g;
            try {
                g = Integer.parseInt(grade.getText().trim());
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.WARNING, "Grade must be a number.").showAndWait();
                return null;
            }

            return new RegisterResult(
                    username.getText().trim(),
                    email.getText().trim(),
                    g,
                    pwHidden.getText()
            );
        });
    }
}
