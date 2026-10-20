package com.shao.labequipment.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BorrowRecord {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final long id;
    private final String borrowNo;
    private final String borrowerName;
    private final String deviceName;
    private final LocalDateTime borrowTime;
    private final LocalDateTime expectedReturnTime;
    private final String status;
    private final boolean overdue;
    private final boolean dueSoon;

    public BorrowRecord(long id, String borrowNo, String borrowerName,
                        String deviceName, LocalDateTime borrowTime,
                        LocalDateTime expectedReturnTime, String status,
                        boolean overdue, boolean dueSoon) {
        this.id = id;
        this.borrowNo = borrowNo;
        this.borrowerName = borrowerName;
        this.deviceName = deviceName;
        this.borrowTime = borrowTime;
        this.expectedReturnTime = expectedReturnTime;
        this.status = status;
        this.overdue = overdue;
        this.dueSoon = dueSoon;
    }

    public long getId() {
        return id;
    }

    public String getBorrowNo() {
        return borrowNo;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getBorrowTimeText() {
        return borrowTime.format(FORMATTER);
    }

    public String getExpectedReturnTimeText() {
        return expectedReturnTime.format(FORMATTER);
    }

    public String getStatus() {
        return status;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public boolean isDueSoon() {
        return dueSoon;
    }
}
