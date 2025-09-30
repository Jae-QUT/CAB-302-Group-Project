package com.therejects.cab302groupproject.controller;

//import com.almasb.fxgl.quest.Quest;
import com.therejects.cab302groupproject.Navigation.*;
import com.therejects.cab302groupproject.model.QuestionGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;


/**
 * A class that inherits the QuestionGenerator class that will generate the main battle screen for users to
 * duel it out with their chosen monsters. Users will be able to answer math questions from this screen
 * after choosing an action.
 */
public class BattleGUI extends QuestionGenerator {

    @FXML
    private ProgressBar playerHp, enemyHp;
    @FXML private Label playerHpLabel;
    @FXML private ProgressBar playerMana;
    @FXML private ImageView playerSprite;
    @FXML private ImageView enemySprite;
    @FXML private Label battleMessage;
    @FXML private GridPane mainMenu;
    @FXML private VBox subMenu;
    @FXML private Label playerName;
    @FXML private Label enemyName;

    private ScreenManager screenManager;

    /**
     * Creates the current instance of the screen manager for navigating between screens
     * @param sm Is the instance of the screen manager that we'll reference
     */
    public void setScreenManager(ScreenManager sm) { this.screenManager = sm; }

    // helper to use it safely

    /**
     *
     * @return
     */
    public ScreenManager sm() {
        if (screenManager == null) {
            // fallback if someone forgot to inject; build from current window
            Stage stage = (Stage) battleMessage.getScene().getWindow();
            screenManager = new ScreenManager(stage);
        }
        return screenManager;
    }


    private String winner;
    private String loser;
    private String outcome;
    private int playerMaxHp = 50;
    private int enemyMaxHp = 50;

    private int playerCurrentHp = playerMaxHp;
    private int enemyCurrentHp = enemyMaxHp;
    private String user = this.user;
    private String enemy = "AI";


    @FXML
    private void initialize() {
        // initial visibility
        mainMenu.setVisible(true);
        subMenu.setVisible(false);
        battleMessage.setText("What will Zabird do?");
        playerName.setText("Zabird");
        enemyName.setText("Anqchor");

        try {
            Image p = new Image(getClass().getResourceAsStream("/images/player.png"));
            playerSprite.setImage(p);
            Image e = new Image(getClass().getResourceAsStream("/images/enemy.png"));
            if (p != null) playerSprite.setImage(p);
            if (e != null) enemySprite.setImage(e);
        } catch (Exception ignored) { /* not critical */ }

    }

// show submenu: hides mainMenu and fills subMenu with provided buttons + a Back button
    private void showSubMenu(String title, Button... options) {
        subMenu.getChildren().clear();

        battleMessage.setText(title);

        // horizontal box for the options
        HBox optionRow = new HBox(10); // spacing = 10
        optionRow.getChildren().addAll(options);

        subMenu.getChildren().addAll(optionRow, createBackButton());

        mainMenu.setVisible(false);
        subMenu.setVisible(true);
    }

    // create the universal "Back" button
    private Button createBackButton() {
        Button back = new Button("Back");
        back.setPrefWidth(150);
        back.setPrefHeight(44);
        back.setOnAction(e -> {
            subMenu.getChildren().clear();
            subMenu.setVisible(false);
            mainMenu.setVisible(true);
            battleMessage.setText("What will Zabird do?");
        });
        return back;
    }

    // common routine to finish an action (restore main menu)
    private void finishAction(String resultText) {
        battleMessage.setText(resultText);
        subMenu.getChildren().clear();
        subMenu.setVisible(false);
        mainMenu.setVisible(true);
    }

    // helper method to refresh HP bars + text
    private void updateHpBars() {
        playerHp.setProgress((double) playerCurrentHp / playerMaxHp);
        enemyHp.setProgress((double) enemyCurrentHp / enemyMaxHp);

        playerHpLabel.setText(playerCurrentHp + " / " + playerMaxHp);

        // Disabled activity if Hp = 0
        if (enemyCurrentHp == 0 || playerCurrentHp == 0) {
            mainMenu.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Congratulations! " + winner + " has defeated " + loser + "!").showAndWait();

        }

    }

    /* ---------- Button Handlers ---------- */

    @FXML
    private void onFight() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/therejects/cab302groupproject/QuestionGen-view.fxml"));
            Parent root = loader.load();
            QuestionGenController ctrl = loader.getController();
            QuestionGenerator qGen = ctrl.generator;
            ctrl.setQuestionGenerator(qGen);

            Stage popup = new Stage();
            popup.setTitle("Answer to Attack!");
            popup.setScene(new Scene(root));
            popup.initModality(Modality.WINDOW_MODAL);
            Window owner = battleMessage.getScene().getWindow();
            popup.initOwner(owner);
            popup.showAndWait();

            if(qGen.checkAnswer(ctrl.userAnswer))
            {
                    enemyCurrentHp = Math.max(0, enemyCurrentHp - 10);
                    updateHpBars();
                    finishAction("Correct! Attack landed.");
            }
            else
            {
                finishAction("Wrong! Your Attack Missed!");
            }



        /*Button light = new Button("Light Attack");
        Button heavy = new Button("Heavy Attack");

        light.setPrefWidth(150);
        light.setPrefHeight(44);
        heavy.setPrefWidth(150);
        heavy.setPrefHeight(44);

        light.setOnAction(e -> {
            enemyCurrentHp = Math.max(0, enemyCurrentHp - 10); // 10 damage
            updateHpBars();
            finishAction("Zabird used Light Attack!");
        });

        heavy.setOnAction(e -> {
            enemyCurrentHp = Math.max(0, enemyCurrentHp - 20); // 20 damage
            updateHpBars();
            finishAction("Zabird used Heavy Attack!");
        });

        showSubMenu("Attack", light, heavy);*/
    }

    @FXML
    private void onItems() {
        Button potion = new Button("Health Potion");
        Button manaRestore = new Button("Mana Restore (not working)");

        potion.setPrefWidth(150);
        potion.setPrefHeight(44);
        manaRestore.setPrefWidth(150);
        manaRestore.setPrefHeight(44);

        potion.setOnAction(e -> {
            playerHp.setProgress(Math.min(1.0, playerHp.getProgress() + 0.20));
            finishAction("Used Potion!");
        });

        manaRestore.setOnAction(e -> {
            playerHp.setProgress(Math.min(1.0, playerHp.getProgress() + 0.45));
            finishAction("Used Mana Restore!");
        });

        showSubMenu("Choose an item:", potion, manaRestore);
    }

    @FXML
    private void onSwitch() {
        Button mon1 = new Button("Hawtosaur");
        Button mon2 = new Button("Anqchor");

        mon1.setPrefWidth(150);
        mon1.setPrefHeight(44);
        mon2.setPrefWidth(150);
        mon2.setPrefHeight(44);

        mon1.setOnAction(e -> {
            // place-holder behaviour
            finishAction("Switched to Hawtosaur!");
        });

        mon2.setOnAction(e -> {
            finishAction("Switched to Anqchor!");
        });

        showSubMenu("Choose a Mon:", mon1, mon2);
    }


    @FXML
    private void onForfeit() {
        Button confirm = new Button("Confirm Forfeit");
        confirm.setPrefWidth(150);
        confirm.setPrefHeight(44);
        confirm.setOnAction(e -> {
            finishAction("You forfeited the battle!");
            // optionally disable main menu so you can't act after forfeiting
            mainMenu.setDisable(true);
        });
        showSubMenu("Are you sure?", confirm);
        sm().navigateTo("MAIN_MENU");


    }

}
