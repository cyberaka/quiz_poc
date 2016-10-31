package com.cyberaka.quiz.dto.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QuestionParserTest {

    @Test
    public void parseSingleTextAnswers() {
        QuestionParser parser = new QuestionParser();
        String[] answerArr = parser.parseAnswers("final");
        assertEquals(1, answerArr.length);
        assertEquals("final", answerArr[0]);
    }

    @Test
    public void parseSingleAnswers() {
        QuestionParser parser = new QuestionParser();
        String[] answerArr = parser.parseAnswers("A");
        assertEquals(1, answerArr.length);
        assertEquals("A", answerArr[0]);
    }

    @Test
    public void parseMultipleAnswers() {
        QuestionParser parser = new QuestionParser();
        String[] answerArr = parser.parseAnswers("A,B,C");
        assertEquals(3, answerArr.length);
        assertEquals("A", answerArr[0]);
        assertEquals("B", answerArr[1]);
        assertEquals("C", answerArr[2]);
    }

    @Test
    public void parseNoOptions() {
        QuestionParser parser = new QuestionParser();
        String[] optionsArr = parser.parseOptions("");
        assertEquals(0, optionsArr.length);

        List<String> optionsList = Arrays.asList(optionsArr);
        assertEquals(0, optionsList.size());
    }

    @Test
    public void parseSingleLineOptions() {
        QuestionParser parser = new QuestionParser();
        String[] optionsArr = parser.parseOptions("A. arr[n].length();\n" +
                "B. arr.size;\n" +
                "C. arr.size -1;\n" +
                "D. arr[n][size];\n" +
                "E. arr[n].length;");
        assertEquals(5, optionsArr.length);
        assertEquals("A. arr[n].length();", optionsArr[0]);
        assertEquals("B. arr.size;", optionsArr[1]);
        assertEquals("C. arr.size -1;", optionsArr[2]);
        assertEquals("D. arr[n][size];", optionsArr[3]);
        assertEquals("E. arr[n].length;", optionsArr[4]);
    }

    @Test
    public void parseMultiLineOptions() {
        QuestionParser parser = new QuestionParser();
        String[] optionsArr = parser.parseOptions("A. Test(1)\n" +
                "   Test(2)\n" +
                "   Test(3)\n" +
                "B. Test(3)\n" +
                "   Test(2)\n" +
                "   Test(1)\n" +
                "C. Test(2)\n" +
                "   Test(1) \n" +
                "   Test(3)\n" +
                "D. Test(1)\n" +
                "   Test(3) \n" +
                "   Test(2)\n");
        assertEquals(4, optionsArr.length);
        assertEquals("A. Test(1)\n" +
                "   Test(2)\n" +
                "   Test(3)", optionsArr[0]);
        assertEquals("B. Test(3)\n" +
                "   Test(2)\n" +
                "   Test(1)", optionsArr[1]);
        assertEquals("C. Test(2)\n" +
                "   Test(1) \n" +
                "   Test(3)", optionsArr[2]);
        assertEquals("D. Test(1)\n" +
                "   Test(3) \n" +
                "   Test(2)", optionsArr[3]);
    }
}
