package com.sibi.helpi.models;

public class Report {
    private String reason;
    private String description;

    public Report(String reason, String description) {
        this.reason = reason;
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }
}