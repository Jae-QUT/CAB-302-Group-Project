package com.therejects.cab302groupproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class BattleGUI {

    @FXML private ProgressBar playerHp;
    @FXML private ProgressBar enemyHp;
    @FXML private ImageView playerSprite;
    @FXML private ImageView enemySprite;
    @FXML private Label battleMessage;
    @FXML private GridPane mainMenu;
    @FXML private VBox subMenu;
    @FXML private Label playerName;
    @FXML private Label enemyName;

    @FXML
    private void initialize() {
        // initial visibility
        mainMenu.setVisible(true);
        subMenu.setVisible(false);
        battleMessage.setText("What will Pikachu do?");
        playerName.setText("Charmander Lv.5");
        enemyName.setText("Wild Pikachu Lv.5");

        try {
            Image p = new Image(getClass().getResourceAsStream("/images/player.png"));
            playerSprite.setImage(p);
            Image e = new Image(getClass().getResourceAsStream("/images/enemy.png"));
            if (p != null) playerSprite.setImage(p);
            if (e != null) enemySprite.setImage(e);
        } catch (Exception ignored) { /* not critical */ }
    }

    /* ---------- helper UI methods ---------- */

    // show submenu: hides mainMenu and fills subMenu with provided buttons + a Back button
    private void showSubMenu(String title, Button... buttons) {
        mainMenu.setVisible(false);
        subMenu.getChildren().clear();
        subMenu.setVisible(true);
        battleMessage.setText(title);

        for (Button b : buttons) {
            subMenu.getChildren().add(b);
        }
        subMenu.getChildren().add(createBackButton());
    }

    // create the universal "Back" button
    private Button createBackButton() {
        Button back = new Button("Back");
        back.setPrefWidth(140);
        back.setOnAction(e -> {
            subMenu.getChildren().clear();
            subMenu.setVisible(false);
            mainMenu.setVisible(true);
            battleMessage.setText("What will Pikachu do?");
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

    /* ---------- button handlers ---------- */

    @FXML
    private void onFight() {
        Button light = new Button("Light Attack");
        Button heavy = new Button("Heavy Attack");

        light.setPrefWidth(140);
        heavy.setPrefWidth(140);

        light.setOnAction(e -> {
            // example: deal small damage to enemy
            double newProg = Math.max(0, enemyHp.getProgress() - 0.12);
            enemyHp.setProgress(newProg);
            finishAction("Charmander used Light Attack!");
        });

        heavy.setOnAction(e -> {
            double newProg = Math.max(0, enemyHp.getProgress() - 0.30);
            enemyHp.setProgress(newProg);
            finishAction("Charmander used Heavy Attack!");
        });

        showSubMenu("Choose a move:", light, heavy);
    }

    @FXML
    private void onItems() {
        Button potion = new Button("Potion");
        Button superPotion = new Button("Super Potion");

        potion.setPrefWidth(140);
        superPotion.setPrefWidth(140);

        potion.setOnAction(e -> {
            playerHp.setProgress(Math.min(1.0, playerHp.getProgress() + 0.20));
            finishAction("Used Potion!");
        });

        superPotion.setOnAction(e -> {
            playerHp.setProgress(Math.min(1.0, playerHp.getProgress() + 0.45));
            finishAction("Used Super Potion!");
        });

        showSubMenu("Choose an item:", potion, superPotion);
    }

    @FXML
    private void onSwitch() {
        Button mon1 = new Button("Charmander");
        Button mon2 = new Button("Pidgey");

        mon1.setPrefWidth(140);
        mon2.setPrefWidth(140);

        mon1.setOnAction(e -> {
            // place-holder behaviour
            finishAction("Switched to Charmander!");
        });

        mon2.setOnAction(e -> {
            finishAction("Switched to Pidgey!");
        });

        showSubMenu("Choose a Pokémon:", mon1, mon2);
    }

    @FXML
    private void onForfeit() {
        Button confirm = new Button("Confirm Forfeit");
        confirm.setPrefWidth(140);
        confirm.setOnAction(e -> {
            finishAction("You forfeited the battle!");
            // optionally disable main menu so you can't act after forfeiting
            mainMenu.setDisable(true);
        });
        showSubMenu("Are you sure?", confirm);
    }
}