package com.shao.labequipment.ui;

import com.shao.labequipment.dao.ProfileDao;
import com.shao.labequipment.entity.UserProfile;
import com.shao.labequipment.util.Session;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ProfileFrame extends JFrame {
    private final JTextField usernameField = new JTextField(28);
    private final JTextField realNameField = new JTextField(28);
    private final JTextField roleField = new JTextField(28);
    private final JTextField statusField = new JTextField(28);
    private final JTextField phoneField = new JTextField(28);
    private final JTextField emailField = new JTextField(28);
    private final JPasswordField oldPasswordField = new JPasswordField(28);
    private final JPasswordField newPasswordField = new JPasswordField(28);
    private final JPasswordField confirmPasswordField = new JPasswordField(28);
    private final ProfileDao profileDao = new ProfileDao();

    public ProfileFrame() {
        setTitle("个人资料与账户安全");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        for (JTextField field : new JTextField[]{usernameField, realNameField, roleField,
                statusField, phoneField, emailField, oldPasswordField, newPasswordField, confirmPasswordField}) {
            field.setFont(field.getFont().deriveFont(16f));
            field.setPreferredSize(new Dimension(field.getPreferredSize().width, 34));
        }
        realNameField.setEditable(false);
        roleField.setEditable(false);
        statusField.setEditable(false);
        JPanel form = form();
        row(form, 0, "用户名：", usernameField);
        row(form, 1, "真实姓名：", realNameField);
        row(form, 2, "用户角色：", roleField);
        row(form, 3, "账号状态：", statusField);
        row(form, 4, "联系电话：", phoneField);
        row(form, 5, "邮箱：", emailField);
        JButton refresh = new JButton("刷新资料");
        refresh.addActionListener(e -> loadProfile());
        JButton save = new JButton("保存资料修改");
        save.addActionListener(e -> saveProfile());
        JPanel details = page(form, refresh, save);

        JPanel passwordForm = form();
        row(passwordForm, 0, "原密码：", oldPasswordField);
        row(passwordForm, 1, "新密码：", newPasswordField);
        row(passwordForm, 2, "确认新密码：", confirmPasswordField);
        row(passwordForm, 3, "", new JLabel("验证原密码后修改当前账号密码，新密码为6～128位。"));
        JButton change = new JButton("验证并修改密码");
        change.addActionListener(e -> changePassword());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("个人资料", details);
        tabs.addTab("修改密码", page(passwordForm, change));
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(tabs);
        setContentPane(content);
        pack();
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(Math.max(680, getWidth()), screen.width - 32),
                Math.min(Math.max(490, getHeight()), screen.height - 32));
        setMinimumSize(new Dimension(Math.min(520, screen.width - 32), Math.min(360, screen.height - 32)));
        setLocationRelativeTo(null);
        Session.bind(this, () -> {
            if (Session.getCurrentUser() == null) { dispose(); return; }
            roleField.setText(Session.getCurrentUser().getRoleName());
        });
        loadProfile();
    }

    private JPanel form() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return p;
    }
    private void row(JPanel p, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(9, 0, 9, 18);
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(labelComponent.getFont().deriveFont(16f));
        p.add(labelComponent, c);
        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(9, 0, 9, 0);
        p.add(field, c);
    }
    private JPanel page(JPanel form, JButton... buttons) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(form, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        for (JButton b : buttons) footer.add(b);
        JPanel page = new JPanel(new BorderLayout());
        page.add(scroll, BorderLayout.CENTER); page.add(footer, BorderLayout.SOUTH);
        return page;
    }

    private void loadProfile() {
        try {
            long id = Session.requireActiveUser().getId();
            UserProfile profile = profileDao.findByUserId(id);
            if (profile == null) { dispose(); return; }
            usernameField.setText(profile.getUsername());
            realNameField.setText(profile.getRealName());
            roleField.setText(Session.getCurrentUser().getRoleName());
            statusField.setText(profile.getStatus() == 1 ? "正常" : "停用");
            phoneField.setText(profile.getPhone() == null ? "" : profile.getPhone());
            emailField.setText(profile.getEmail() == null ? "" : profile.getEmail());
        } catch (Exception e) { error("读取资料失败：" + e.getMessage()); }
    }

    private void saveProfile() {
        String username = usernameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        if (!username.matches("[A-Za-z0-9_]{4,50}")) { error("用户名须为4～50位字母、数字或下划线。"); return; }
        if (!phone.isEmpty() && !phone.matches("[0-9+\\- ]{6,20}")) { error("联系电话格式不正确。"); return; }
        if (email.length() > 100 || (!email.isEmpty() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))) { error("邮箱格式不正确或超过100个字符。"); return; }
        try {
            if (!profileDao.updateContact(Session.getCurrentUser().getId(), username, phone, email)) {
                error("保存失败，当前账号不可用。"); return;
            }
            Session.refreshCurrentUser();
            JOptionPane.showMessageDialog(this, "个人资料保存成功。");
            loadProfile();
        } catch (Exception e) { error("保存失败：" + e.getMessage()); }
    }

    private void changePassword() {
        char[] old = oldPasswordField.getPassword();
        char[] fresh = newPasswordField.getPassword();
        char[] confirm = confirmPasswordField.getPassword();
        try {
            if (!Arrays.equals(fresh, confirm)) { error("两次输入的新密码不一致。"); return; }
            boolean changed = profileDao.changePassword(Session.getCurrentUser().getId(), new String(old), new String(fresh));
            if (!changed) { error("原密码错误，密码未修改。"); oldPasswordField.setText(""); return; }
            oldPasswordField.setText(""); newPasswordField.setText(""); confirmPasswordField.setText("");
            JOptionPane.showMessageDialog(this, "密码修改成功，下次登录请使用新密码。");
        } catch (Exception e) { error("修改密码失败：" + e.getMessage()); }
        finally { Arrays.fill(old, '\0'); Arrays.fill(fresh, '\0'); Arrays.fill(confirm, '\0'); }
    }

    private void error(String message) { JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.WARNING_MESSAGE); }
}
