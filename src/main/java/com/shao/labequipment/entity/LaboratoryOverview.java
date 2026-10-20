package com.shao.labequipment.entity;

public record LaboratoryOverview(long id, String labCode, String labName,
        String location, int capacity, String status, long deviceCount,
        long availableCount, long borrowedCount, long maintenanceCount,
        long disabledCount, long pendingReservationCount) { }
