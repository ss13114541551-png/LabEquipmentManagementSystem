package com.shao.labequipment.entity;

public class DashboardSummary {
    private final int laboratoryCount;
    private final int deviceCount;
    private final int pendingReservationCount;
    private final int borrowingCount;

    public DashboardSummary(int laboratoryCount, int deviceCount,
                            int pendingReservationCount, int borrowingCount) {
        this.laboratoryCount = laboratoryCount;
        this.deviceCount = deviceCount;
        this.pendingReservationCount = pendingReservationCount;
        this.borrowingCount = borrowingCount;
    }

    public int getLaboratoryCount() {
        return laboratoryCount;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public int getPendingReservationCount() {
        return pendingReservationCount;
    }

    public int getBorrowingCount() {
        return borrowingCount;
    }
}