package com.shao.labequipment.entity;

public class DeviceUsage {
    private final String deviceNo;
    private final String deviceName;
    private final String labName;
    private final String categoryName;
    private final String status;
    private final long borrowCount;
    private final long totalBorrowMinutes;

    public DeviceUsage(String deviceNo, String deviceName, String labName,
                       int borrowCount, int totalBorrowMinutes) {
        this(deviceNo, deviceName, "", labName, "", borrowCount, totalBorrowMinutes);
    }

    public DeviceUsage(String deviceNo, String deviceName, String categoryName,
                       String labName, String status, long borrowCount, long totalBorrowMinutes) {
        this.deviceNo = deviceNo;
        this.deviceName = deviceName;
        this.labName = labName;
        this.categoryName = categoryName;
        this.status = status;
        this.borrowCount = borrowCount;
        this.totalBorrowMinutes = totalBorrowMinutes;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getLabName() {
        return labName;
    }

    public String getCategoryName() { return categoryName; }
    public String getStatus() { return status; }

    public long getBorrowCount() {
        return borrowCount;
    }

    public long getTotalBorrowMinutes() {
        return totalBorrowMinutes;
    }
}
