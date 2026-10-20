package com.shao.labequipment.ui;

import com.shao.labequipment.dao.UserRoleDao;
import com.shao.labequipment.entity.UserRoleInfo;
import com.shao.labequipment.util.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserRoleFrame extends JFrame {

    private final JTable userTable = new JTable();
    private final JComboBox<String> roleBox = new JComboBox<>();
    private final UserRoleDao userRoleDao = new UserRoleDao();

    public UserRoleFrame() {
        setTitle("用户角色授权管理");
        setSize(850, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton refreshButton = new JButton("刷新用户角色");
        refreshButton.addActionListener(e -> loadData());

        JButton assignButton = new JButton("分配选中用户角色");
        assignButton.addActionListener(e -> assignSelectedUserRole());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("目标角色："));
        topPanel.add(roleBox);
        topPanel.add(assignButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(userTable), BorderLayout.CENTER);
        Session.bind(this, () -> {
            if (Session.getCurrentUser() == null || !Session.getCurrentUser().canManageRoles()) dispose();
        });

        loadRoleOptions();
        loadData();
    }

    private void loadRoleOptions() {
        try {
            roleBox.removeAllItems();

            for (String role : userRoleDao.findRoleOptions()) {
                roleBox.addItem(role);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "读取角色失败：" + e.getMessage());
        }
    }

    private void loadData() {
        String[] columns = {"用户ID", "用户名", "真实姓名", "当前角色"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            for (UserRoleInfo user : userRoleDao.findAllUserRoles()) {
                model.addRow(new Object[]{
                        user.getUserId(),
                        user.getUsername(),
                        user.getRealName(),
                        user.getRoleNames()
                });
            }

            userTable.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "读取用户失败：" + e.getMessage());
        }
    }

    private void assignSelectedUserRole() {
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择用户。");
            return;
        }

        try {
            long userId = ((Number) userTable
                    .getValueAt(selectedRow, 0)).longValue();

            String selectedRole = (String) roleBox.getSelectedItem();
            long roleId = Long.parseLong(selectedRole.split(" - ", 2)[0]);

            userRoleDao.replaceUserRole(userId, roleId);

            Session.refreshCurrentUser();

            JOptionPane.showMessageDialog(this, "角色分配成功！");
            if (Session.getCurrentUser() == null || !Session.getCurrentUser().canManageRoles()) return;
            loadData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "角色分配失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
