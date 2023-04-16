package com.cyberaka.quiz.domain;

/**
 * DTO to denote subject import.
 */
public class SubjectImport {

    private String subject;
    private String targetSheet;
    private String sourceWorkSheet;
    private String sourceSheet;

    public SubjectImport() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTargetSheet() {
        return targetSheet;
    }

    public void setTargetSheet(String targetSheet) {
        this.targetSheet = targetSheet;
    }

    public String getSourceWorkSheet() {
        return sourceWorkSheet;
    }

    public void setSourceWorkSheet(String sourceWorkSheet) {
        this.sourceWorkSheet = sourceWorkSheet;
    }

    public String getSourceSheet() {
        return sourceSheet;
    }

    public void setSourceSheet(String sourceSheet) {
        this.sourceSheet = sourceSheet;
    }

    @Override
    public String toString() {
        return "SubjectImport{" +
                "subject='" + subject + '\'' +
                ", targetSheet='" + targetSheet + '\'' +
                ", sourceWorkSheet='" + sourceWorkSheet + '\'' +
                ", sourceSheet='" + sourceSheet + '\'' +
                '}';
    }
}
