package com.shao.labequipment.entity;

public class Device {
    private final long id;
    private final String deviceNo;
    private final String deviceName;
    private final String categoryName;
    private final String labName;
    private final String status;

    public Device(long id, String deviceNo, String deviceName,
                  String categoryName, String labName, String status) {
        this.id = id;
        this.deviceNo = deviceNo;
        this.deviceName = deviceName;
        this.categoryName = categoryName;
        this.labName = labName;
        this.status = status;
    }
    public long getId() {
        return id;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getLabName() {
        return labName;
    }

    public String getStatus() {
        return status;
    }
    @Override
    public String toString() {
        return id + " | " + deviceNo + " | " + deviceName
                + " | " + categoryName + " | " + labName
                + " | " + status;
    }
}