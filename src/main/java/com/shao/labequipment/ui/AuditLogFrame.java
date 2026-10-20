package com.shao.labequipment.ui;

import com.shao.labequipment.dao.AuditLogDao;
import com.shao.labequipment.entity.AuditLog;
import com.shao.labequipment.util.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AuditLogFrame extends JFrame {

    private final JTable logTable = new JTable();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    public AuditLogFrame() {
        setTitle("系统操作审计日志");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton refreshButton = new JButton("刷新日志");
        refreshButton.addActionListener(e -> loadLogData());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logTable), BorderLayout.CENTER);
        Session.bind(this, () -> {
            if (Session.getCurrentUser() == null || !Session.getCurrentUser().canViewDashboard()) dispose();
        });

        loadLogData();
    }

    private void loadLogData() {
        String[] columns = {
                "ID", "操作用户", "功能模块", "操作类型",
                "目标对象", "操作详情", "操作时间"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            for (AuditLog log : auditLogDao.findLatestLogs()) {
                model.addRow(new Object[]{
                        log.getId(),
                        log.getUserName(),
                        log.getModuleName(),
                        log.getActionName(),
                        log.getTargetType(),
                        log.getDetail(),
                        log.getOperationTimeText()
                });
            }

            logTable.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "读取审计日志失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
