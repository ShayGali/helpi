package com.sibi.helpi.models;

import com.sibi.helpi.utils.AppConstants.reportStatus;
import com.sibi.helpi.utils.AppConstants.reportReason;

import java.util.Date;


public class Report {


    private final String postId;
    private final reportReason reason;
    private final String reporterId;
    private final String reporterNotes;
    private final Date dateReported; // date of the report
    private String handlerId;
    private String handlerNotes;
    private reportStatus status;
    private String reportId;



    public Report() {
        this.postId = "";
        this.reason = reportReason.SPAM;
        this.reporterId = "";
        this.status = reportStatus.PENDING;
        this.dateReported = new Date();      //Current date
        this.reporterNotes = "";
    }

    public Report(String postId, reportReason reason, String reporterId, String reporterNotes) {

        this.postId = postId;
        this.reason = reason;
        this.reporterId = reporterId;
        this.status = reportStatus.PENDING;
        this.dateReported = new Date();      //Current date
        this.reporterNotes = reporterNotes;

    }
    public reportReason getReason() {
        return reason;
    }
    public String getPostId() {
        return postId;
    }
    public reportStatus getStatus() {
        return status;
    }
    public void setStatus(reportStatus status) {
        this.status = status;
    }
    public String getReporterId() {
        return reporterId;
    }
    public String getReporterNotes() {
        return reporterNotes;
    }
    public String getHandlerId() {
        return handlerId;
    }
    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }
    public String getHandlerNotes() {
        return handlerNotes;
    }
    public void setHandlerNotes(String handlerNotes) {
        this.handlerNotes = handlerNotes;
    }
    public Date getDateReported() {
        return dateReported;
    }

    public String getId() {
        return reportId;
    }
    public void setId(String reportId) {
        this.reportId = reportId;
    }








}