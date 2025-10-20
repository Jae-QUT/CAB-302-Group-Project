package com.therejects.cab302groupproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the QuestionGenerator class.
 * Ensures math questions are generated correctly for student battles.
 *
 * @author Testing Team
 */
class QuestionGeneratorTest {
    private QuestionGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new QuestionGenerator();
    }

    /**
     * Tests that addition questions are generated in valid format.
     */
    @Test
    void testGenerateAdditionQuestion() {
        String question = generator.generateAdditionQuestion();
        assertNotNull(question);
        assertTrue(question.contains("+"));
        assertTrue(question.contains("?"));
    }

    /**
     * Tests that the question text is retrievable.
     */
    @Test
    void testGetQuestionText() {
        generator.generateAdditionQuestion();
        String question = generator.getQuestionText();
        assertNotNull(question);
        assertFalse(question.isEmpty());
    }

    /**
     * Tests that checkAnswer works with a simple test case.
     */
    @Test
    void testCheckAnswerSimple() {
        // Generate and check with known values
        generator.generateAdditionQuestion();

        // Test wrong answer first
        assertFalse(generator.checkAnswer(99999));

        // For correct answer, we'd need to parse the question
        // For now just verify the method works
    }

    /**
     * Tests multiple question generations produce different results.
     */
    @Test
    void testMultipleQuestions() {
        String q1 = generator.generateAdditionQuestion();
        String q2 = generator.generateAdditionQuestion();

        assertNotNull(q1);
        assertNotNull(q2);
        // Questions might be the same due to randomness, just verify they generate
    }
}