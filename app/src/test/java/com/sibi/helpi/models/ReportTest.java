package com.sibi.helpi.models;

import org.junit.Test;
import static org.junit.Assert.*;
import com.sibi.helpi.utils.AppConstants.reportReason;
import com.sibi.helpi.utils.AppConstants.reportStatus;

import java.util.Date;

public class ReportTest {

    @Test
    public void reportInitializationWithParameters() {
        Report report = new Report("postId123", reportReason.SPAM, "reporterId123", "Notes");
        assertEquals("postId123", report.getPostId());
        assertEquals(reportReason.SPAM, report.getReason());
        assertEquals("reporterId123", report.getReporterId());
        assertEquals("Notes", report.getReporterNotes());
        assertEquals(reportStatus.PENDING, report.getStatus());
        assertNotNull(report.getDateReported());
        assertEquals("", report.getReportId());
    }

    @Test
    public void reportDefaultInitialization() {
        Report report = new Report();
        assertEquals("", report.getPostId());
        assertEquals(reportReason.SPAM, report.getReason());
        assertEquals("", report.getReporterId());
        assertEquals("", report.getReporterNotes());
        assertEquals(reportStatus.PENDING, report.getStatus());
        assertNotNull(report.getDateReported());
        assertEquals("", report.getReportId());
    }

    @Test
    public void setStatus() {
        Report report = new Report();
        report.setStatus(reportStatus.RESOLVED);
        assertEquals(reportStatus.RESOLVED, report.getStatus());
    }

    @Test
    public void setHandlerId() {
        Report report = new Report();
        report.setHandlerId("handlerId123");
        assertEquals("handlerId123", report.getHandlerId());
    }

    @Test
    public void setHandlerNotes() {
        Report report = new Report();
        report.setHandlerNotes("Handler notes");
        assertEquals("Handler notes", report.getHandlerNotes());
    }

    @Test
    public void setReportId() {
        Report report = new Report();
        report.setReportId("reportId123");
        assertEquals("reportId123", report.getReportId());
    }

    @Test
    public void getReason() {
        Report report = new Report("postId123", reportReason.SPAM, "reporterId123", "Notes");
        assertEquals(reportReason.SPAM, report.getReason());
    }

    @Test
    public void getPostId() {
        Report report = new Report("postId123", reportReason.SPAM, "reporterId123", "Notes");
        assertEquals("postId123", report.getPostId());
    }

    @Test
    public void getStatus() {
        Report report = new Report("postId123", reportReason.SPAM, "reporterId123", "Notes");
        assertEquals(reportStatus.PENDING, report.getStatus());
    }

    @Test
    public void getReporterId() {
        Report report = new Report("postId123", reportReason.SPAM, "reporterId123", "Notes");
        assertEquals("reporterId123", report.getReporterId());
    }

    @Test
    public void getReporterNotes() {
        Report report = new Report("postId123", reportReason.SPAM, "reporterId123", "Notes");
        assertEquals("Notes", report.getReporterNotes());
    }

    @Test
    public void getHandlerId() {
        Report report = new Report();
        report.setHandlerId("handlerId123");
        assertEquals("handlerId123", report.getHandlerId());
    }

    @Test
    public void getHandlerNotes() {
        Report report = new Report();
        report.setHandlerNotes("Handler notes");
        assertEquals("Handler notes", report.getHandlerNotes());
    }

    @Test
    public void getDateReported() {
        Report report = new Report("postId123", reportReason.SPAM, "reporterId123", "Notes");
        assertNotNull(report.getDateReported());
    }

    @Test
    public void getReportId() {
        Report report = new Report();
        report.setReportId("reportId123");
        assertEquals("reportId123", report.getReportId());
    }
}