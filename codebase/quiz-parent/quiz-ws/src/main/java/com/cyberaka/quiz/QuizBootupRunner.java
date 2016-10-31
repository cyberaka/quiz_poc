package com.cyberaka.quiz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.cyberaka.quiz.dao.QuestionRepository;
import com.cyberaka.quiz.dao.SubTopicRepository;
import com.cyberaka.quiz.dao.TopicRepository;
import com.cyberaka.quiz.dao.UserRepository;
import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.domain.Topic;
import com.cyberaka.quiz.domain.User;
import com.cyberaka.quiz.service.AppConstants;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Component
public class QuizBootupRunner implements CommandLineRunner {
    
    static final Logger LOG = Logger.getLogger(QuizBootupRunner.class.getName());

    @Autowired
    QuestionRepository questionRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    TopicRepository topicRepo;

    @Autowired
    SubTopicRepository subtopicRepo;

    @Value("${data.file}")
    String dataFile;

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setAdmin(true);
        user.setConsumer(true);
        user.setPublisher(true);
        user.setEmail("cyberaka@gmail.com");
        user.setPassword("1234");
        user.setPhoneNo("1234");
        user.setUserName("cyberaka");
        user.setName("Abhinav Anand");
        userRepo.save(user);

                File file = new File(dataFile);
                if (file.isDirectory()) {
                    processDirectory(user, file);
                } else {
                    processFile(user, file);
                }
        }
        
        private void processFile(User user, File file) throws IOException {
            FileInputStream fis = new FileInputStream(file);

            // Finds the workbook instance for XLSX file
            XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
           
            // Return subject sheet from the XLSX workbook
            XSSFSheet subjectSheet = myWorkBook.getSheet("Subjects");
           
            // Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = subjectSheet.iterator();
           
            Cell categoryCell, subCategoryCell, sheetNameCell, processCell = null;
            String categoryCellStr, subCategoryCellStr, sheetNameCellStr, processCellStr = null;
                
            // Verify the header
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                categoryCell = headerRow.getCell(0);
                categoryCellStr = readStringCellValue(categoryCell);
                subCategoryCell = headerRow.getCell(1);
                subCategoryCellStr = readStringCellValue(subCategoryCell);
                sheetNameCell = headerRow.getCell(2);
                sheetNameCellStr = readStringCellValue(sheetNameCell);
                processCell = headerRow.getCell(3);
                processCellStr = readStringCellValue(processCell);
                if (checkSubjectSheetHeader(categoryCellStr, subCategoryCellStr, sheetNameCellStr, processCellStr)) {
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        categoryCell = row.getCell(0);
                        categoryCellStr = readStringCellValue(categoryCell);
                        subCategoryCell = row.getCell(1);
                        subCategoryCellStr = readStringCellValue(subCategoryCell);
                        sheetNameCell = row.getCell(2);
                        sheetNameCellStr = readStringCellValue(sheetNameCell);
                        processCell = row.getCell(3);
                        processCellStr = readStringCellValue(processCell);
                        
                        if (processCellStr.equalsIgnoreCase("yes") && sheetNameCellStr != null) {
                            LOG.log(Level.INFO, "Processing Subject >> {0}", categoryCellStr);
                            XSSFSheet subjectQuestionAnswerSheet = myWorkBook.getSheet(sheetNameCellStr);
                            if (subjectQuestionAnswerSheet != null) { 
                                processQuestionAnswerSheet(user, categoryCellStr, subCategoryCellStr, subjectQuestionAnswerSheet);
                            } else {
                                LOG.log(Level.SEVERE, "Subject sheet not found >> {0}", sheetNameCellStr);
                            }
                        } else {
                            LOG.log(Level.FINE, "Skipping Subject >> {0}", categoryCellStr);
                        }
                        
                    }
                }
            }
            myWorkBook.close();
            fis.close();
        }
        
        private void processDirectory(User user, File file) {
        Topic topic = new Topic();
        topic.setTitle("Java");
        topicRepo.save(topic);

        File[] childFiles = file.listFiles();
        for (File childFile: childFiles) {
            if (childFile.isFile() && childFile.canRead() && childFile.getName().endsWith(".txt")) {
                System.out.println("Processing File: " + childFile.getAbsolutePath());

                SubTopic subTopic = new SubTopic();
                subTopic.setTitle(childFile.getName().substring(0, childFile.getName().length() - 4));
                subTopic.setTopic(topic);
                subtopicRepo.save(subTopic);

                TextToSqlConverter converter = new TextToSqlConverter(childFile.getAbsolutePath());
                try {
                    converter.bootup(topic, subTopic, user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String readStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        return cell.getStringCellValue();
    }

    private boolean checkSubjectSheetHeader(String categoryCellStr, String subCategoryCellStr, String sheetNameCellStr, String processCellStr) {
        return categoryCellStr.equalsIgnoreCase("Category") && subCategoryCellStr.equalsIgnoreCase("Sub Category") &&
                sheetNameCellStr.equalsIgnoreCase("Sheet Name") && processCellStr.equalsIgnoreCase("Process");
    }
    
    private boolean checkQuestionBankSheetHeader(String questionCellStr, String optionACellStr, String optionBCellStr, String optionCCellStr,
            String optionDCellStr, String optionECellStr, String answerCellStr) {
        return questionCellStr.equalsIgnoreCase("Question") && optionACellStr.equalsIgnoreCase("Option A") &&
                optionBCellStr.equalsIgnoreCase("Option B") && optionCCellStr.equalsIgnoreCase("Option C") &&
                optionDCellStr.equalsIgnoreCase("Option D") && optionECellStr.equalsIgnoreCase("Option E") && 
                answerCellStr.equalsIgnoreCase("Answer");
    }

    private void processQuestionAnswerSheet(User user, String categoryCellStr, String subCategoryCellStr, XSSFSheet questionAnswerSheet) {
        Topic topic = new Topic();
        topic.setTitle(categoryCellStr);
        topicRepo.save(topic);

        SubTopic subTopic = new SubTopic();
        subTopic.setTitle(subCategoryCellStr);
        subTopic.setTopic(topic);
        subtopicRepo.save(subTopic);

            Iterator<Row> rowIterator = questionAnswerSheet.iterator();
           
            Cell questionCell, optionACell, optionBCell, optionCCell, optionDCell, optionECell, answerCell = null;
            String questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, answerCellStr = null;
            StringBuilder optionBuilder = new StringBuilder();
            char optionIndex = 0;
                
            // Verify the header
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                questionCell = headerRow.getCell(0);
                questionCellStr = readStringCellValue(questionCell);
                optionACell = headerRow.getCell(1);
                optionACellStr = readStringCellValue(optionACell);
                optionBCell = headerRow.getCell(2);
                optionBCellStr = readStringCellValue(optionBCell);
                optionCCell = headerRow.getCell(3);
                optionCCellStr = readStringCellValue(optionCCell);
                optionDCell = headerRow.getCell(4);
                optionDCellStr = readStringCellValue(optionDCell);
                optionECell = headerRow.getCell(5);
                optionECellStr = readStringCellValue(optionECell);
                answerCell = headerRow.getCell(6);
                answerCellStr = readStringCellValue(answerCell);
                if (checkQuestionBankSheetHeader(questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, answerCellStr)) {
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        questionCell = row.getCell(0);
                        questionCellStr = readStringCellValue(questionCell);
                        optionACell = row.getCell(1);
                        optionACellStr = readStringCellValue(optionACell);
                        optionBCell = row.getCell(2);
                        optionBCellStr = readStringCellValue(optionBCell);
                        optionCCell = row.getCell(3);
                        optionCCellStr = readStringCellValue(optionCCell);
                        optionDCell = row.getCell(4);
                        optionDCellStr = readStringCellValue(optionDCell);
                        optionECell = row.getCell(5);
                        optionECellStr = readStringCellValue(optionECell);
                        answerCell = row.getCell(6);
                        answerCellStr = readStringCellValue(answerCell);
                        
                        optionIndex = 0;
                        optionBuilder.setLength(0);
                        optionBuilder.append(optionACellStr.isEmpty()? "": ((char) ('A' + optionIndex++) + ". " + optionACellStr + "\n"));
                        optionBuilder.append(optionBCellStr.isEmpty()? "": ((char) ('A' + optionIndex++) + ". " + optionBCellStr + "\n"));
                        optionBuilder.append(optionCCellStr.isEmpty()? "": ((char) ('A' + optionIndex++) + ". " + optionCCellStr + "\n"));
                        optionBuilder.append(optionDCellStr.isEmpty()? "": ((char) ('A' + optionIndex++) + ". " + optionDCellStr + "\n"));
                        optionBuilder.append(optionECellStr.isEmpty()? "": ((char) ('A' + optionIndex++) + ". " + optionECellStr + "\n"));
                        
                        Question quest = new Question();
                        quest.setTopic(topic);
                        quest.setSubTopic(subTopic);
                        quest.setContributer(user);
                        quest.setQuestion(questionCellStr);
                        quest.setOptions(optionBuilder.toString());
                        quest.setAnswers(answerCellStr);
                        quest.setDifficultyLevel(AppConstants.DIFFICULTY_MEDIUM);
                        if (quest.isValid()) {
                            questionRepo.save(quest);
                        } else {
                            LOG.log(Level.SEVERE, "Invalid question skipped >> {0}", quest.getQuestion());
                        }
                    }
                }
            }
    }

    class TextToSqlConverter {

        private String fileName;

        public TextToSqlConverter(String fileName) {
            this.fileName = fileName;
        }

        public void bootup(Topic topic, SubTopic subTopic, User user) throws IOException {
            ArrayList<String> lines = readFile();
            ArrayList<String[]> list = parseQuestions(lines.toArray(new String[0]));

            for (int i=0; i<list.size(); i++) {
                String[] line = list.get(i);
//                System.out.println(String.format("Question:\n%s\n Options:\n%s\n Answers:\n%s\n=========\n",
//                        line[1], line[3], line[4]));

                Question quest = new Question();
                quest.setTopic(topic);
                quest.setSubTopic(subTopic);
                quest.setContributer(user);
                quest.setQuestion(line[1]);
                quest.setOptions(line[3]);
                quest.setAnswers(line[4]);
                quest.setDifficultyLevel(AppConstants.DIFFICULTY_MEDIUM);
                questionRepo.save(quest);
            }
//            dumpInsertStatements(list);
        }

        private void dumpInsertStatements(ArrayList<String[]> list) {
            for (int i=0; i<list.size(); i++) {
                String[] line = list.get(i);
                System.out.println(String.format("Question:\n%s\n Options:\n%s\n Answers:\n%s\n=========\n",
                        line[1], line[3], line[4]));
            }
        }

        private ArrayList<String[]> parseQuestions(String[] lines) {
            ArrayList<String[]> questions = new ArrayList<>();
            StringBuilder questionLines = new StringBuilder();
            for (int i=0; i<lines.length; i++) {
                if (lines[i].startsWith("Q. ")) {
                    if (questionLines.length() != 0) {
                        questions.add(parseQuestion(0, questionLines.toString()));
                        questionLines.setLength(0);
                    }
                }
                questionLines.append(lines[i] + "\n");
            }
            if (questionLines.length() != 0) {
                questions.add(parseQuestion(0, questionLines.toString()));
            }
            return questions;
        }

        // This function reads the complete mixed up question/exhibit/answer
        // text and assigns them into the currentExamQuestionList array
        private String[] parseQuestion(int sectionNo, String mixedUp) {
            String[] parsedQuestion = new String[8];
            boolean isExhibit = false;
            int exhibitPos = 0;
            boolean isOptions = false;
            int optionsPos = 0;
            boolean isAnswer = false;
            int answerPos = 0;
            String tempQuestion = "";
            String tempExhibit = "";
            String tempOptions = "";
            String tempAnswer = "";
            parsedQuestion[0] = "";
            parsedQuestion[1] = "";
            parsedQuestion[2] = "";
            parsedQuestion[3] = "";
            parsedQuestion[4] = "";
            parsedQuestion[5] = "";
            parsedQuestion[6] = "";
            parsedQuestion[7] = "";

            exhibitPos = mixedUp.indexOf("<EXHIBIT>");
            if (exhibitPos != -1)
                isExhibit = true;

            optionsPos = mixedUp.indexOf("A. ");
            if (optionsPos != -1)
                isOptions = true;

            answerPos = mixedUp.indexOf("ANSWER:");
            if (answerPos != -1)
                isAnswer = true;

            // Set the section no.
            parsedQuestion[0] = (new Integer(sectionNo)).toString();

            // Now we will extract the question
            if (isExhibit) {
                tempQuestion = mixedUp.substring(0, exhibitPos - 1);
            } else if (isOptions) {
                tempQuestion = mixedUp.substring(0, optionsPos - 1);
            } else if (isAnswer) {
                tempQuestion = mixedUp.substring(0, answerPos - 1);
            }
            // Set the question
            parsedQuestion[1] = tempQuestion.substring(3,
                    tempQuestion.length()).trim();

            // Now we will extract the exhibit
            if (isExhibit) {
                if (isOptions) {
                    tempExhibit = mixedUp.substring(exhibitPos, optionsPos - 1);
                } else if (isAnswer) {
                    tempExhibit = mixedUp.substring(exhibitPos, answerPos - 1);
                }
            }
            // Set the exhibit
            parsedQuestion[2] = tempExhibit.trim();

            // Now we will extract the options
            if (isOptions) {
                if (isAnswer) {
                    tempOptions = mixedUp.substring(optionsPos, answerPos - 1);
                }
            }
            // Set the options
            parsedQuestion[3] = tempOptions.trim();

            // Now we will extract the answer
            if (isAnswer) {
                tempAnswer = mixedUp.substring(answerPos, mixedUp.length());
            }
            // Set the options
            parsedQuestion[4] = tempAnswer.substring(7,
                    tempAnswer.length()).trim();

            parsedQuestion[5] = "";
            parsedQuestion[6] = "";

            // parsedQuestion
            // section no.
            // Question
            // Exhibit
            // Options
            // Answer key
            // Status - Answer given list (a,b,c,d / a / 0x6)
            // Marked - Marked
            return parsedQuestion;
        }

        private ArrayList<String> readFile() throws IOException {
            ArrayList<String> lines = new ArrayList<String>();
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                   lines.add(line);
                }
            }
            return lines;
        }

    }
}
