package com.shao.labequipment.ui;

import com.shao.labequipment.dao.AuthDao;

import com.shao.labequipment.entity.LoginUser;
import com.shao.labequipment.util.Session;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JTextField captchaField = new JTextField();

    private final CaptchaPanel captchaPanel = new CaptchaPanel();
    private final AuthDao authDao = new AuthDao();

    public LoginFrame() {
        setTitle("系统登录");
        setSize(500, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 12));

        formPanel.add(new JLabel("用户名："));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("密码："));
        formPanel.add(passwordField);

        formPanel.add(new JLabel("验证码："));

        JPanel captchaBox = new JPanel(new BorderLayout(8, 0));
        captchaBox.add(captchaField, BorderLayout.CENTER);
        captchaBox.add(captchaPanel, BorderLayout.EAST);
        formPanel.add(captchaBox);

        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(e -> handleLogin());

        JButton refreshButton = new JButton("换一张");
        refreshButton.addActionListener(e -> captchaPanel.refresh());

        JButton changePasswordButton = new JButton("修改密码");
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(changePasswordButton);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String captcha = captchaField.getText().trim();

        if (!captchaPanel.getCode().equalsIgnoreCase(captcha)) {
            JOptionPane.showMessageDialog(this, "验证码错误，请重新输入。");
            captchaPanel.refresh();
            captchaField.setText("");
            return;
        }

        try {
            LoginUser loginUser = authDao.login(username, password);

            if (loginUser != null) {
                Session.setCurrentUser(loginUser);
                JOptionPane.showMessageDialog(this, "登录成功！");

                dispose();

                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误。");
                captchaPanel.refresh();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "登录失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showChangePasswordDialog() {
        JTextField changeUsernameField = new JTextField(usernameField.getText().trim());
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("用户名："));
        panel.add(changeUsernameField);
        panel.add(new JLabel("原密码："));
        panel.add(oldPasswordField);
        panel.add(new JLabel("新密码："));
        panel.add(newPasswordField);
        panel.add(new JLabel("确认新密码："));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "修改个人密码",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = changeUsernameField.getText().trim();
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isBlank() || oldPassword.isBlank()
                || newPassword.isBlank() || confirmPassword.isBlank()) {
            JOptionPane.showMessageDialog(this, "所有内容均不能为空。");
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "新密码长度不能少于6位。");
            return;
        }

        if (newPassword.equals(oldPassword)) {
            JOptionPane.showMessageDialog(this, "新密码不能与原密码相同。");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的新密码不一致。");
            return;
        }

        try {
            boolean changed = authDao.changePassword(
                    username,
                    oldPassword,
                    newPassword
            );

            if (!changed) {
                JOptionPane.showMessageDialog(
                        this,
                        "用户名或原密码错误，修改失败。"
                );
                return;
            }

            JOptionPane.showMessageDialog(this, "密码修改成功，请使用新密码登录。");
            usernameField.setText(username);
            passwordField.setText("");
            captchaField.setText("");
            captchaPanel.refresh();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "修改密码失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
