package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.model.QuestionGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class QuestionGenController {

    @FXML private Label promptLabel;
    @FXML private TextField answerField;
    @FXML private Label feedbackLabel;
    @FXML private Button submitButton;
    @FXML private Button nextButton;

    public int userAnswer;
    public boolean submitted;


    private QuestionGenerator generator = new QuestionGenerator();

    public void setQuestionGenerator(QuestionGenerator generator) {
        if (generator != null) this.generator = generator;
    }

    @FXML
    public void initialize() {
        assert promptLabel != null : "promptLabel not injected — check fx:id";
        assert answerField  != null : "answerField not injected — check fx:id";
        submitButton.setDefaultButton(true);
        answerField.setOnAction(e -> onSubmitClick());
        nextQuestion();
    }

//    This was for testing sake and to make sure all the question types would generate
//    @FXML
//    private void onGenerateClick() {nextQuestion(); }

    @FXML
    private void onSubmitClick() {
        String text = answerField.getText().trim();
        if (text.isEmpty()) { feedbackLabel.setText("Please enter a number."); return; }
        try {
            userAnswer = Integer.parseInt(text);
            submitted = true;
            boolean correct = generator.checkAnswer(userAnswer);
            feedbackLabel.setText(correct ? "Correct!" : "Wrong!");

            if (correct)((Stage) answerField.getScene().getWindow()).close();
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("Numbers only!!!");
        }
    }

    private void nextQuestion() {
        String q = generator.generateAdditionQuestion(); // uses YOUR generator
        promptLabel.setText(q);
        answerField.clear();
        feedbackLabel.setText("");
        answerField.requestFocus();
    }
}
