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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

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

    @Value("${data.google.app_name}")
    String applicationName = "Google Sheets API Java Quickstart";

    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Value("${data.google.tokens_dir}")
    String tokensDirectoryPath;

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

    @Value("${data.google.credentials_json}")
    String credentialsFilePath;

    @Value("${data.google.spreadsheet_id}")
    String spreadsheetId;

    // QUIZ_JPA_DDL_AUTO
    @Value("${spring.jpa.hibernate.ddl-auto}")
    String ddlAuto;

    @Override
    public void run(String... args) throws Exception {
        if (!ddlAuto.equalsIgnoreCase("create")) {
            return;
        }
        googleProcessFile();
//        File file = new File(dataFile);
//        if (file.isDirectory()) {
//            LOG.log(Level.SEVERE, "ATTENTION!! Directory parsing is deprecated. Try switching to excel mode. ");
//            User user = new User();
//            user.setAdmin(true);
//            user.setConsumer(true);
//            user.setPublisher(true);
//            user.setEmail("cyberaka@gmail.com");
//            user.setPassword("1234");
//            user.setPhoneNo("1234");
//            user.setUserName("cyberaka");
//            user.setName("Abhinav Anand");
//            userRepo.save(user);
//            processDirectory(user, file);
//        } else {
//            processFile(file);
//        }
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

        Cell categoryCell, subCategoryCell, sheetNameCell, processCell, versionCell = null;
        String categoryCellStr, subCategoryCellStr, sheetNameCellStr, processCellStr, versionCellStr = null;

        // Verify the header
        if (rowIterator.hasNext()) {
            // Expected Field >> Category	Sub Category	Sheet Name	Process
            Row headerRow = rowIterator.next();
            categoryCell = headerRow.getCell(0);
            categoryCellStr = readStringCellValue(categoryCell);
            subCategoryCell = headerRow.getCell(1);
            subCategoryCellStr = readStringCellValue(subCategoryCell);
            sheetNameCell = headerRow.getCell(2);
            sheetNameCellStr = readStringCellValue(sheetNameCell);
            processCell = headerRow.getCell(3);
            processCellStr = readStringCellValue(processCell);
            versionCell = headerRow.getCell(4);
            versionCellStr = readStringCellValue(versionCell);
            if (checkSubjectSheetHeader(categoryCellStr, subCategoryCellStr, sheetNameCellStr, processCellStr, versionCellStr)) {
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
                    versionCell = row.getCell(4);
                    versionCellStr = readStringCellValue(versionCell);

                    if (isYes(processCellStr) && sheetNameCellStr != null) {
                        LOG.log(Level.INFO, "Processing Subject >> {0} ", categoryCellStr + " - " + subCategoryCellStr);
                        XSSFSheet subjectQuestionAnswerSheet = myWorkBook.getSheet(sheetNameCellStr);
                        if (subjectQuestionAnswerSheet != null) {
                            processQuestionAnswerSheet(user, categoryCellStr, subCategoryCellStr, versionCellStr, subjectQuestionAnswerSheet);
                        } else {
                            LOG.log(Level.SEVERE, "Subject sheet not found >> {0}", sheetNameCellStr);
                        }
                    } else {
                        LOG.log(Level.FINE, "Skipping Subject >> {0}", categoryCellStr);
                    }

                }
            } else {
                LOG.info("Skipping subject import as header doesn't match...");
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

    private boolean checkSubjectSheetHeader(String categoryCellStr, String subCategoryCellStr, String sheetNameCellStr, String processCellStr, String versionCellStr) {
        return categoryCellStr.equalsIgnoreCase("Category") && subCategoryCellStr.equalsIgnoreCase("Sub Category") &&
                sheetNameCellStr.equalsIgnoreCase("Sheet Name") && processCellStr.equalsIgnoreCase("Process") &&
                versionCellStr.equalsIgnoreCase("Version");
    }

    /**
     * Version 1 of header.
     *
     */
    private boolean checkQuestionBankSheetHeader(String questionCellStr, String optionACellStr, String optionBCellStr, String optionCCellStr,
                                                 String optionDCellStr, String optionECellStr, String answerCellStr) {
        return questionCellStr.equalsIgnoreCase("Question") && optionACellStr.equalsIgnoreCase("Option A") &&
                optionBCellStr.equalsIgnoreCase("Option B") && optionCCellStr.equalsIgnoreCase("Option C") &&
                optionDCellStr.equalsIgnoreCase("Option D") && optionECellStr.equalsIgnoreCase("Option E") &&
                answerCellStr.equalsIgnoreCase("Answer");
    }

    /**
     * Version 2 of header.
     *
     */
    private boolean checkQuestionBankSheetHeader(String questionCellStr, String optionACellStr, String optionBCellStr, String optionCCellStr,
                                                 String optionDCellStr, String optionECellStr, String optionFCellStr, String answerCellStr,
                                                 String explanationCellStr, String chapterCellStr, String pageCellStr, String bookCellStr) {
        return questionCellStr.equalsIgnoreCase("Question") && optionACellStr.equalsIgnoreCase("Option A") &&
                optionBCellStr.equalsIgnoreCase("Option B") && optionCCellStr.equalsIgnoreCase("Option C") &&
                optionDCellStr.equalsIgnoreCase("Option D") && optionECellStr.equalsIgnoreCase("Option E") &&
                optionFCellStr.equalsIgnoreCase("Option F") && answerCellStr.equalsIgnoreCase("Answer") &&
                explanationCellStr.equalsIgnoreCase("Explanation") && chapterCellStr.equalsIgnoreCase("Chapter") &&
                pageCellStr.equalsIgnoreCase("Page") && bookCellStr.equalsIgnoreCase("Book");
    }

    /**
     * Invoked for every sheet which has to be processed as per the control set in the subject sheet.
     *
     * @param user
     * @param categoryCellStr
     * @param subCategoryCellStr
     * @param questionAnswerSheet
     */
    private void processQuestionAnswerSheet(User user, String categoryCellStr, String subCategoryCellStr, String versionCellStr, XSSFSheet questionAnswerSheet) {
        Topic topic = null;
        List<Topic> topicList = topicRepo.findByTitle(categoryCellStr);
        if (topicList.isEmpty()) {
            topic = new Topic();
            topic.setTitle(categoryCellStr);
            topicRepo.save(topic);
        } else {
            if (topicList.size() == 1) {
                topic = topicList.get(0);
            } else {
                throw new IllegalArgumentException("Unable to find unique topic. Multiple topics found. >> " + topicList.size() + " >> " + topicList);
            }
        }

        SubTopic subTopic = new SubTopic();
        subTopic.setTitle(subCategoryCellStr);
        subTopic.setTopic(topic);
        subtopicRepo.save(subTopic);

        Iterator<Row> rowIterator = questionAnswerSheet.iterator();

        if (versionCellStr.equalsIgnoreCase("1")) {
            extractV1Format(user, topic, subTopic, rowIterator);
        } else {
            extractV2Format(user, topic, subTopic, rowIterator);
        }
    }

    private void extractV1Format(User user, Topic topic, SubTopic subTopic, Iterator<Row> rowIterator) {
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

    private void extractV2Format(User user, Topic topic, SubTopic subTopic, Iterator<Row> rowIterator) {
        // Question	Option A	Option B	Option C	Option D	Option E	Option F	Answer	Explanation	Chapter	Page	Book
        Cell questionCell, optionACell, optionBCell, optionCCell, optionDCell, optionECell, optionFCell, answerCell, explanationCell, chapterCell, pageCell, bookCell = null;
        String questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, optionFCellStr, answerCellStr, explanationCellStr, chapterCellStr, pageCellStr, bookCellStr = null;
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
            optionFCell = headerRow.getCell(6);
            optionFCellStr = readStringCellValue(optionFCell);
            answerCell = headerRow.getCell(7);
            answerCellStr = readStringCellValue(answerCell);
            explanationCell = headerRow.getCell(8);
            explanationCellStr = readStringCellValue(explanationCell);
            chapterCell = headerRow.getCell(9);
            chapterCellStr = readStringCellValue(chapterCell);
            pageCell = headerRow.getCell(10);
            pageCellStr = readStringCellValue(pageCell);
            bookCell = headerRow.getCell(11);
            bookCellStr = readStringCellValue(bookCell);
            if (checkQuestionBankSheetHeader(questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, optionFCellStr,
                    answerCellStr, explanationCellStr, chapterCellStr, pageCellStr, bookCellStr)) {
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
                    optionFCell = row.getCell(6);
                    optionFCellStr = readStringCellValue(optionFCell);
                    answerCell = row.getCell(7);
                    answerCellStr = readStringCellValue(answerCell);
                    explanationCell = row.getCell(8);
                    explanationCellStr = readStringCellValue(explanationCell);
                    chapterCell = row.getCell(9);
                    chapterCellStr = readStringCellValue(chapterCell);
                    pageCell = row.getCell(10);
                    pageCellStr = readStringCellValue(pageCell);
                    bookCell = row.getCell(11);
                    bookCellStr = readStringCellValue(bookCell);

                    if (!chapterCellStr.equalsIgnoreCase(subTopic.getTitle())) {
                        continue;
                    }
                    optionIndex = 0;
                    optionBuilder.setLength(0);
                    optionBuilder.append(optionACellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionACellStr + "\n"));
                    optionBuilder.append(optionBCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionBCellStr + "\n"));
                    optionBuilder.append(optionCCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionCCellStr + "\n"));
                    optionBuilder.append(optionDCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionDCellStr + "\n"));
                    optionBuilder.append(optionECellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionECellStr + "\n"));
                    optionBuilder.append(optionFCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionFCellStr + "\n"));

                    Question quest = new Question();
                    quest.setTopic(topic);
                    quest.setSubTopic(subTopic);
                    quest.setContributer(user);
                    quest.setQuestion(questionCellStr);
                    quest.setOptions(optionBuilder.toString());
                    quest.setAnswers(answerCellStr);
                    quest.setDifficultyLevel(QuizAppConstants.DIFFICULTY_MEDIUM);
                    quest.setExplanation(explanationCellStr);
                    quest.setChapter(chapterCellStr);
                    quest.setPage(pageCellStr);
                    quest.setBook(bookCellStr);
                    if (quest.isValidV2()) {
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
            parsedQuestion[0] = sectionNo + ""; //(new Integer(sectionNo)).toString();

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

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport httpTransport)
            throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(credentialsFilePath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialsFilePath);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private void googleProcessFile() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service =
                new Sheets.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
                        .setApplicationName(applicationName)
                        .build();
        String range = "Users!A1:I";
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();

        // Persist the user information
        User user = googlePersistUserInformation(values.iterator());
        if (user == null) {
            LOG.log(Level.SEVERE, "No admin user found. Terminating bootup process.");
            return;
        }

