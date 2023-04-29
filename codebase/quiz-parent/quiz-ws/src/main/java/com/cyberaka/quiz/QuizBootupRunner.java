package com.cyberaka.quiz;

import com.cyberaka.quiz.common.QuizAppConstants;
import com.cyberaka.quiz.dao.QuestionRepository;
import com.cyberaka.quiz.dao.SubTopicRepository;
import com.cyberaka.quiz.dao.TopicRepository;
import com.cyberaka.quiz.dao.UserRepository;
import com.cyberaka.quiz.domain.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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
    List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    @Value("${data.google.credentials_json}")
    String credentialsFilePath;

    @Value("${data.google.spreadsheet_id}")
    String spreadsheetId;

    // QUIZ_JPA_DDL_AUTO
    @Value("${data.ddl_auto}")
    String ddlAuto;

    @Override
    public void run(String... args) throws Exception {
        if (!ddlAuto.equalsIgnoreCase("create")) {
            return;
        }
        googleProcessFile();
    }

    private boolean isYes(String msg) {
        return msg.equalsIgnoreCase("yes");
    }

    private boolean checkUserSheetHeader(String userIdCellStr, String passwordCellStr, String nameCellStr, String emailCellStr,
                                         String phoneCellStr, String adminCellStr, String publisherCellStr, String consumerCellStr, String processCellStr) {
        return userIdCellStr.equalsIgnoreCase("User ID") && passwordCellStr.equalsIgnoreCase("Password") && nameCellStr.equalsIgnoreCase("Name") &&
                emailCellStr.equalsIgnoreCase("E-Mail") && phoneCellStr.equalsIgnoreCase("Phone") && adminCellStr.equalsIgnoreCase("Admin") &&
                publisherCellStr.equalsIgnoreCase("Publisher") && consumerCellStr.equalsIgnoreCase("Consumer") &&
                processCellStr.equalsIgnoreCase("Process");
    }

    private boolean checkSubjectImportSheetHeader(String subjectCellStr, String targetSheetStr, String workSheetIdStr, String sheetIdCellStr, String processCellStr) {
        return subjectCellStr.equalsIgnoreCase("Subject") &&
                targetSheetStr.equalsIgnoreCase("Target Sheet") &&
                workSheetIdStr.equalsIgnoreCase("Source Worksheet") &&
                sheetIdCellStr.equalsIgnoreCase("Source Sheet") &&
                processCellStr.equalsIgnoreCase("Process");
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
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

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

        range = "Subject Import!A1:E";
        response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        values = response.getValues();

        // Persist the user information
        ArrayList<SubjectImport> subjectImportList = googleSubjectImport(values.iterator());
        googleImportSubjects(subjectImportList, service, response);

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
            categoryCellStr = googleReadStringCellValue(headerRow, 0);
            subCategoryCellStr = googleReadStringCellValue(headerRow, 1);
            sheetNameCellStr = googleReadStringCellValue(headerRow, 2);
            processCellStr = googleReadStringCellValue(headerRow, 3);
            versionCellStr = googleReadStringCellValue(headerRow, 4);
            if (checkSubjectSheetHeader(categoryCellStr, subCategoryCellStr, sheetNameCellStr, processCellStr, versionCellStr)) {
                while (rowIterator.hasNext()) {
                    List<Object> row = rowIterator.next();
                    categoryCellStr = googleReadStringCellValue(row, 0);
                    subCategoryCellStr = googleReadStringCellValue(row, 1);
                    sheetNameCellStr = googleReadStringCellValue(row, 2);
                    processCellStr = googleReadStringCellValue(row, 3);
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
        LOG.info("Done with Google Process File.");
    }

    private void googleImportSubjects(ArrayList<SubjectImport> subjectImportList, Sheets service, ValueRange masterResponse) throws IOException {
        // Find out all the sheets in the target sheet and build a map.
        LOG.info("Loading sheets from target worksheet..");
        Spreadsheet targetSpreadSheet = service.spreadsheets().get(spreadsheetId).execute();
        List<Sheet> targetSheetList = targetSpreadSheet.getSheets();
        HashMap<String, Sheet> targetSheetMap = new HashMap<>();
        Iterator<Sheet> targetSheetItr = targetSheetList.iterator();
        while (targetSheetItr.hasNext()) {
            Sheet sheet = targetSheetItr.next();
            targetSheetMap.put(sheet.getProperties().getTitle(), sheet);
            LOG.info("Found >> " + sheet.getProperties().getTitle());
        }

        Iterator<SubjectImport> subjectImportItr = subjectImportList.iterator();
        while (subjectImportItr.hasNext()) {
            SubjectImport subjectImport = subjectImportItr.next();
            LOG.info("Subject Import >> " + subjectImport);

            // Find the target sheet.
            LOG.info("Loading Target Sheet");
            Sheet targetSheet = targetSheetMap.get(subjectImport.getTargetSheet());
            if (targetSheet == null) {
                LOG.info("Skipping subjectImport import. Invalid subjectImport target name >> " + subjectImport);
                continue;
            }
            int totalTargetRows = targetSheet.getProperties().getGridProperties().getRowCount();
            LOG.info("Loading target sheet values");
            String targetRange = subjectImport.getTargetSheet() + "!A1:L";
            ValueRange targetResponse = service.spreadsheets().values()
                    .get(spreadsheetId, targetRange)
                    .execute();
            List<List<Object>> targetResponseValues = targetResponse.getValues();
            totalTargetRows = targetResponseValues.size();

            // Load Data from the source sheet.
            LOG.info("Loading source sheet values");
            String sourceRange = subjectImport.getSourceSheet() + "!A1:M";
            ValueRange sourceResponse = service.spreadsheets().values()
                    .get(subjectImport.getSourceWorkSheet(), sourceRange)
                    .execute();
            List<List<Object>> sourceResponseValues = sourceResponse.getValues();
            Iterator<List<Object>> sourceRowIterator = sourceResponseValues.iterator();
            while (sourceRowIterator.hasNext()) {
                List<Object> row = sourceRowIterator.next();
                row.remove(0); // Remove question number field.
            }

            // Delete the rows from the target sheet.
            LOG.info("Deleting rows from the target sheet.");
            if (totalTargetRows > 1) {
                int sheetId = targetSheet.getProperties().getSheetId();
                LOG.info("Sheet ID >> " + sheetId);
                BatchUpdateSpreadsheetRequest content = new BatchUpdateSpreadsheetRequest();
                Request request = new Request()
                        .setDeleteDimension(new DeleteDimensionRequest()
                                .setRange(new DimensionRange()
                                        .setSheetId(targetSheet.getProperties().getSheetId())
                                        .setDimension("ROWS")
                                        .setStartIndex(0)
                                        .setEndIndex(totalTargetRows)
                                )
                        );
                List<Request> requests = new ArrayList<Request>();
                requests.add(request);
                content.setRequests(requests);
                service.spreadsheets().batchUpdate(spreadsheetId, content).execute();
            }

            // Copy the content from source sheet to target sheet.
            LOG.info("Copying values from source sheet to target sheet.");
            targetRange = subjectImport.getTargetSheet() + "!A1:L";
            ValueRange requestBody = new ValueRange().setValues(sourceResponseValues);
            Sheets.Spreadsheets.Values.Append appendRequest = service.spreadsheets().values().append(spreadsheetId, targetRange, requestBody);
            appendRequest.setValueInputOption("USER_ENTERED");
            appendRequest.setInsertDataOption("INSERT_ROWS");

            AppendValuesResponse appendResult = appendRequest.execute();
            appendResult.getUpdates().getUpdatedData();

            // Freeze 1st row
            GridProperties targetSheetGridProperties = targetSheet.getProperties().getGridProperties();
            targetSheetGridProperties.setFrozenRowCount(1);
            UpdateSheetPropertiesRequest updateSheetPropertiesRequest = new UpdateSheetPropertiesRequest().setFields("gridProperties.frozenRowCount")
                    .setProperties(new SheetProperties().setSheetId(targetSheet.getProperties().getSheetId())
                            .setGridProperties(new GridProperties().setFrozenRowCount(1)));
            List<Request> requests = List.of(new Request().setUpdateSheetProperties(updateSheetPropertiesRequest));
            BatchUpdateSpreadsheetRequest apiRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            service.spreadsheets().batchUpdate(spreadsheetId, apiRequest ).execute();
        }
    }

    private void googleImportQuestions(User user, Topic topic, SubTopic subTopic, Iterator<List<Object>> rowIterator) {
        String questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, optionFCellStr, answerCellStr, explanationCellStr, chapterCellStr, pageCellStr, bookCellStr = null;
        ArrayList<Question> questionList = new ArrayList<>();
        // Verify the header
        if (rowIterator.hasNext()) {
            List<Object> headerRow = rowIterator.next();
            questionCellStr = googleReadStringCellValue(headerRow, 1);
            optionACellStr = googleReadStringCellValue(headerRow, 2);
            optionBCellStr = googleReadStringCellValue(headerRow, 3);
            optionCCellStr = googleReadStringCellValue(headerRow, 4);
            optionDCellStr = googleReadStringCellValue(headerRow, 5);
            optionECellStr = googleReadStringCellValue(headerRow, 6);
            optionFCellStr = googleReadStringCellValue(headerRow, 7);
            answerCellStr = googleReadStringCellValue(headerRow, 8);
            explanationCellStr = googleReadStringCellValue(headerRow, 9);
            chapterCellStr = googleReadStringCellValue(headerRow, 10);
            pageCellStr = googleReadStringCellValue(headerRow, 11);
            bookCellStr = googleReadStringCellValue(headerRow, 12);
            if (checkQuestionBankSheetHeader(questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, optionFCellStr,
                    answerCellStr, explanationCellStr, chapterCellStr, pageCellStr, bookCellStr)) {
                while (rowIterator.hasNext()) {
                    List<Object> row = rowIterator.next();
                    questionCellStr = googleReadStringCellValue(row, 1);
                    optionACellStr = googleReadStringCellValue(row, 2);
                    optionBCellStr = googleReadStringCellValue(row, 3);
                    optionCCellStr = googleReadStringCellValue(row, 4);
                    optionDCellStr = googleReadStringCellValue(row, 5);
                    optionECellStr = googleReadStringCellValue(row, 6);
                    optionFCellStr = googleReadStringCellValue(row, 7);
                    answerCellStr = googleReadStringCellValue(row, 8);
                    explanationCellStr = googleReadStringCellValue(row, 9);
                    chapterCellStr = googleReadStringCellValue(row, 10);
                    pageCellStr = googleReadStringCellValue(row, 11);
                    bookCellStr = googleReadStringCellValue(row, 12);

                    if (!chapterCellStr.equalsIgnoreCase(subTopic.getTitle())) {
                        continue;
                    }
                    List<QuestionOption> questionOptionList = new ArrayList<>();
                    if (!optionACellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("A", optionACellStr));
                    }
                    if (!optionBCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("B", optionBCellStr));
                    }
                    if (!optionCCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("C", optionCCellStr));
                    }
                    if (!optionDCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("D", optionDCellStr));
                    }
                    if (!optionECellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("E", optionECellStr));
                    }
                    if (!optionFCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("F", optionFCellStr));
                    }

                    Question quest = new Question();
                    quest.setTopic(topic);
                    quest.setSubTopic(subTopic);
                    quest.setContributer(user);
                    quest.setQuestion(questionCellStr);
                    quest.setOptions(questionOptionList.toArray(new QuestionOption[0]));
                    quest.setAnswers(answerCellStr);
                    quest.setDifficultyLevel(QuizAppConstants.DIFFICULTY_MEDIUM);
                    quest.setExplanation(explanationCellStr);
                    quest.setChapter(chapterCellStr);
                    quest.setPage(pageCellStr);
                    quest.setBook(bookCellStr);
                    if (quest.isValidV2()) {
                        questionList.add(quest);
                    } else {
                        LOG.log(Level.SEVERE, "Invalid question skipped >> {0}", quest.getQuestion());
                    }
                }
            }
        }
    }

    private ArrayList<SubjectImport> googleSubjectImport(Iterator<List<Object>> rowIterator) {
        String subjectStr, targetSheetStr, workSheetIdStr, sheetIdStr, processCellStr = null;
        ArrayList<SubjectImport> subjectImportList = new ArrayList<>();
        // Verify the header
        if (rowIterator.hasNext()) {
            List<Object> row = rowIterator.next();
            subjectStr = googleReadStringCellValue(row, 0);
            targetSheetStr = googleReadStringCellValue(row, 1);
            workSheetIdStr = googleReadStringCellValue(row, 2);
            sheetIdStr = googleReadStringCellValue(row, 3);
            processCellStr = googleReadStringCellValue(row, 4);
            if (checkSubjectImportSheetHeader(subjectStr, targetSheetStr, workSheetIdStr, sheetIdStr, processCellStr)) {
                while (rowIterator.hasNext()) {
                    row = rowIterator.next();
                    subjectStr = googleReadStringCellValue(row, 0);
                    targetSheetStr = googleReadStringCellValue(row, 1);
                    workSheetIdStr = googleReadStringCellValue(row, 2);
                    sheetIdStr = googleReadStringCellValue(row, 3);
                    processCellStr = googleReadStringCellValue(row, 4);

                    if (subjectStr != null && targetSheetStr != null && workSheetIdStr != null && sheetIdStr != null && processCellStr != null && isYes(processCellStr)) {
                        LOG.log(Level.INFO, "Processing Subject Import >> {0}", subjectStr);
                        SubjectImport subjectImport = new SubjectImport();
                        subjectImport.setSubject(subjectStr);
                        subjectImport.setTargetSheet(targetSheetStr);
                        subjectImport.setSourceWorkSheet(workSheetIdStr);
                        subjectImport.setSourceSheet(sheetIdStr);
                        subjectImportList.add(subjectImport);
                    } else {
                        LOG.log(Level.INFO, "Skipping Subject Import >> {0}", subjectStr);
                    }
                }
            }
        }
        return subjectImportList;
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

        Cell userIdCell, passwordCell, nameCell, emailCell, phoneCell, adminCell, publisherCell, consumerCell, processCell = null;
        String userIdCellStr, passwordCellStr, nameCellStr, emailCellStr, phoneCellStr, adminCellStr, publisherCellStr, consumerCellStr, processCellStr = null;

        // Verify the header
        if (rowIterator.hasNext()) {
            List<Object> row = rowIterator.next();
            userIdCellStr = googleReadStringCellValue(row, 0);
            passwordCellStr = googleReadStringCellValue(row, 1);
            nameCellStr = googleReadStringCellValue(row, 2);
            emailCellStr = googleReadStringCellValue(row, 3);
            phoneCellStr = googleReadStringCellValue(row, 4);
            adminCellStr = googleReadStringCellValue(row, 5);
            publisherCellStr = googleReadStringCellValue(row, 6);
            consumerCellStr = googleReadStringCellValue(row, 7);
            processCellStr = googleReadStringCellValue(row, 8);
            if (checkUserSheetHeader(userIdCellStr, passwordCellStr, nameCellStr, emailCellStr, phoneCellStr, adminCellStr, publisherCellStr, consumerCellStr, processCellStr)) {
                while (rowIterator.hasNext()) {
                    row = rowIterator.next();
                    userIdCellStr = googleReadStringCellValue(row, 0);
                    passwordCellStr = googleReadStringCellValue(row, 1);
                    nameCellStr = googleReadStringCellValue(row, 2);
                    emailCellStr = googleReadStringCellValue(row, 3);
                    phoneCellStr = googleReadStringCellValue(row, 4);
                    adminCellStr = googleReadStringCellValue(row, 5);
                    publisherCellStr = googleReadStringCellValue(row, 6);
                    consumerCellStr = googleReadStringCellValue(row, 7);
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
            LOG.log(Level.SEVERE, "No data found for header.");
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
        // Verify the header
        if (rowIterator.hasNext()) {
            List<Object> headerRow = rowIterator.next();
            questionCellStr = googleReadStringCellValue(headerRow, 0);
            optionACellStr = googleReadStringCellValue(headerRow, 1);
            optionBCellStr = googleReadStringCellValue(headerRow, 2);
            optionCCellStr = googleReadStringCellValue(headerRow, 3);
            optionDCellStr = googleReadStringCellValue(headerRow, 4);
            optionECellStr = googleReadStringCellValue(headerRow, 5);
            answerCellStr = googleReadStringCellValue(headerRow, 6);
            if (checkQuestionBankSheetHeader(questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, answerCellStr)) {
                while (rowIterator.hasNext()) {
                    List<Object> row = rowIterator.next();
                    questionCellStr = googleReadStringCellValue(headerRow, 0);
                    optionACellStr = googleReadStringCellValue(headerRow, 1);
                    optionBCellStr = googleReadStringCellValue(headerRow, 2);
                    optionCCellStr = googleReadStringCellValue(headerRow, 3);
                    optionDCellStr = googleReadStringCellValue(headerRow, 4);
                    optionECellStr = googleReadStringCellValue(headerRow, 5);
                    answerCellStr = googleReadStringCellValue(headerRow, 6);

                    List<QuestionOption> questionOptionList = new ArrayList<>();
                    if (!optionACellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("A", optionACellStr));
                    }
                    if (!optionBCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("B", optionBCellStr));
                    }
                    if (!optionCCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("C", optionCCellStr));
                    }
                    if (!optionDCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("D", optionDCellStr));
                    }
                    if (!optionECellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("E", optionECellStr));
                    }

                    Question quest = new Question();
                    quest.setTopic(topic);
                    quest.setSubTopic(subTopic);
                    quest.setContributer(user);
                    quest.setQuestion(questionCellStr);
                    quest.setOptions(questionOptionList.toArray(new QuestionOption[0]));
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
//String md5Hex = DigestUtils.md5DigestAsHex(message.getBytes()).toUpperCase();
    private void googleExtractV2Format(User user, Topic topic, SubTopic subTopic, Iterator<List<Object>> rowIterator) {
        // Question	Option A	Option B	Option C	Option D	Option E	Option F	Answer	Explanation	Chapter	Page	Book
        Cell questionCell, optionACell, optionBCell, optionCCell, optionDCell, optionECell, optionFCell, answerCell, explanationCell, chapterCell, pageCell, bookCell = null;
        String questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, optionFCellStr, answerCellStr, explanationCellStr, chapterCellStr, pageCellStr, bookCellStr = null;

        int questionsAdded = 0, questionsUpdated = 0, questionsSkipped = 0;

        // Verify the header
        if (rowIterator.hasNext()) {
            List<Object> headerRow = rowIterator.next();
            questionCellStr = googleReadStringCellValue(headerRow, 0);
            optionACellStr = googleReadStringCellValue(headerRow, 1);
            optionBCellStr = googleReadStringCellValue(headerRow, 2);
            optionCCellStr = googleReadStringCellValue(headerRow, 3);
            optionDCellStr = googleReadStringCellValue(headerRow, 4);
            optionECellStr = googleReadStringCellValue(headerRow, 5);
            optionFCellStr = googleReadStringCellValue(headerRow, 6);
            answerCellStr = googleReadStringCellValue(headerRow, 7);
            explanationCellStr = googleReadStringCellValue(headerRow, 8);
            chapterCellStr = googleReadStringCellValue(headerRow, 9);
            pageCellStr = googleReadStringCellValue(headerRow, 10);
            bookCellStr = googleReadStringCellValue(headerRow, 11);
            if (checkQuestionBankSheetHeader(questionCellStr, optionACellStr, optionBCellStr, optionCCellStr, optionDCellStr, optionECellStr, optionFCellStr,
                    answerCellStr, explanationCellStr, chapterCellStr, pageCellStr, bookCellStr)) {

                // Load the questions in a map.
                List<Question> existingQuestionList = questionRepo.queryByTopicAndSubTopic(topic.getTitle(), subTopic.getTitle());
                HashMap<String, Question> existingQuestionMap = new HashMap<>();
                for (Question existingQuestion: existingQuestionList) {
                    existingQuestionMap.put(existingQuestion.toString(), existingQuestion);
                }

                while (rowIterator.hasNext()) {
                    List<Object> row = rowIterator.next();
                    questionCellStr = googleReadStringCellValue(row, 0);
                    optionACellStr = googleReadStringCellValue(row, 1);
                    optionBCellStr = googleReadStringCellValue(row, 2);
                    optionCCellStr = googleReadStringCellValue(row, 3);
                    optionDCellStr = googleReadStringCellValue(row, 4);
                    optionECellStr = googleReadStringCellValue(row, 5);
                    optionFCellStr = googleReadStringCellValue(row, 6);
                    answerCellStr = googleReadStringCellValue(row, 7);
                    explanationCellStr = googleReadStringCellValue(row, 8);
                    chapterCellStr = googleReadStringCellValue(row, 9);
                    pageCellStr = googleReadStringCellValue(row, 10);
                    bookCellStr = googleReadStringCellValue(row, 11);

                    if (!chapterCellStr.equalsIgnoreCase(subTopic.getTitle())) {
                        continue;
                    }
                    List<QuestionOption> questionOptionList = new ArrayList<>();
                    if (!optionACellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("A", optionACellStr));
                    }
                    if (!optionBCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("B", optionBCellStr));
                    }
                    if (!optionCCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("C", optionCCellStr));
                    }
                    if (!optionDCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("D", optionDCellStr));
                    }
                    if (!optionECellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("E", optionECellStr));
                    }
                    if (!optionFCellStr.isEmpty()) {
                        questionOptionList.add(new QuestionOption("F", optionFCellStr));
                    }

                    Question quest = new Question();
                    quest.setTopic(topic);
                    quest.setSubTopic(subTopic);
                    quest.setContributer(user);
                    quest.setQuestion(questionCellStr);
                    quest.setOptions(questionOptionList.toArray(new QuestionOption[0]));
                    quest.setAnswers(answerCellStr);
                    quest.setDifficultyLevel(QuizAppConstants.DIFFICULTY_MEDIUM);
                    quest.setExplanation(explanationCellStr);
                    quest.setChapter(chapterCellStr);
                    quest.setPage(pageCellStr);
                    quest.setBook(bookCellStr);
                    if (quest.isValidV2()) {
//                        List<Question> matchingQuestionList = questionRepo.findByQuestionAndTopicAndSubTopic(quest.getQuestion(), quest.getTopic().getTitle(), quest.getSubTopic().getTitle());
                        Question existingQuestion = existingQuestionMap.get(quest.toString());
                        if (existingQuestion == null) { // Only if the question doesn't exist.
                            questionRepo.save(quest);
                            questionsAdded++;
                            LOG.info("Added Question >> " + quest.getQuestion());
                        } else { // If matching question is found.
                            if (!existingQuestion.toString().equalsIgnoreCase(quest.toString())) {
                                existingQuestion.setOptions(quest.getOptions());
                                existingQuestion.setAnswers(quest.getAnswers());
                                existingQuestion.setDifficultyLevel(quest.getDifficultyLevel());
                                existingQuestion.setExplanation(quest.getExplanation());
                                existingQuestion.setChapter(quest.getChapter());
                                existingQuestion.setPage(quest.getPage());
                                existingQuestion.setBook(quest.getBook());
                                questionRepo.save(existingQuestion);
                                questionsUpdated++;
                                LOG.info("Updated Question >> " + existingQuestion.getQuestion());
                            } else {
                                questionsSkipped++;
//                                LOG.info("No Changes detected in question. Skipping >> " + quest.getQuestion());
                            }
                        }
                    } else {
                        LOG.log(Level.SEVERE, "Invalid question skipped >> {0}", quest.getQuestion());
                    }
                }
            }
        }
        LOG.info("Import Stats. Added (" + questionsAdded + "), Updated (" + questionsUpdated + "), Skipped (" + questionsSkipped + ")");
    }

}
