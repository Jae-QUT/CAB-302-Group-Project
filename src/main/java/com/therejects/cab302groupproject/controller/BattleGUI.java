package com.therejects.cab302groupproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class BattleGUI {

    @FXML private Label enemyLabel;
    @FXML private Label playerLabel;
    @FXML private Label battleLog;

    @FXML private Button fightBtn;
    @FXML private Button bagBtn;
    @FXML private Button pokemonBtn;
    @FXML private Button forfeitBtn;

    @FXML
    private void initialize() {
        fightBtn.setOnAction(e -> battleLog.setText("Choose a move..."));
        bagBtn.setOnAction(e -> battleLog.setText("Opening your bag..."));
        pokemonBtn.setOnAction(e -> battleLog.setText("Choose a Pokémon..."));
        forfeitBtn.setOnAction(e -> onGoToQuestionGen());
    }

    @FXML
    private void onGoToQuestionGen() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "/com/therejects/cab302groupproject/QuestionGen-view.fxml"
            ));
            Stage stage = (Stage) forfeitBtn.getScene().getWindow(); // any node from current scene
            stage.setScene(new Scene(root)); // replace entire scene
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
