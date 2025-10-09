package com.therejects.cab302groupproject.controller;

import com.example.mon.app.MonDatabase;
import com.example.mon.app.Monster;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class MonsterSelectionController {
    @FXML private FlowPane monsterContainer;
    @FXML private Label selectionCount;
    @FXML private Button confirmButton;

    private final List<Monster> allMons = new ArrayList<>();
    private final List<Monster> selectedMons = new ArrayList<>();

    @FXML
    public void initialize() {
        loadMonstersFromDatabase();
        displayMonsters();
        updateSelectionLabel();
    }

    /** Load monsters from your SQLite database */
    private void loadMonstersFromDatabase() {
        String sql = "SELECT name, spritePath, maxHp FROM Monsters"; // your DB table

        try (Connection conn = MonDatabase.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                String sprite = rs.getString("spritePath");
                int hp = rs.getInt("maxHp");
                allMons.add(new Monster(name, sprite, hp));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Display all monsters as selectable boxes */
    private void displayMonsters() {
        monsterContainer.getChildren().clear();

        for (Monster mon : allMons) {
            VBox box = createMonsterCard(mon);
            monsterContainer.getChildren().add(box);
        }
    }

    private VBox createMonsterCard(Monster mon) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #999; -fx-border-width: 2; -fx-padding: 10; -fx-alignment: center;");
        box.setPrefSize(150, 200);

        ImageView image = new ImageView();
        try {
            image.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(mon.getSpritePath()))));
        } catch (Exception e) {
            image.setImage(null);
        }
        image.setFitWidth(100);
        image.setFitHeight(100);
        image.setPreserveRatio(true);

        Label name = new Label(mon.getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        box.getChildren().addAll(image, name);

        // selection click handler
        box.setOnMouseClicked(e -> toggleSelection(mon, box));

        return box;
    }

    /** Toggle select/deselect monster */
    private void toggleSelection(Monster mon, VBox box) {
        if (selectedMons.contains(mon)) {
            selectedMons.remove(mon);
            box.setStyle("-fx-border-color: #999; -fx-border-width: 2; -fx-padding: 10; -fx-alignment: center;");
        } else {
            if (selectedMons.size() >= 3) {
                return; // can't select more than 3
            }
            selectedMons.add(mon);
            box.setStyle("-fx-border-color: #00cc00; -fx-border-width: 3; -fx-padding: 10; -fx-alignment: center;");
        }

        updateSelectionLabel();
    }

    private void updateSelectionLabel() {
        selectionCount.setText("Selected: " + selectedMons.size() + " / 3");
        confirmButton.setDisable(selectedMons.size() != 3);
    }

    /** Proceed to battle with chosen monsters */
    @FXML
    private void onConfirm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/therejects/cab302groupproject/battle-view.fxml"));
            Scene scene = new Scene(loader.load(), 980, 560);
            BattleGUI controller = loader.getController();

            // pass the chosen mons to the battle
            controller.initializePlayerTeam(selectedMons);

            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
