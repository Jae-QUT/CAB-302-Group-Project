package com.therejects.cab302groupproject.controller;

import com.therejects.cab302groupproject.model.QuestionGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Methods to ensure that the question generator is called under the correct reasons
 */
public class QuestionGenController {

    @FXML private Label promptLabel;
    @FXML private TextField answerField;
    @FXML private Label feedbackLabel;
    @FXML private Button submitButton;
    @FXML private Button nextButton;

    public int userAnswer;
    public boolean submitted;

    /**
     * Creates an instance of the {@link QuestionGenerator} so the same answers can be connected to the questions being asked
     */
    public QuestionGenerator generator = new QuestionGenerator();

    /**
     * Assigns the question generator to a variable so we can reference it elsewhere
     * @param generator The current instance of the question generator
     */
    public void setQuestionGenerator(QuestionGenerator generator) {
        if (generator != null) this.generator = generator;
    }

    /**
     * Logic to ensure the generator popup displays as intended. Also used to test that the questions would change consistently
     */
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

            ((Stage) answerField.getScene().getWindow()).close();
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
