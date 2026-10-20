package com.shao.labequipment.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final long id;
    private final String reservationNo;
    private final String applicantName;
    private final String labName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String purpose;
    private final String status;

    public Reservation(long id, String reservationNo, String applicantName,
                       String labName, LocalDateTime startTime,
                       LocalDateTime endTime, String purpose, String status) {
        this.id = id;
        this.reservationNo = reservationNo;
        this.applicantName = applicantName;
        this.labName = labName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getReservationNo() {
        return reservationNo;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getLabName() {
        return labName;
    }

    public String getStartTimeText() {
        return startTime.format(FORMATTER);
    }

    public String getEndTimeText() {
        return endTime.format(FORMATTER);
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }

}