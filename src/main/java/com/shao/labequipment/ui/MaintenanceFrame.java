package com.shao.labequipment.ui;

import com.shao.labequipment.dao.MaintenanceRecordDao;
import com.shao.labequipment.entity.MaintenanceRecord;
import com.shao.labequipment.util.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

public class MaintenanceFrame extends JFrame {
    private final JTable maintenanceTable = new JTable();
    private final MaintenanceRecordDao maintenanceDao = new MaintenanceRecordDao();
    private final JLabel pendingLabel = new JLabel("待处理：0");

    public MaintenanceFrame() {
        setTitle("设备维修管理");
        setSize(1250, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton refreshButton = new JButton("刷新记录");
        refreshButton.addActionListener(e -> loadData());

        JButton startButton = new JButton("开始处理");
        startButton.addActionListener(e -> startSelected());

        JButton completeButton = new JButton("登记处理结果");
        completeButton.addActionListener(e -> completeSelected());

        pendingLabel.setForeground(new Color(180, 0, 0));
        pendingLabel.setFont(pendingLabel.getFont().deriveFont(Font.BOLD));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(refreshButton);
        topPanel.add(startButton);
        topPanel.add(completeButton);
        topPanel.add(pendingLabel);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(maintenanceTable), BorderLayout.CENTER);
        Session.bind(this, () -> {
            if (Session.getCurrentUser() == null
                    || !Session.getCurrentUser().hasPermission("MAINTENANCE_MANAGE")) dispose();
        });
        loadData();
    }

    private void loadData() {
        String[] columns = {
                "ID", "维修编号", "设备编号", "设备名称", "报修人",
                "报修时间", "开始时间", "结束时间", "故障说明",
                "费用", "状态", "处理结果"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            int pendingCount = 0;
            for (MaintenanceRecord record : maintenanceDao.findAll()) {
                if ("PENDING".equals(record.getStatus())) {
                    pendingCount++;
                }
                model.addRow(new Object[]{
                        record.getId(), record.getMaintenanceNo(),
                        record.getDeviceNo(), record.getDeviceName(),
                        record.getReporterName(), record.getReportTimeText(),
                        record.getStartTimeText(), record.getEndTimeText(),
                        record.getFaultDescription(), record.getCost(),
                        record.getStatus(), record.getRemark()
                });
            }
            maintenanceTable.setModel(model);
            pendingLabel.setText("待处理：" + pendingCount);
        } catch (Exception e) {
            showError("读取维修记录失败：" + e.getMessage());
        }
    }

    private Long selectedId() {
        int row = maintenanceTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条维修记录。");
            return null;
        }
        return ((Number) maintenanceTable.getValueAt(row, 0)).longValue();
    }

    private void startSelected() {
        Long id = selectedId();
        if (id == null) return;
        try {
            maintenanceDao.startProcessing(id);
            JOptionPane.showMessageDialog(this, "已登记开始处理。");
            loadData();
        } catch (Exception e) {
            showError("操作失败：" + e.getMessage());
        }
    }

    private void completeSelected() {
        Long id = selectedId();
        if (id == null) return;

        JTextField costField = new JTextField("0.00");
        JTextArea resultArea = new JTextArea(4, 25);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel costPanel = new JPanel(new BorderLayout(8, 0));
        costPanel.add(new JLabel("维修费用："), BorderLayout.WEST);
        costPanel.add(costField, BorderLayout.CENTER);
        panel.add(costPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(new JLabel("处理结果："), BorderLayout.WEST);

        int option = JOptionPane.showConfirmDialog(
                this, panel, "登记维修处理结果",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (option != JOptionPane.OK_OPTION) return;

        try {
            BigDecimal cost = new BigDecimal(costField.getText().trim());
            String result = resultArea.getText().trim();
            if (cost.signum() < 0) {
                throw new IllegalArgumentException("维修费用不能为负数。");
            }
            if (result.isEmpty()) {
                throw new IllegalArgumentException("处理结果不能为空。");
            }

            maintenanceDao.completeMaintenance(id, cost, result);
            JOptionPane.showMessageDialog(this, "维修处理结果登记成功。");
            loadData();
        } catch (NumberFormatException e) {
            showError("维修费用格式不正确。");
        } catch (Exception e) {
            showError("登记失败：" + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "错误", JOptionPane.ERROR_MESSAGE
        );
    }
}
