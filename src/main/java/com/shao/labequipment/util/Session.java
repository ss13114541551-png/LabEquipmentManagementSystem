package com.shao.labequipment.util;

import com.shao.labequipment.dao.AuthDao;
import com.shao.labequipment.entity.LoginUser;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Session {
    private static volatile LoginUser currentUser;
    private static final CopyOnWriteArrayList<Runnable> listeners = new CopyOnWriteArrayList<>();
    private Session() { }
    public static LoginUser getCurrentUser() { return currentUser; }

    public static void setCurrentUser(LoginUser user) {
        LoginUser old = currentUser;
        currentUser = user;
        if (!Objects.equals(old, user)) notifyListeners();
    }

    public static LoginUser refreshCurrentUser() throws Exception {
        LoginUser previous = currentUser;
        if (previous == null) throw new IllegalStateException("请先登录。");
        LoginUser refreshed = new AuthDao().findById(previous.getId());
        if (currentUser == previous) setCurrentUser(refreshed);
        return refreshed;
    }

    public static LoginUser requireActiveUser() throws Exception {
        LoginUser user = refreshCurrentUser();
        if (user == null) throw new IllegalStateException("账号已停用或删除，请重新登录。");
        return user;
    }

    public static LoginUser requirePermission(String code) throws Exception {
        LoginUser user = requireActiveUser();
        if (!user.hasPermission(code)) throw new SecurityException("当前账号已无此操作权限，请联系管理员。");
        return user;
    }

    public static LoginUser requireManager() throws Exception {
        LoginUser user = requireActiveUser();
        if (!user.canViewDashboard()) throw new SecurityException("当前账号无管理查看权限。");
        return user;
    }

    public static void bind(Window window, Runnable listener) {
        listeners.add(listener);
        window.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { listeners.remove(listener); }
        });
        listener.run();
    }

    private static void notifyListeners() {
        Runnable task = () -> listeners.forEach(Runnable::run);
        if (SwingUtilities.isEventDispatchThread()) task.run();
        else SwingUtilities.invokeLater(task);
    }
}
