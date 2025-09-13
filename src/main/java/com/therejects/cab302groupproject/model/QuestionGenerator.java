package com.therejects.cab302groupproject.model;

import java.util.Random;
import java.util.Scanner;


// Write out imports needed for this section

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
    private String questionText;
    private String response;    // This will be the system saying correct or incorrect
    private String operator;    // Which kind of arithmetic will be done


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
     * @return the question text to be displayed during battle
     */
    public String generateAdditionQuestion()
    {
        operand1 = rand.nextInt(20);
        operand2 = rand.nextInt(5, 10);
        answer = operand2 + operand1;


        switch(rand.nextInt(1,4))
        {
            case 1 -> questionText = operand1 + " + " + operand2 +" = ?";

            case 2 -> questionText = operand1 + " + ? = " + answer;

            case 3 -> questionText = " ? + " + operand2 +" = "+ answer;

            default -> questionText = operand1 + " + " + operand2 +" = ?"; // Just in case
        }
        return questionText;
    }

    public boolean checkAnswer(int userAnswer)
    {
        boolean rightOrWrong = (userAnswer == answer);
        response = rightOrWrong ? "Correct!" : "Wrong!";
        return rightOrWrong;
    }
}
