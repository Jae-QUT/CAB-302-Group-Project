package com.therejects.cab302groupproject.model;

import java.util.Random;
import javafx.stage.PopupWindow;

//import java.util.Scanner; // Might use this later

/**
 * A class that that will generate and display a math question based on the modifiers/variables
 * provided from the "room" chosen (+,-,*,/), the year level, etc. This will generate and
 * display a math question that is suitable for the demographic of the user
 */
public class QuestionGenerator {
    /**
     * Built out constructor to generate the math questions
     *
     */
    private final Random rand;

    private int operand1;
    private int operand2;
    private int answer;
    private int sum;
    private String questionText;
    private String response;    // This will be the system saying correct or incorrect
    // private Operator operator;      // Make an operator method from the
                                    // Which kind of arithmetic will be done


    // --- Constructors ---
    public QuestionGenerator()
    {
        this.rand = new Random();
    }


// --- Methods ---
    /**
     * This method generates an addition question to be displayed on the screen for the students to solve. The Question
     * appears in one of 3 possible forms.
     * Come back to this method later to increase complexity
     *
     * @return The question text to be displayed during battle
     */
    public String generateAdditionQuestion()
    {
        operand1 = rand.nextInt(11);
        operand2 = rand.nextInt(11);
        sum = operand2 + operand1;

        switch(rand.nextInt(1,4))
        {
            case 1 -> {
                questionText = operand1 + " + " + operand2 +" = ?";
                answer = sum;
            }
            case 2 -> {
                questionText = operand1 + " + ? = " + sum;
                answer = operand2;
            }
            case 3 -> {
                questionText = " ? + " + operand2 +" = "+ sum;
                answer = operand1;
            }
            default -> questionText = operand1 + " + " + operand2 +" = ?"; // Just in case
        }
        return questionText;
    }


    public String getQuestionText(){
        return questionText;
    }

    /**
     * This is an accuracy checker for the question. We can use this to determine whether
     * attacks land or miss
     * @param userAnswer is the response from the student which is then used to compare against
     *                   the true answer
     * @return whether or not the user's answer was correct or if they were wrong. If the
     * boolean == true, they are right. if bool == false, they're wrong.
     */
    public boolean checkAnswer(int userAnswer)
    {
        boolean rightOrWrong = (userAnswer == answer);
        response = rightOrWrong ? "Correct!" : "Wrong!";
        return rightOrWrong;
    }

    public String getResponse(){
        return response;
    }
}
