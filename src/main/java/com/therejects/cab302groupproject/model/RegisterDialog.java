package com.therejects.cab302groupproject.model;
import com.example.mon.app.RegisterResult;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;

/**
 * JavaFX dialog for registering a new user account.
 * Connects to the 'Show' checkbox that can be clicked to toggle the visibility of the password
 * Automatically enables/disables the create button in 'Register'
 * Handles GUI
 * Validates matching passwords and ensures numeric input in year level
 *
 */




public class RegisterDialog extends Dialog<RegisterResult> {
    /**
     * {@link RegisterDialog} is the application that launches the registration popup for when a
     * new user makes their account.
     * Done as an application but should be revised into the FXML format to be in line with the unit requirements
     */
    public RegisterDialog() {
        setTitle("Create Account");

        ButtonType createBtnType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createBtnType, ButtonType.CANCEL);

        TextField username = new TextField();
        TextField email    = new TextField();
        TextField grade    = new TextField();

        // Password fields (hidden + visible for show/hide)
        PasswordField pwHidden = new PasswordField();
        TextField     pwShown  = new TextField();
        pwShown.setManaged(false);
        pwShown.setVisible(false);
        pwShown.textProperty().bindBidirectional(pwHidden.textProperty());

        PasswordField pw2Hidden = new PasswordField();
        TextField     pw2Shown  = new TextField();
        pw2Shown.setManaged(false);
        pw2Shown.setVisible(false);
        pw2Shown.textProperty().bindBidirectional(pw2Hidden.textProperty());

        CheckBox show = new CheckBox("Show");
        show.selectedProperty().addListener((obs, was, is) -> {
            pwShown.setManaged(is);    pwShown.setVisible(is);
            pwHidden.setManaged(!is);  pwHidden.setVisible(!is);
            pw2Shown.setManaged(is);   pw2Shown.setVisible(is);
            pw2Hidden.setManaged(!is); pw2Hidden.setVisible(!is);
        });

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        int r = 0;
        grid.add(new Label("Username"),          0, r); grid.add(username, 1, r++);
        grid.add(new Label("Student Email"),     0, r); grid.add(email,    1, r++);
        grid.add(new Label("Grade/Year Level"),  0, r); grid.add(grade,    1, r++);
        grid.add(new Label("Password"),          0, r); grid.add(pwHidden, 1, r); grid.add(pwShown,  1, r++);
        grid.add(new Label("Confirm Password"),  0, r); grid.add(pw2Hidden,1, r); grid.add(pw2Shown, 1, r++);
        grid.add(show, 1, r);

        // Enable "Create" only when fields are non-empty
        Node createBtn = getDialogPane().lookupButton(createBtnType);
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

        getDialogPane().setContent(grid);

        // Build result
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
