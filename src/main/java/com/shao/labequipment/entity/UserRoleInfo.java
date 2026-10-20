package com.shao.labequipment.entity;

public class UserRoleInfo {
    private final long userId;
    private final String username;
    private final String realName;
    private final String roleNames;

    public UserRoleInfo(long userId, String username,
                        String realName, String roleNames) {
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.roleNames = roleNames;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRealName() {
        return realName;
    }

    public String getRoleNames() {
        return roleNames == null ? "未分配角色" : roleNames;
    }
}