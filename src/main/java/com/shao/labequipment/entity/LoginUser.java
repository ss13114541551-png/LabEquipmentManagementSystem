package com.shao.labequipment.entity;

import java.util.Set;
import java.util.Objects;

public final class LoginUser {
    private final long id;
    private final String username;
    private final String realName;
    private final String roleName;
    private final Set<String> permissions;

    public LoginUser(long id, String username, String realName,
                     String roleName, Set<String> permissions) {
        this.id = id;
        this.username = username;
        this.realName = realName;
        this.roleName = roleName;
        this.permissions = Set.copyOf(permissions);
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getRealName() { return realName; }
    public String getRoleName() { return roleName; }
    public boolean hasPermission(String code) { return permissions.contains(code); }
    public boolean canAuditReservations() { return hasPermission("RESERVATION_AUDIT"); }
    public boolean canManageRoles() { return hasPermission("ROLE_MANAGE"); }
    public boolean canManageDevices() {
        return hasPermission("DEVICE_ADD") || hasPermission("DEVICE_EDIT")
                || hasPermission("DEVICE_DELETE");
    }
    public boolean canExport() { return hasPermission("REPORT_EXPORT"); }
    public boolean canViewDashboard() {
        return canManageDevices() || hasPermission("BORROW_MANAGE")
                || hasPermission("MAINTENANCE_MANAGE") || canManageRoles();
    }

    @Override public boolean equals(Object other) {
        if (!(other instanceof LoginUser u)) return false;
        return id == u.id && Objects.equals(username, u.username)
                && Objects.equals(realName, u.realName)
                && Objects.equals(roleName, u.roleName)
                && permissions.equals(u.permissions);
    }
    @Override public int hashCode() {
        return Objects.hash(id, username, realName, roleName, permissions);
    }
}
