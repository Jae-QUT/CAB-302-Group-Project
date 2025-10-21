package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.model.Monster;
import com.therejects.cab302groupproject.model.MonDatabase;
import com.therejects.cab302groupproject.model.User;
//import com.example.mon.app.Database;

import com.therejects.cab302groupproject.Navigation.*;
import com.therejects.cab302groupproject.model.QuestionGenerator;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;

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
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/**
 * A class that inherits the QuestionGenerator class that will generate the main battle screen for users to
 * duel it out with their chosen monsters. Users will be able to answer math questions from this screen
 * after choosing an action.

 */
public class BattleGUI extends QuestionGenerator {

    @FXML private ProgressBar playerHp, enemyHp;
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

    ScoringSystem score = new ScoringSystem();
    User user = User.getCurrentUser();

    /**
     * Creates the current instance of the screen manager for navigating between screens
     * @param sm Is the instance of the screen manager that we'll reference
     */
    public void setScreenManager(ScreenManager sm) { this.screenManager = sm; }

    // helper to use it safely

    /**
     *
     * @return the screenManager if there is an issue injecting into the manager. It will return
     */
    public ScreenManager sm() {
        if (screenManager == null) {
            // fallback if someone forgot to inject; build from current window
            Stage stage = (Stage) battleMessage.getScene().getWindow();
            screenManager = new ScreenManager(stage);
        }
        return screenManager;
    }

    public void initializePlayerTeam(List<Monster> selectedMons) {
        this.playerMons = selectedMons.toArray(new Monster[0]);
        this.activePlayerIndex = 0;
        loadActiveMon();
    }

    private void initializeEnemyTeam() {
        try {
            List<Monster> allMons = MonDatabase.getAllMonsters();

            Collections.shuffle(allMons);
            enemyMons = allMons.stream().limit(3).toArray(Monster[]::new);

            activeEnemyIndex = 0;
            loadActiveEnemy();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to load enemy mons from DB. Using default mons.");
            // fallback hardcoded monsters
            enemyMons = new Monster[]{
                    new Monster("Zabird", "/images/Sprites/Zabird.png", 50),
                    new Monster("Anqchor", "/images/Sprites/Anqchor.png", 50),
                    new Monster("Sharkle", "/images/Sprites/Sharkle.png", 50)
            };
            activeEnemyIndex = 0;
            loadActiveEnemy();
        }
    }

    private String winner;
    private String loser;
    private int playerMaxHp = 50;
    private int playerPotions = 2;
    private int enemyMaxHp = 50;
    private int enemyPotions = 2;

    private int playerCurrentHp = playerMaxHp;
    private int enemyCurrentHp = enemyMaxHp;

    private Monster[] playerMons;
    private int activePlayerIndex = 0;

    private Monster[] enemyMons;
    private int activeEnemyIndex = 0;  // current active mon

    /**
     * Setter for pre-selected monsters from selection screen
     * @param selectedMons List of selected monsters
     */
    public void setPlayerMons(Monster[] selectedMons) {
        this.playerMons = selectedMons;
        activePlayerIndex = 0;
        loadActiveMon();
    }

    private boolean isBattleOver = false;

    @FXML
    private void initialize() {

        if (playerMons == null) {
            try {
                List<Monster> monsFromDb = MonDatabase.getAllMonsters();
                playerMons = monsFromDb.toArray(new Monster[0]);
            } catch (SQLException e) {
                e.printStackTrace();
                playerMons = new Monster[] {
                        new Monster(playerMons[activePlayerIndex].getName(), "/images/Sprites/" + playerMons[activePlayerIndex] + ".png", 50)
                };
            }
        }

        mainMenu.setVisible(true);
        subMenu.setVisible(false);
        battleMessage.setText("What will " + playerMons[activePlayerIndex].getName() + " do?");
        playerName.setText(playerMons[activePlayerIndex].getName());
        loadActiveMon();

        initializeEnemyTeam();
        activeEnemyIndex = 0;
        enemyName.setText(enemyMons[0].getName());
        String path = enemyMons[0].getSpritePath();
        URL imageUrl = getClass().getResource(path);

        if (imageUrl == null) {
            System.err.println("Sprite not found: " + path);
        } else {
            enemySprite.setImage(new Image(imageUrl.toString()));
        }

        enemyCurrentHp = enemyMons[0].getCurrentHp();
        enemyMaxHp = enemyMons[0].getMaxHp();
        updateHpBars();
    }

