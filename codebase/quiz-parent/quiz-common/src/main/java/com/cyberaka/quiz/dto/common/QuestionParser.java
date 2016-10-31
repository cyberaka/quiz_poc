package com.cyberaka.quiz.dto.common;

import java.util.ArrayList;

/**
 * This is a utility class to parse questions, answers and options.<br/>
 * <br>
 * Logic<br>
 * =====<br>
 * Options:<br>
 * Every option starts on a new line and the first three characters<br>
 * follow the format "A. " wherein the first character is a capital letter followed by ". "<br>
 * <p>
 * Answes:<br>
 * The answers are separated by ",".
 *
 * @author anindita
 */
public class QuestionParser {

    /**
     * Parses the provided answer by tokenizing on the ',' character.
     *
     * @param answers An array of answers as detected in the provided answer text.
     * @return
     */
    public String[] parseAnswers(String answers) {
        return answers.split(",");
    }

    /**
     * Parses the provided options by reading each line and executing the logic:<br>
     * Every option starts on a new line and the first three characters<br>
     * follow the format "A. " wherein the first character is a capital letter followed by ". "<br>
     *
     * @param opt An array of options.
     * @return
     */
    public String[] parseOptions(String opt) {
        String options = opt.trim();
        if (options.isEmpty()) {
            return new String[0];
        }
        ArrayList<String> optionList = new ArrayList<>();
        char option = 65; // A
        String optionStr;
        int lastIndex = 0;
        int newIndex;
        do {
            option++;
            optionStr = option + ". ";
            newIndex = options.indexOf(optionStr);
            if (newIndex == -1) {
                optionList.add(options.substring(lastIndex, options.length()).trim());
                break;
            } else {
                optionList.add(options.substring(lastIndex, newIndex).trim());
                lastIndex = newIndex;
            }
        } while (true);
        return optionList.toArray(new String[0]);
    }
}
