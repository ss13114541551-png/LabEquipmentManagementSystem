package com.shao.labequipment.entity;

public class UserProfile {
    private final long id;
    private final String username;
    private final String realName;
    private final String phone;
    private final String email;
    private final int status;
    private final String roleNames;

    public UserProfile(long id, String username, String realName,
                       String phone, String email, int status,
                       String roleNames) {
        this.id = id;
        this.username = username;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.roleNames = roleNames;
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getRealName() { return realName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public int getStatus() { return status; }
    public String getRoleNames() { return roleNames; }
}
