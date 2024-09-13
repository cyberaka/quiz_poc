package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.PdfGenaratorUtil;
import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.domain.User;
import com.cyberaka.quiz.dto.*;
import com.cyberaka.quiz.dto.common.exception.QuizSecurityException;
import com.cyberaka.quiz.service.QuestionService;
import com.cyberaka.quiz.service.UserService;
import com.cyberaka.quiz.utils.CommonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin(origins = "*")
public class QuestionController {

    private Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    PdfGenaratorUtil pdfGenaratorUtil;

    @Value("${data.result_output}")
    String dataResultOutputFolder;

    @Value("${data.font_file}")
    String dataFontFile;

    @RequestMapping("/quiz/{topicId}/{subTopicId}/{level}/{count}")
    @ResponseBody
    public List<QuestionDto> getQuiz(@PathVariable("topicId") String topicId, @PathVariable("subTopicId") String subTopicId,
                                     @PathVariable("level") int level, @PathVariable("count") int count) {
        log.info("getQuiz(" + topicId + ", " + subTopicId + ", " + level + ", " + count + ")");

        List<QuestionDto> results = StreamSupport.stream(
                        questionService.findQuiz(topicId, subTopicId, level, count)
                                .spliterator(), false)
                .map(this::convertToDto)
                .collect(Collectors.toList());

        if (CommonHelper.getInstance().isAuthenticated()) {
            return results;
        } else {
            return CommonHelper.getInstance().getTwentyPercentOfResults(results);
        }
    }

    @RequestMapping(value="/quiz/publish", method=RequestMethod.POST)
    @ResponseBody
    public void submitQuizAnswer(Model model, @RequestBody QuestionAnswerDto[] body) throws QuizSecurityException {
        if (true) { // Need to deprecate this functionality.
            return;
        }
        int userId = 1; // TODO Replace with principal nickname
        System.out.println("Received answer submitted by user " + userId);
        System.out.println(body);
        User user = userService.find(userId);
        if (user == null) {
            throw new QuizSecurityException("No valid user id provided!");
        }
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("name",user.getName());

        //Get current date time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy h-mm-a");
        String formatDate = now.format(dateFormatter);
        String formatTime = now.format(timeFormatter);
        String fileFormatterStr = now.format(fileFormatter);
        data.put("date_of_examination", formatDate);
        data.put("time_of_examination", formatTime);

        ArrayList<CandidateResultDto> resultList = new ArrayList<>();
        int sl = 0;
        int score = 0;
        for (QuestionAnswerDto dto: body) {
            CandidateResultDto templateDto = new CandidateResultDto();
            templateDto.setQuestionNumber(++sl);
            templateDto.setQuestion(dto.getQuestion());
            templateDto.setAnswer(dto.getAnswers().stream().map(Object::toString).collect(Collectors.joining(",")));
            templateDto.setCandidateAnswer(dto.getUserAnswers().stream().map(Object::toString).collect(Collectors.joining(",")));

            boolean correct = false;
            if (dto.getAnswers().size() == 1 && (dto.getOptions() == null || dto.getOptions().size() == 0)) {
                // User entry.
                if (dto.getAnswers().get(0).equalsIgnoreCase(dto.getUserAnswers().get(0))) {
                    correct = true;
                }
            } else {
                if (dto.getAnswers().size() == 1) {
                    // Single choice.
                    if (dto.getAnswers().get(0).equalsIgnoreCase(dto.getUserAnswers().get(0))) {
                        correct = true;
                    }
                } else {
                    // Multiple choice.
                    int correctAnswerCount = 0;
                    for (int j = 0; j < dto.getAnswers().size(); j++) {
                        String questionAnswer = dto.getAnswers().get(j);

                        for (int i = 0; i < dto.getUserAnswers().size(); i++) {
                            String userAnswer = dto.getUserAnswers().get(i);
                            if (questionAnswer.equalsIgnoreCase(userAnswer)) {
                                correctAnswerCount++;
                            }
                        }
                    }
                    if (correctAnswerCount == dto.getAnswers().size()) {
                        correct = true;
                    }
                }
            }
            if (correct) {
                templateDto.setCorrect("Yes");
                score++;
            } else {
                templateDto.setCorrect("No");
            }
            resultList.add(templateDto);
        }
        data.put("candidate_score", score + " out of " + body.length);
        data.put("answertable", resultList);

        String fileName = dataResultOutputFolder + File.separator + user.getName() + " " + fileFormatterStr + ".pdf";
        try {
            pdfGenaratorUtil.createPdf("result", dataFontFile, fileName, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QuestionDto convertToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.clone(question);
        return dto;
    }
}
