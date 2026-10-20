package com.shao.labequipment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MaintenanceRecord {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final long id;
    private final String maintenanceNo;
    private final String deviceNo;
    private final String deviceName;
    private final String reporterName;
    private final LocalDateTime reportTime;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String faultDescription;
    private final BigDecimal cost;
    private final String status;
    private final String remark;

    public MaintenanceRecord(long id, String maintenanceNo, String deviceNo,
                             String deviceName, String reporterName,
                             LocalDateTime reportTime, LocalDateTime startTime,
                             LocalDateTime endTime, String faultDescription,
                             BigDecimal cost, String status, String remark) {
        this.id = id;
        this.maintenanceNo = maintenanceNo;
        this.deviceNo = deviceNo;
        this.deviceName = deviceName;
        this.reporterName = reporterName;
        this.reportTime = reportTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.faultDescription = faultDescription;
        this.cost = cost;
        this.status = status;
        this.remark = remark;
    }

    private String format(LocalDateTime time) {
        return time == null ? "" : time.format(FORMATTER);
    }

    public long getId() { return id; }
    public String getMaintenanceNo() { return maintenanceNo; }
    public String getDeviceNo() { return deviceNo; }
    public String getDeviceName() { return deviceName; }
    public String getReporterName() { return reporterName; }
    public String getReportTimeText() { return format(reportTime); }
    public String getStartTimeText() { return format(startTime); }
    public String getEndTimeText() { return format(endTime); }
    public String getFaultDescription() { return faultDescription; }
    public BigDecimal getCost() { return cost; }
    public String getStatus() { return status; }
    public String getRemark() { return remark == null ? "" : remark; }
}
