package com.shao.labequipment.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final long id;
    private final String userName;
    private final String moduleName;
    private final String actionName;
    private final String targetType;
    private final String detail;
    private final LocalDateTime operationTime;

    public AuditLog(long id, String userName, String moduleName,
                    String actionName, String targetType,
                    String detail, LocalDateTime operationTime) {
        this.id = id;
        this.userName = userName;
        this.moduleName = moduleName;
        this.actionName = actionName;
        this.targetType = targetType;
        this.detail = detail;
        this.operationTime = operationTime;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName == null ? "系统/数据库操作" : userName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getActionName() {
        return actionName;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getDetail() {
        return detail;
    }

    public String getOperationTimeText() {
        return operationTime.format(FORMATTER);
    }
}