    private void loadActiveMon() {
        Monster active = playerMons[activePlayerIndex];
        playerName.setText(active.getName());
        playerSprite.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(active.getSpritePath()))));
        playerCurrentHp = active.getCurrentHp();
        playerMaxHp = active.getMaxHp();
        updateHpBars();
    }

    private void loadActiveEnemy() {
        Monster activeEnemy = enemyMons[activeEnemyIndex];
        enemyName.setText(activeEnemy.getName());
        enemySprite.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(activeEnemy.getSpritePath()))
        ));
        enemyCurrentHp = activeEnemy.getCurrentHp();
        enemyMaxHp = activeEnemy.getMaxHp();
        updateHpBars();
        battleMessage.setText("Enemy sent out " + activeEnemy.getName() + "!");
        waitThen(1.5, () -> {
            battleMessage.setText("What will " + playerMons[activePlayerIndex].getName() + " do?");
        });
    }

    // Adds a few seconds before displaying next action
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

    private Button createBackButton() {
        Button back = new Button("Back");
        back.setPrefWidth(250);
        back.setPrefHeight(44);
        back.setOnAction(e -> {
            subMenu.getChildren().clear();
            subMenu.setVisible(false);
            mainMenu.setVisible(true);
            battleMessage.setText("What will " + playerMons[activePlayerIndex].getName() + " do?");
        });
        return back;
    }

    private void finishAction(String resultText) {
        battleMessage.setText(resultText);
        subMenu.getChildren().clear();
        subMenu.setVisible(false);
        mainMenu.setVisible(true);
    }

    private void updateHpBars() {
        double playerPercent = (double) playerCurrentHp / playerMaxHp;
        double enemyPercent = (double) enemyCurrentHp / enemyMaxHp;

        playerHp.setProgress(playerPercent);
        enemyHp.setProgress(enemyPercent);

        playerHpLabel.setText(playerCurrentHp + " / " + playerMaxHp);

        // Color thresholds
        setHpBarColor(playerHp, playerPercent);
        setHpBarColor(enemyHp, enemyPercent);

        winner = (enemyCurrentHp == 0) ? playerName.getText() : enemyName.getText();
        loser = (enemyCurrentHp == 0) ? enemyName.getText() : playerName.getText();
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
    private void onFight() throws IOException, SQLException {
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
            score.calculateDelta(true);
            score.addToScore(this.user.getUsername(), score.delta);
            System.out.println();
            if (!isBattleOver) {
                enemyMons[activeEnemyIndex].setCurrentHp(enemyCurrentHp);
                enemyTurn();
            }
        } else {
            finishAction("Incorrect! Your Attack Missed!");
            score.calculateDelta(false);
            score.subtractFromScore(this.user.getUsername(), score.delta);
            finishAction("Wrong! Your Attack Missed!");
            if (!isBattleOver) {
                enemyMons[activeEnemyIndex].setCurrentHp(enemyCurrentHp);
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
        Button potion = new Button("Health Potion (x"+ playerPotions +")");
        // Button manaRestore = new Button("Mana Restore (not working)");

        potion.setPrefWidth(250);
        potion.setPrefHeight(40);
        // manaRestore.setPrefWidth(200);
        // manaRestore.setPrefHeight(44);

        // Disable potion if none left
        if (playerPotions <= 0) {
            potion.setDisable(true);
            potion.setText("No Potions Left");
        }

        potion.setOnAction(e -> {
            if (playerPotions <= 0) {
                finishAction("No potions left!");
                return;
            }

            try {
                // Load question popup
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/therejects/cab302groupproject/QuestionGen-view.fxml"));
                Parent root = loader.load();
                QuestionGenController ctrl = loader.getController();
                QuestionGenerator qGen = ctrl.generator;
                ctrl.setQuestionGenerator(qGen);

                Stage popup = new Stage();
                popup.setTitle("Answer for Potion!");
                popup.setScene(new Scene(root));
                popup.initModality(Modality.WINDOW_MODAL);
                popup.initOwner(battleMessage.getScene().getWindow());
                popup.showAndWait();

                int healAmount;
                if (qGen.checkAnswer(ctrl.userAnswer)) {
                    healAmount = 20;
                    finishAction("Correct! Potion restored 20 HP!");
                } else {
                    healAmount = 10;
                    finishAction("Incorrect! Potion only restored 10 HP!");
                }

                // Apply healing without exceeding max HP
                playerCurrentHp = Math.min(playerMaxHp, playerCurrentHp + healAmount);
                updateHpBars();

                // Reduce potion count
                playerPotions--;

                if (!isBattleOver) {
                    enemyTurn();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                finishAction("Something went wrong with the potion.");
            }
        });

//        manaRestore.setOnAction(e -> {
//            finishAction("Used Mana Restore! (not implemented)");
//            if (!isBattleOver) {
//                enemyTurn();
//            }
//        });

        showSubMenu("Choose an item:", potion); // manaRestore);
    }

    @FXML
    private void onSwitch() {
        showSwitchMenu(false);
    }

    private void forceSwitchMenu() {
        showSwitchMenu(true);
    }

    private void showSwitchMenu(boolean forced) {
        subMenu.getChildren().clear();
        mainMenu.setVisible(false);
        subMenu.setVisible(true);

        battleMessage.setText("Choose a Mon:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        for (int i = 0; i < playerMons.length; i++) {
            Monster mon = playerMons[i];
            Button btn = new Button(mon.getName() + " (" + mon.getCurrentHp() + " HP)");
            btn.setPrefWidth(250);
            btn.setPrefHeight(44);

            if (mon.getCurrentHp() == 0 || i == activePlayerIndex) {
                btn.setDisable(true);
            }

            final int idx = i;
            btn.setOnAction(e -> {
                // Save current active mon’s HP before switching
                playerMons[activePlayerIndex].setCurrentHp(playerCurrentHp);

                activePlayerIndex = idx;
                loadActiveMon();
                finishAction("Go! " + mon.getName() + "!");

                // Enemy turn if not forced
                if (!forced && !isBattleOver) {
                    enemyTurn();
                }
            });

            grid.add(btn, i % 2, i / 2); // 2x2 layout
        }

        subMenu.getChildren().add(grid);

        if (!forced) {
            subMenu.getChildren().add(createBackButton());
        }
    }

    @FXML
    private void onForfeit() {
        Button confirm = new Button("Confirm Forfeit");
        confirm.setPrefWidth(250);
        confirm.setPrefHeight(44);
        confirm.setOnAction(e -> {
            finishAction("You forfeited the battle!");
            isBattleOver = true;
            showBattleEndPopup(false);
        });
        showSubMenu("Are you sure?", confirm);
//        sm().navigateTo("MAIN_MENU");

    }

    // Enemy Turn Based
    private void enemyTurn() {
        mainMenu.setDisable(true);
        checkBattleEnd();
        if (isBattleOver) {
            return;
        }

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

            int delay = 2 + new Random().nextInt(4); // random 2–6 seconds
            PauseTransition wait = new PauseTransition(Duration.seconds(delay));
            wait.setOnFinished(e -> {
                dotsAnimation.stop();
                doEnemyAction();
            });
            wait.play();
        });
    }

    private void doEnemyAction() {
        mainMenu.setDisable(true);

        double enemyHpPercent = (double) enemyCurrentHp / enemyMaxHp;
        double playerHpPercent = (double) playerCurrentHp / playerMaxHp;

        // Debug
        // System.out.println("AI Decision Check: enemyHP=" + enemyHpPercent + " playerHP=" + playerHpPercent);

        String enemy = enemyName.getText();
        Random rand = new Random();

        if (enemyHpPercent <= 0.3 && enemyPotions > 0) {
            int healAmount = 10;
            enemyCurrentHp = Math.min(enemyMaxHp, enemyCurrentHp + healAmount);
            enemyPotions--;
            battleMessage.setText(enemy + " used a potion and recovered " + healAmount + " HP!");
        }
        else if (enemyHpPercent < playerHpPercent * 0.5 && rand.nextDouble() < 0.2) {
            // Low HP – consider switching if another mon is alive
            boolean canSwitch = false;
            for (int i = 0; i < enemyMons.length; i++) {
                if (i != activeEnemyIndex && enemyMons[i].getCurrentHp() > 0) {
                    canSwitch = true;
                    break;
                }
            }

            if (canSwitch) {
                battleMessage.setText(enemyName.getText() + " decided to switch mons!");
                waitThen(1.5, this::switchEnemyMon);
                return;
            } else {
                int damage = 25 + rand.nextInt(10);
                playerCurrentHp = Math.max(0, playerCurrentHp - damage);
                battleMessage.setText(enemyName.getText() + " attacked and dealt 10 damage!");
            }
        }
        else {
            if (rand.nextDouble() < 0.1) {
                battleMessage.setText(enemy + " tried to attack but missed!");
            } else {
                int damage = 10;
                playerCurrentHp = Math.max(0, playerCurrentHp - damage);
                battleMessage.setText(enemy + " attacked and dealt " + damage + " damage!");
            }
        }
        updateHpBars();
        checkBattleEnd();
        if (isBattleOver) return;

        waitThen(1, () -> {
            battleMessage.setText("What will " + playerMons[activePlayerIndex].getName() + " do?");
            mainMenu.setDisable(false);
        });
    }

    private void switchEnemyMon() {
        for (int i = 0; i < enemyMons.length; i++) {
            if (i != activeEnemyIndex && enemyMons[i].getCurrentHp() > 0) {
                activeEnemyIndex = i;
                Monster newMon = enemyMons[i];

                enemyName.setText(newMon.getName());
                enemySprite.setImage(new Image(getClass().getResource(newMon.getSpritePath()).toString()));
                enemyCurrentHp = newMon.getCurrentHp();
                enemyMaxHp = newMon.getMaxHp();

                battleMessage.setText("Enemy switched to " + newMon.getName() + "!");
                updateHpBars();
                return;
            }
            waitThen(1, () -> {
                battleMessage.setText("What will " + playerMons[activePlayerIndex].getName() + " do?");
                mainMenu.setDisable(false);
            });
        }
    }

    private boolean allMonsFainted(Monster[] mons) {
        for (Monster mon : mons) {
            if (mon.getCurrentHp() > 0) return false;
        }
        return true;
    }

    private void checkBattleEnd() {
        // If player's current Mon fainted
        if (playerCurrentHp <= 0) {
            playerMons[activePlayerIndex].setCurrentHp(0);

            // Check if other mons are still alive
            boolean hasOtherMons = false;
            for (Monster mon : playerMons) {
                if (mon.getCurrentHp() > 0) {
                    hasOtherMons = true;
                    break;
                }
            }

            if (hasOtherMons) {
                battleMessage.setText(playerName.getText() + " fainted! Choose your next Mon.");
                forceSwitchMenu();
                return;
            } else {
                battleMessage.setText(playerName.getText() + " fainted! You lose!");
                isBattleOver = true;
                mainMenu.setDisable(true);
                showBattleEndPopup(false);
                return;
            }
        }

        if (enemyCurrentHp <= 0) {
            enemyMons[activeEnemyIndex].setCurrentHp(0);
            updateHpBars();

            boolean hasOtherMons = false;
            for (int i = 0; i < enemyMons.length; i++) {
                if (enemyMons[i].getCurrentHp() > 0) {
                    hasOtherMons = true;
                    activeEnemyIndex = i;
                    break;
                }
            }

            if (hasOtherMons) {
                battleMessage.setText(enemyName.getText() + " fainted! Enemy is sending out another Mon!");
                waitThen(1.5, this::loadActiveEnemy);
            } else {
                battleMessage.setText(enemyName.getText() + " fainted! You win!");
                isBattleOver = true;
                mainMenu.setDisable(true);
                showBattleEndPopup(true);
            }
        }
    }

    private void showBattleEndPopup(boolean playerWon) {
        mainMenu.setDisable(true);

        String title = playerWon ? "Victory!" : "Defeat!";
        String msg = playerWon
                ? "🏆 You have defeated all enemy Mons!"
                : "💀 All your Mons have fainted...";

        Platform.runLater(() -> {
            Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
            resultAlert.setTitle("Battle Over");
            resultAlert.setHeaderText(title);
            resultAlert.setContentText(msg);

            resultAlert.setOnHidden(e -> {
                sm().navigateTo("MAIN_MENU");
            });

            resultAlert.showAndWait();
        });
    }
}