//        // Return subject sheet from the XLSX workbook
//        XSSFSheet subjectSheet = myWorkBook.getSheet("Subjects");

//        // Get iterator to all the rows in current sheet
//        Iterator<Row> rowIterator = subjectSheet.iterator();

        range = "Subjects!A1:E";
        response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        values = response.getValues();
        Iterator<List<Object>> rowIterator = values.iterator();

        Cell categoryCell, subCategoryCell, sheetNameCell, processCell, versionCell = null;
        String categoryCellStr, subCategoryCellStr, sheetNameCellStr, processCellStr, versionCellStr = null;

        // Verify the header
        if (rowIterator.hasNext()) {
            // Expected Field >> Category	Sub Category	Sheet Name	Process
            List<Object> headerRow = rowIterator.next();
//            categoryCell = headerRow.getCell(0);
            categoryCellStr = googleReadStringCellValue(headerRow, 0);
//            subCategoryCell = headerRow.getCell(1);
            subCategoryCellStr = googleReadStringCellValue(headerRow, 1);
//            sheetNameCell = headerRow.getCell(2);
            sheetNameCellStr = googleReadStringCellValue(headerRow, 2);
//            processCell = headerRow.getCell(3);
            processCellStr = googleReadStringCellValue(headerRow, 3);
//            versionCell = headerRow.getCell(4);
            versionCellStr = googleReadStringCellValue(headerRow, 4);
            if (checkSubjectSheetHeader(categoryCellStr, subCategoryCellStr, sheetNameCellStr, processCellStr, versionCellStr)) {
                while (rowIterator.hasNext()) {
                    List<Object> row = rowIterator.next();
//                    categoryCell = row.getCell(0);
                    categoryCellStr = googleReadStringCellValue(row, 0);
//                    subCategoryCell = row.getCell(1);
                    subCategoryCellStr = googleReadStringCellValue(row, 1);
//                    sheetNameCell = row.getCell(2);
                    sheetNameCellStr = googleReadStringCellValue(row, 2);
//                    processCell = row.getCell(3);
                    processCellStr = googleReadStringCellValue(row, 3);
//                    versionCell = row.getCell(4);
                    versionCellStr = googleReadStringCellValue(row, 4);

                    if (isYes(processCellStr) && sheetNameCellStr != null) {
                        LOG.log(Level.INFO, "Processing Subject >> {0} ", categoryCellStr + " - " + subCategoryCellStr);
                        range = sheetNameCellStr + "!A1:L";
                        try {
                            response = service.spreadsheets().values()
                                    .get(spreadsheetId, range)
                                    .execute();
                            googleProcessQuestionAnswerSheet(user, categoryCellStr, subCategoryCellStr, versionCellStr, response);
                        } catch (GoogleJsonResponseException ex) {
                            LOG.log(Level.SEVERE, "Subject sheet not found >> {0}", sheetNameCellStr);
                        }
                    } else {
                        LOG.log(Level.INFO, "Skipping Subject >> {0}", categoryCellStr);
                    }

                }
            } else {
                LOG.info("Skipping subject import as header doesn't match...");
            }
        }
    }

    /**
     * Persist the user information found in the 'Users' sheet into the database.
     *
     * @param rowIterator The users iterator.
     * @return The admin user if any.
     */
    private User googlePersistUserInformation(Iterator<List<Object>> rowIterator) {
        User adminUser = null;
        User user = null;
        // Get iterator to all the rows in current sheet
//        Iterator<Row> rowIterator = users.iterator();

        Cell userIdCell, passwordCell, nameCell, emailCell, phoneCell, adminCell, publisherCell, consumerCell, processCell = null;
        String userIdCellStr, passwordCellStr, nameCellStr, emailCellStr, phoneCellStr, adminCellStr, publisherCellStr, consumerCellStr, processCellStr = null;

        // Verify the header
        if (rowIterator.hasNext()) {
            List<Object> row = rowIterator.next();
//            userIdCell = row.getCell(0);
            userIdCellStr = googleReadStringCellValue(row, 0);
//            passwordCell = row.getCell(1);
            passwordCellStr = googleReadStringCellValue(row, 1);
//            nameCell = row.getCell(2);
            nameCellStr = googleReadStringCellValue(row, 2);
//            emailCell = row.getCell(3);
            emailCellStr = googleReadStringCellValue(row, 3);
//            phoneCell = row.getCell(4);
            phoneCellStr = googleReadStringCellValue(row, 4);
//            adminCell = row.getCell(5);
            adminCellStr = googleReadStringCellValue(row, 5);
//            publisherCell = row.getCell(6);
            publisherCellStr = googleReadStringCellValue(row, 6);
//            consumerCell = row.getCell(7);
            consumerCellStr = googleReadStringCellValue(row, 7);
//            processCell = row.getCell(8);
            processCellStr = googleReadStringCellValue(row, 8);
            if (checkUserSheetHeader(userIdCellStr, passwordCellStr, nameCellStr, emailCellStr, phoneCellStr, adminCellStr, publisherCellStr, consumerCellStr, processCellStr)) {
                while (rowIterator.hasNext()) {
                    row = rowIterator.next();
//                    userIdCell = row.getCell(0);
                    userIdCellStr = googleReadStringCellValue(row, 0);
//                    passwordCell = row.getCell(1);
                    passwordCellStr = googleReadStringCellValue(row, 1);
//                    nameCell = row.getCell(2);
                    nameCellStr = googleReadStringCellValue(row, 2);
//                    emailCell = row.getCell(3);
                    emailCellStr = googleReadStringCellValue(row, 3);
//                    phoneCell = row.getCell(4);
                    phoneCellStr = googleReadStringCellValue(row, 4);
//                    adminCell = row.getCell(5);
                    adminCellStr = googleReadStringCellValue(row, 5);
//                    publisherCell = row.getCell(6);
                    publisherCellStr = googleReadStringCellValue(row, 6);
//                    consumerCell = row.getCell(7);
                    consumerCellStr = googleReadStringCellValue(row, 7);
//                    processCell = row.getCell(8);
                    processCellStr = googleReadStringCellValue(row, 8);

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
                        LOG.log(Level.FINE, "Skipping User >> {0}", userIdCellStr);
                    }
                }
            }
        }
        return adminUser;
    }

    private boolean googleCheckUserSheetHeader(String userIdCellStr, String passwordCellStr, String nameCellStr, String emailCellStr,
                                         String phoneCellStr, String adminCellStr, String publisherCellStr, String consumerCellStr, String processCellStr) {
        return userIdCellStr.equalsIgnoreCase("User ID") && passwordCellStr.equalsIgnoreCase("Password") && nameCellStr.equalsIgnoreCase("Name") &&
                emailCellStr.equalsIgnoreCase("E-Mail") && phoneCellStr.equalsIgnoreCase("Phone") && adminCellStr.equalsIgnoreCase("Admin") &&
                publisherCellStr.equalsIgnoreCase("Publisher") && consumerCellStr.equalsIgnoreCase("Consumer") &&
                processCellStr.equalsIgnoreCase("Process");
    }

    private String googleReadStringCellValue(List<Object> row, int index) {
//        if (cell == null) {
//            return "";
//        }
//        cell.setCellType(Cell.CELL_TYPE_STRING);
//        return cell.getStringCellValue();
        return "" + row.get(index);
    }

    private boolean googleCheckSubjectSheetHeader(String categoryCellStr, String subCategoryCellStr, String sheetNameCellStr, String processCellStr, String versionCellStr) {
        return categoryCellStr.equalsIgnoreCase("Category") && subCategoryCellStr.equalsIgnoreCase("Sub Category") &&
                sheetNameCellStr.equalsIgnoreCase("Sheet Name") && processCellStr.equalsIgnoreCase("Process") &&
                versionCellStr.equalsIgnoreCase("Version");
    }

    /**
     * Version 1 of header.
     *
     */
    private boolean googleCheckQuestionBankSheetHeader(String questionCellStr, String optionACellStr, String optionBCellStr, String optionCCellStr,
                                                 String optionDCellStr, String optionECellStr, String answerCellStr) {
        return questionCellStr.equalsIgnoreCase("Question") && optionACellStr.equalsIgnoreCase("Option A") &&
                optionBCellStr.equalsIgnoreCase("Option B") && optionCCellStr.equalsIgnoreCase("Option C") &&
                optionDCellStr.equalsIgnoreCase("Option D") && optionECellStr.equalsIgnoreCase("Option E") &&
                answerCellStr.equalsIgnoreCase("Answer");
    }

    /**
     * Version 2 of header.
     *
     */
    private boolean googleCheckQuestionBankSheetHeader(String questionCellStr, String optionACellStr, String optionBCellStr, String optionCCellStr,
                                                 String optionDCellStr, String optionECellStr, String optionFCellStr, String answerCellStr,
                                                 String explanationCellStr, String chapterCellStr, String pageCellStr, String bookCellStr) {
        return questionCellStr.equalsIgnoreCase("Question") && optionACellStr.equalsIgnoreCase("Option A") &&
                optionBCellStr.equalsIgnoreCase("Option B") && optionCCellStr.equalsIgnoreCase("Option C") &&
                optionDCellStr.equalsIgnoreCase("Option D") && optionECellStr.equalsIgnoreCase("Option E") &&
                optionFCellStr.equalsIgnoreCase("Option F") && answerCellStr.equalsIgnoreCase("Answer") &&
                explanationCellStr.equalsIgnoreCase("Explanation") && chapterCellStr.equalsIgnoreCase("Chapter") &&
                pageCellStr.equalsIgnoreCase("Page") && bookCellStr.equalsIgnoreCase("Book");
    }

    /**
     * Invoked for every sheet which has to be processed as per the control set in the subject sheet.
     *
     * @param user
     * @param categoryCellStr
     * @param subCategoryCellStr
     * @param questionAnswerSheet
     */
    private void googleProcessQuestionAnswerSheet(User user, String categoryCellStr, String subCategoryCellStr, String versionCellStr, ValueRange questionAnswerSheet) {
        Topic topic = null;
        List<Topic> topicList = topicRepo.findByTitle(categoryCellStr);
        if (topicList.isEmpty()) {
            topic = new Topic();
            topic.setTitle(categoryCellStr);
            topicRepo.save(topic);
        } else {
            if (topicList.size() == 1) {
                topic = topicList.get(0);
            } else {
                throw new IllegalArgumentException("Unable to find unique topic. Multiple topics found. >> " + topicList.size() + " >> " + topicList);
            }
        }

        SubTopic subTopic = new SubTopic();
        subTopic.setTitle(subCategoryCellStr);
        subTopic.setTopic(topic);
        subtopicRepo.save(subTopic);

        List<List<Object>> values = questionAnswerSheet.getValues();
        if (values == null || values.isEmpty()) {
            System.err.println("No data found for header.");
            throw new IllegalArgumentException("The google sheet header is invalid.");
        } else {
            if (versionCellStr.equalsIgnoreCase("1")) {
                googleExtractV1Format(user, topic, subTopic, values.iterator());
            } else {
                googleExtractV2Format(user, topic, subTopic, values.iterator());
            }
        }
    }

    private void googleExtractV1Format(User user, Topic topic, SubTopic subTopic, Iterator<List<Object>> rowIterator) {
        Cell questionCell, optionACell, optionBCell, optionCCell, optionDCell, optionECell, answerCell = null;
        String questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, answerCellStr = null;
        StringBuilder optionBuilder = new StringBuilder();
        char optionIndex = 0;
        // Verify the header
        if (rowIterator.hasNext()) {
            List<Object> headerRow = rowIterator.next();
//            questionCell = headerRow.getCell(0);
            questionCellStr = googleReadStringCellValue(headerRow, 0);
//            optionACell = headerRow.getCell(1);
            optionACellStr = googleReadStringCellValue(headerRow, 1);
//            optionBCell = headerRow.getCell(2);
            optionBCellStr = googleReadStringCellValue(headerRow, 2);
//            optionCCell = headerRow.getCell(3);
            optionCCellStr = googleReadStringCellValue(headerRow, 3);
//            optionDCell = headerRow.getCell(4);
            optionDCellStr = googleReadStringCellValue(headerRow, 4);
//            optionECell = headerRow.getCell(5);
            optionECellStr = googleReadStringCellValue(headerRow, 5);
//            answerCell = headerRow.getCell(6);
            answerCellStr = googleReadStringCellValue(headerRow, 6);
            if (checkQuestionBankSheetHeader(questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, answerCellStr)) {
                while (rowIterator.hasNext()) {
                    List<Object> row = rowIterator.next();
//                    questionCell = row.getCell(0);
                    questionCellStr = googleReadStringCellValue(headerRow, 0);
//                    optionACell = row.getCell(1);
                    optionACellStr = googleReadStringCellValue(headerRow, 1);
//                    optionBCell = row.getCell(2);
                    optionBCellStr = googleReadStringCellValue(headerRow, 2);
//                    optionCCell = row.getCell(3);
                    optionCCellStr = googleReadStringCellValue(headerRow, 3);
//                    optionDCell = row.getCell(4);
                    optionDCellStr = googleReadStringCellValue(headerRow, 4);
//                    optionECell = row.getCell(5);
                    optionECellStr = googleReadStringCellValue(headerRow, 5);
//                    answerCell = row.getCell(6);
                    answerCellStr = googleReadStringCellValue(headerRow, 6);

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

    private void googleExtractV2Format(User user, Topic topic, SubTopic subTopic, Iterator<List<Object>> rowIterator) {
        // Question	Option A	Option B	Option C	Option D	Option E	Option F	Answer	Explanation	Chapter	Page	Book
        Cell questionCell, optionACell, optionBCell, optionCCell, optionDCell, optionECell, optionFCell, answerCell, explanationCell, chapterCell, pageCell, bookCell = null;
        String questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, optionFCellStr, answerCellStr, explanationCellStr, chapterCellStr, pageCellStr, bookCellStr = null;
        StringBuilder optionBuilder = new StringBuilder();
        char optionIndex = 0;
        // Verify the header
        if (rowIterator.hasNext()) {
            List<Object> headerRow = rowIterator.next();
//            questionCell = headerRow.getCell(0);
            questionCellStr = googleReadStringCellValue(headerRow, 0);
//            optionACell = headerRow.getCell(1);
            optionACellStr = googleReadStringCellValue(headerRow, 1);
//            optionBCell = headerRow.getCell(2);
            optionBCellStr = googleReadStringCellValue(headerRow, 2);
//            optionCCell = headerRow.getCell(3);
            optionCCellStr = googleReadStringCellValue(headerRow, 3);
//            optionDCell = headerRow.getCell(4);
            optionDCellStr = googleReadStringCellValue(headerRow, 4);
//            optionECell = headerRow.getCell(5);
            optionECellStr = googleReadStringCellValue(headerRow, 5);
//            optionFCell = headerRow.getCell(6);
            optionFCellStr = googleReadStringCellValue(headerRow, 6);
//            answerCell = headerRow.getCell(7);
            answerCellStr = googleReadStringCellValue(headerRow, 7);
//            explanationCell = headerRow.getCell(8);
            explanationCellStr = googleReadStringCellValue(headerRow, 8);
//            chapterCell = headerRow.getCell(9);
            chapterCellStr = googleReadStringCellValue(headerRow, 9);
//            pageCell = headerRow.getCell(10);
            pageCellStr = googleReadStringCellValue(headerRow, 10);
//            bookCell = headerRow.getCell(11);
            bookCellStr = googleReadStringCellValue(headerRow, 11);
            if (checkQuestionBankSheetHeader(questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, optionFCellStr,
                    answerCellStr, explanationCellStr, chapterCellStr, pageCellStr, bookCellStr)) {
                while (rowIterator.hasNext()) {
                    List<Object> row = rowIterator.next();
//                    questionCell = row.getCell(0);
                    questionCellStr = googleReadStringCellValue(row, 0);
//                    optionACell = row.getCell(1);
                    optionACellStr = googleReadStringCellValue(row, 1);
//                    optionBCell = row.getCell(2);
                    optionBCellStr = googleReadStringCellValue(row, 2);
//                    optionCCell = row.getCell(3);
                    optionCCellStr = googleReadStringCellValue(row, 3);
//                    optionDCell = row.getCell(4);
                    optionDCellStr = googleReadStringCellValue(row, 4);
//                    optionECell = row.getCell(5);
                    optionECellStr = googleReadStringCellValue(row, 5);
//                    optionFCell = row.getCell(6);
                    optionFCellStr = googleReadStringCellValue(row, 6);
//                    answerCell = row.getCell(7);
                    answerCellStr = googleReadStringCellValue(row, 7);
//                    explanationCell = row.getCell(8);
                    explanationCellStr = googleReadStringCellValue(row, 8);
//                    chapterCell = row.getCell(9);
                    chapterCellStr = googleReadStringCellValue(row, 9);
//                    pageCell = row.getCell(10);
                    pageCellStr = googleReadStringCellValue(row, 10);
//                    bookCell = row.getCell(11);
                    bookCellStr = googleReadStringCellValue(row, 11);

                    if (!chapterCellStr.equalsIgnoreCase(subTopic.getTitle())) {
                        continue;
                    }
                    optionIndex = 0;
                    optionBuilder.setLength(0);
                    optionBuilder.append(optionACellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionACellStr + "\n"));
                    optionBuilder.append(optionBCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionBCellStr + "\n"));
                    optionBuilder.append(optionCCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionCCellStr + "\n"));
                    optionBuilder.append(optionDCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionDCellStr + "\n"));
                    optionBuilder.append(optionECellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionECellStr + "\n"));
                    optionBuilder.append(optionFCellStr.isEmpty() ? "" : ((char) ('A' + optionIndex++) + ". " + optionFCellStr + "\n"));

                    Question quest = new Question();
                    quest.setTopic(topic);
                    quest.setSubTopic(subTopic);
                    quest.setContributer(user);
                    quest.setQuestion(questionCellStr);
                    quest.setOptions(optionBuilder.toString());
                    quest.setAnswers(answerCellStr);
                    quest.setDifficultyLevel(QuizAppConstants.DIFFICULTY_MEDIUM);
                    quest.setExplanation(explanationCellStr);
                    quest.setChapter(chapterCellStr);
                    quest.setPage(pageCellStr);
                    quest.setBook(bookCellStr);
                    if (quest.isValidV2()) {
                        questionRepo.save(quest);
                    } else {
                        LOG.log(Level.SEVERE, "Invalid question skipped >> {0}", quest.getQuestion());
                    }
                }
            }
        }
    }

}
