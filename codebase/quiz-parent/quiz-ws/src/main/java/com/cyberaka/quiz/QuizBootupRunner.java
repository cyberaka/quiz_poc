package com.cyberaka.quiz;

import com.cyberaka.quiz.common.QuizAppConstants;
import com.cyberaka.quiz.dao.QuestionRepository;
import com.cyberaka.quiz.dao.SubTopicRepository;
import com.cyberaka.quiz.dao.TopicRepository;
import com.cyberaka.quiz.dao.UserRepository;
import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.domain.Topic;
import com.cyberaka.quiz.domain.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        File file = new File(dataFile);
        if (file.isDirectory()) {
            LOG.log(Level.SEVERE, "ATTENTION!! Directory parsing is deprecated. Try switching to excel mode. ");
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
            processDirectory(user, file);
        } else {
            processFile(file);
        }
    }

    private void processFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

        // Persist the user information
        User user = persistUserInformation(myWorkBook.getSheet("Users"));
        if (user == null) {
            LOG.log(Level.SEVERE, "No admin user found. Terminating bootup process.");
            return;
        }

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

                    if (isYes(processCellStr) && sheetNameCellStr != null) {
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

    private boolean isYes(String msg) {
        return msg.equalsIgnoreCase("yes");
    }

    /**
     * Persist the user information found in the 'Users' sheet into the database.
     *
     * @param users The users sheet.
     * @return The admin user if any.
     */
    private User persistUserInformation(XSSFSheet users) {
        User adminUser = null;
        User user = null;
        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = users.iterator();

        Cell userIdCell, passwordCell, nameCell, emailCell, phoneCell, adminCell, publisherCell, consumerCell, processCell = null;
        String userIdCellStr, passwordCellStr, nameCellStr, emailCellStr, phoneCellStr, adminCellStr, publisherCellStr, consumerCellStr, processCellStr = null;

        // Verify the header
        if (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            userIdCell = row.getCell(0);
            userIdCellStr = readStringCellValue(userIdCell);
            passwordCell = row.getCell(1);
            passwordCellStr = readStringCellValue(passwordCell);
            nameCell = row.getCell(2);
            nameCellStr = readStringCellValue(nameCell);
            emailCell = row.getCell(3);
            emailCellStr = readStringCellValue(emailCell);
            phoneCell = row.getCell(4);
            phoneCellStr = readStringCellValue(phoneCell);
            adminCell = row.getCell(5);
            adminCellStr = readStringCellValue(adminCell);
            publisherCell = row.getCell(6);
            publisherCellStr = readStringCellValue(publisherCell);
            consumerCell = row.getCell(7);
            consumerCellStr = readStringCellValue(consumerCell);
            processCell = row.getCell(8);
            processCellStr = readStringCellValue(processCell);
            if (checkUserSheetHeader(userIdCellStr, passwordCellStr, nameCellStr, emailCellStr, phoneCellStr, adminCellStr, publisherCellStr, consumerCellStr, processCellStr)) {
                while (rowIterator.hasNext()) {
                    row = rowIterator.next();
                    userIdCell = row.getCell(0);
                    userIdCellStr = readStringCellValue(userIdCell);
                    passwordCell = row.getCell(1);
                    passwordCellStr = readStringCellValue(passwordCell);
                    nameCell = row.getCell(2);
                    nameCellStr = readStringCellValue(nameCell);
                    emailCell = row.getCell(3);
                    emailCellStr = readStringCellValue(emailCell);
                    phoneCell = row.getCell(4);
                    phoneCellStr = readStringCellValue(phoneCell);
                    adminCell = row.getCell(5);
                    adminCellStr = readStringCellValue(adminCell);
                    publisherCell = row.getCell(6);
                    publisherCellStr = readStringCellValue(publisherCell);
                    consumerCell = row.getCell(7);
                    consumerCellStr = readStringCellValue(consumerCell);
                    processCell = row.getCell(8);
                    processCellStr = readStringCellValue(processCell);

                    if (userIdCellStr != null && passwordCellStr != null && nameCellStr != null
                            && emailCellStr != null && phoneCellStr != null && adminCellStr != null && publisherCellStr != null
                            && consumerCellStr != null && processCellStr != null && isYes(processCellStr)) {
                        LOG.log(Level.INFO, "Processing User >> {0}", userIdCellStr);
                        user = new User();
                        user.setUserName(userIdCellStr);
                        user.setPassword(passwordCellStr);
                        user.setName(nameCellStr);
                        user.setEmail(emailCellStr);
                        user.setPhoneNo(phoneCellStr);
                        user.setAdmin(isYes(adminCellStr));
                        user.setPublisher(isYes(publisherCellStr));
                        user.setConsumer(isYes(consumerCellStr));
                        userRepo.save(user);
                        if (user.isAdmin()) {
                            adminUser = user;
                        }
                    } else {
                        LOG.log(Level.FINE, "Skipping User >> {0}", userIdCell);
                    }
                }
            }
        }
        return adminUser;
    }

    private boolean checkUserSheetHeader(String userIdCellStr, String passwordCellStr, String nameCellStr, String emailCellStr,
                                         String phoneCellStr, String adminCellStr, String publisherCellStr, String consumerCellStr, String processCellStr) {
        return userIdCellStr.equalsIgnoreCase("User ID") && passwordCellStr.equalsIgnoreCase("Password") && nameCellStr.equalsIgnoreCase("Name") &&
                emailCellStr.equalsIgnoreCase("E-Mail") && phoneCellStr.equalsIgnoreCase("Phone") && adminCellStr.equalsIgnoreCase("Admin") &&
                publisherCellStr.equalsIgnoreCase("Publisher") && consumerCellStr.equalsIgnoreCase("Consumer") &&
                processCellStr.equalsIgnoreCase("Process");
    }


    private void processDirectory(User user, File file) {
        Topic topic = new Topic();
        topic.setTitle("Java");
        topicRepo.save(topic);

        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
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
                    optionBuilder.append(optionACellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionACellStr + "\n"));
                    optionBuilder.append(optionBCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionBCellStr + "\n"));
                    optionBuilder.append(optionCCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionCCellStr + "\n"));
                    optionBuilder.append(optionDCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionDCellStr + "\n"));
                    optionBuilder.append(optionECellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionECellStr + "\n"));

                    Question quest = new Question();
                    quest.setTopic(topic);
                    quest.setSubTopic(subTopic);
                    quest.setContributer(user);
                    quest.setQuestion(questionCellStr);
                    quest.setOptions(optionBuilder.toString());
                    quest.setAnswers(answerCellStr);
                    quest.setDifficultyLevel(QuizAppConstants.DIFFICULTY_MEDIUM);
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

            for (int i = 0; i < list.size(); i++) {
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
                quest.setDifficultyLevel(QuizAppConstants.DIFFICULTY_MEDIUM);
                questionRepo.save(quest);
            }
//            dumpInsertStatements(list);
        }

        private void dumpInsertStatements(ArrayList<String[]> list) {
            for (int i = 0; i < list.size(); i++) {
                String[] line = list.get(i);
                System.out.println(String.format("Question:\n%s\n Options:\n%s\n Answers:\n%s\n=========\n",
                        line[1], line[3], line[4]));
            }
        }

        private ArrayList<String[]> parseQuestions(String[] lines) {
            ArrayList<String[]> questions = new ArrayList<>();
            StringBuilder questionLines = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
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
