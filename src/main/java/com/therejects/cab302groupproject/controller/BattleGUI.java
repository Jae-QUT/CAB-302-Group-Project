package com.therejects.cab302groupproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


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
        forfeitBtn.setOnAction(e -> battleLog.setText("You forfeited the battle!"));
    }
}
