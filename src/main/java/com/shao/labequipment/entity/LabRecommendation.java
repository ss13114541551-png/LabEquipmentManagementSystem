package com.shao.labequipment.entity;

public class LabRecommendation {
    private final long labId;
    private final String labName;
    private final String location;
    private final int capacity;
    private final int matchScore;

    public LabRecommendation(long labId, String labName,
                             String location, int capacity,
                             int matchScore) {
        this.labId = labId;
        this.labName = labName;
        this.location = location;
        this.capacity = capacity;
        this.matchScore = matchScore;
    }

    public long getLabId() {
        return labId;
    }

    public String getLabName() {
        return labName;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMatchScore() {
        return matchScore;
    }
}