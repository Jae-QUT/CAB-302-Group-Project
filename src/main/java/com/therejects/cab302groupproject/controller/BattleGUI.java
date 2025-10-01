package com.therejects.cab302groupproject.controller;

//import com.almasb.fxgl.quest.Quest;
import com.therejects.cab302groupproject.Navigation.*;
import com.therejects.cab302groupproject.model.QuestionGenerator;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;


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

    private boolean isBattleOver = false;

    @FXML
    private void initialize() {
        // initial visibility
        mainMenu.setVisible(true);
        subMenu.setVisible(false);
        loadActiveMon();
        battleMessage.setText("What will Zabird do?");
        playerName.setText("Zabird");
        enemyName.setText("Anqchor");

        try {
            Image e = new Image(getClass().getResourceAsStream("/images/Sprites/Anqchor.png"));
            if (e != null) enemySprite.setImage(e);
        } catch (Exception ignored) { /* not critical */ }
    }

    private Monster[] playerMons = {
            new Monster("Zabird", "/images/Sprites/Zabird.png", 50),
            new Monster("Hawtosaur", "/images/Sprites/Hawtosaur.png", 50),
            new Monster("Batmon", "/images/Sprites/Batmon.png", 50)
    };
    private int activePlayerIndex = 0; // which monster is active

    private void loadActiveMon() {
        Monster active = playerMons[activePlayerIndex];
        playerName.setText(active.getName());
        playerSprite.setImage(new Image(getClass().getResourceAsStream(active.getSpritePath())));
        playerCurrentHp = active.getCurrentHp();
        playerMaxHp = active.getMaxHp();
        updateHpBars();
    }

    // Adds a few seconds before displaying next message
    private void waitThen(double seconds, Runnable next) {
        PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(e -> next.run());
        pause.play();
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
        double playerPercent = (double) playerCurrentHp / playerMaxHp;
        double enemyPercent = (double) enemyCurrentHp / enemyMaxHp;

        playerHp.setProgress(playerPercent);
        enemyHp.setProgress(enemyPercent);

        playerHpLabel.setText(playerCurrentHp + " / " + playerMaxHp);

        // Color thresholds
        setHpBarColor(playerHp, playerPercent);
        setHpBarColor(enemyHp, enemyPercent);
    }

    private void setHpBarColor(ProgressBar hpBar, double percent) {
        hpBar.getStyleClass().removeAll("hp-green", "hp-yellow", "hp-orange", "hp-red");

        if (percent <= 0.2) {
            hpBar.getStyleClass().add("hp-red");
        } else if (percent <= 0.4) {
            hpBar.getStyleClass().add("hp-orange");
        } else if (percent <= 0.6) {
            hpBar.getStyleClass().add("hp-yellow");
        } else {
            hpBar.getStyleClass().add("hp-green");
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

        if (qGen.checkAnswer(ctrl.userAnswer)) {
            enemyCurrentHp = Math.max(0, enemyCurrentHp - 10);
            updateHpBars();
            finishAction("Correct! Attack landed.");
            if (!isBattleOver) {
                enemyTurn();
            }
        } else {
            finishAction("Incorrect! Your Attack Missed!");
            if (!isBattleOver) {
                enemyTurn();
            }
        }

        /* Button light = new Button("Light Attack");
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
            playerCurrentHp = Math.max(0, playerCurrentHp + 10);
            finishAction("You Used Potion!");
            updateHpBars();
            if (!isBattleOver) {
                enemyTurn();
            }
        });

        manaRestore.setOnAction(e -> {
            finishAction("Used Mana Restore! (not implemented)");
            if (!isBattleOver) {
                enemyTurn();
            }
        });

        showSubMenu("Choose an item:", potion, manaRestore);
    }

    @FXML
    private void onSwitch() {
        subMenu.getChildren().clear();

        for (int i = 0; i < playerMons.length; i++) {
            int index = i;
            Monster mon = playerMons[i];

            Button monButton = new Button(mon.getName() + " (" + mon.getCurrentHp() + " HP)");
            monButton.setPrefWidth(150);
            monButton.setPrefHeight(44);

            monButton.setOnAction(e -> {
                if (index == activePlayerIndex) {
                    finishAction(mon.getName() + " is already active!");
                } else if (mon.getCurrentHp() <= 0) {
                    finishAction(mon.getName() + " has fainted and can’t be switched in!");
                } else {
                    activePlayerIndex = index;
                    loadActiveMon();
                    finishAction("Switched to " + mon.getName() + "!");
                    if (!isBattleOver) {
                        enemyTurn();
                    }
                }
            });

            subMenu.getChildren().add(monButton);
        }

        subMenu.getChildren().add(createBackButton());

        mainMenu.setVisible(false);
        subMenu.setVisible(true);
    }

    @FXML
    private void onForfeit() {
        Button confirm = new Button("Confirm Forfeit");
        confirm.setPrefWidth(150);
        confirm.setPrefHeight(44);
        confirm.setOnAction(e -> {
            finishAction("You forfeited the battle!");
            isBattleOver = true;
            mainMenu.setDisable(true);
        });
        showSubMenu("Are you sure?", confirm);
        sm().navigateTo("MAIN_MENU");

    }

    // Enemy Turn Based
    private void enemyTurn() {
        if (isBattleOver) {
            return;
        }
        mainMenu.setDisable(true);
        checkBattleEnd();
        waitThen(1.5, () -> {
            battleMessage.setText("Waiting for opponent");

            // Animate "..." while waiting
            Timeline dotsAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0.5), e -> battleMessage.setText("Waiting for opponent .")),
                    new KeyFrame(Duration.seconds(1.0), e -> battleMessage.setText("Waiting for opponent ..")),
                    new KeyFrame(Duration.seconds(1.5), e -> battleMessage.setText("Waiting for opponent ..."))
            );
            dotsAnimation.setCycleCount(3);
            dotsAnimation.play();

            // After waiting 3–6 seconds, choose action
            int delay = 2 + new Random().nextInt(4); // random 2–6 seconds
            PauseTransition wait = new PauseTransition(Duration.seconds(delay));
            wait.setOnFinished(e -> {
                dotsAnimation.stop(); // stop the animation when turn executes
                doEnemyAction(); // pick attack/miss/potion/switch
            });
            wait.play();
        });
    }

    private void doEnemyAction() {
        mainMenu.setDisable(true);
        Random rand = new Random();
        int roll = rand.nextInt(100);

        if (roll < 60) {
            playerCurrentHp = Math.max(0, playerCurrentHp - 10);
            battleMessage.setText(enemyName.getText() + " attacked and dealt " + 10 + " damage!");
        } else if (roll < 75) {
            battleMessage.setText(enemyName.getText() + " tried to attack but missed!");
        } else if (roll < 90){ // change condition to be && potion != 0
            battleMessage.setText(enemyName.getText() + " used a potion!");
            // to implement how many times of times can use potion,
            // you can do a set potion amount and subtract it each time using it (print for debugging)
            // then if potion is 0, then it is enemy turn again to try and roll a different number
            enemyCurrentHp = Math.max(0, enemyCurrentHp + 10);
        } else {
            battleMessage.setText(enemyName.getText() + " switched mons!");
            // switch logic later
        }
        updateHpBars();
        checkBattleEnd();
        waitThen(1, () -> {
            battleMessage.setText("What will Zabird do?");
            mainMenu.setDisable(false);
        });
    }

    private boolean allMonsFainted(Monster[] mons) {
        for (Monster mon : mons) {
            if (mon.getCurrentHp() > 0) return false;
        }
        return true;
    }

    private void checkBattleEnd() {
        if (allMonsFainted(playerMons)) {
            battleMessage.setText("All your monsters fainted! You lose.");
            isBattleOver = true;
            endBattlePopup();
        } else if (enemyCurrentHp == 0) {
            battleMessage.setText(enemyName.getText() + " fainted! You win!");
            isBattleOver = true;
            endBattlePopup();
        }
    }

    private void endBattlePopup() {
        mainMenu.setDisable(true);
        subMenu.setDisable(true);

        Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
        resultAlert.setTitle("Battle Over");
        resultAlert.setHeaderText(null);
        resultAlert.setContentText("🏆 " + winner + " has defeated " + loser + "!");

        resultAlert.showAndWait();

        sm().navigateTo("MAIN_MENU");
    }
}
