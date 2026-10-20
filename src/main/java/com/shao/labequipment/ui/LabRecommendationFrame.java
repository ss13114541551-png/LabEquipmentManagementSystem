package com.shao.labequipment.ui;

import com.shao.labequipment.dao.LaboratoryDao;
import com.shao.labequipment.entity.LabRecommendation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import com.shao.labequipment.dao.ReservationDao;
import com.shao.labequipment.util.Session;
import com.shao.labequipment.util.ReservationTimes;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LabRecommendationFrame extends JFrame {

    private final JTextField startTimeField =
            new JTextField(16);

    private final JTextField endTimeField =
            new JTextField(16);

    private final JTextField peopleCountField =
            new JTextField("30", 5);

    private final JTable resultTable = new JTable();
    private final LaboratoryDao laboratoryDao = new LaboratoryDao();
    private final ReservationDao reservationDao = new ReservationDao();

    public LabRecommendationFrame() {
        setTitle("智能空闲实验室推荐");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        queryPanel.add(new JLabel("开始时间："));
        queryPanel.add(startTimeField);

        queryPanel.add(new JLabel("结束时间："));
        queryPanel.add(endTimeField);

        queryPanel.add(new JLabel("人数："));
        queryPanel.add(peopleCountField);

        JButton recommendButton = new JButton("推荐实验室");
        recommendButton.addActionListener(e -> recommendLabs());
        queryPanel.add(recommendButton);

        JButton reserveButton = new JButton("预约选中实验室");
        reserveButton.addActionListener(e -> reserveSelectedLab());
        queryPanel.add(reserveButton);
        JButton resetTimeButton = new JButton("重置为当前推荐时段");
        resetTimeButton.addActionListener(e -> resetSuggestedTimes());
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queryPanel.remove(recommendButton); queryPanel.remove(reserveButton);
        actionPanel.add(recommendButton); actionPanel.add(reserveButton); actionPanel.add(resetTimeButton);
        actionPanel.add(new JLabel("时间格式：年-月-日 时:分；默认预约2小时。"));
        JPanel header = new JPanel(new BorderLayout());
        header.add(queryPanel, BorderLayout.NORTH); header.add(actionPanel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
        Session.bind(this, () -> {
            var user = Session.getCurrentUser();
            if (user == null) { dispose(); return; }
            reserveButton.setEnabled(user.hasPermission("RESERVATION_CREATE"));
        });
        resetSuggestedTimes();
        DocumentListener clearResults = new DocumentListener() {
            private void clear() { resultTable.setModel(new DefaultTableModel()); }
            public void insertUpdate(DocumentEvent e) { clear(); }
            public void removeUpdate(DocumentEvent e) { clear(); }
            public void changedUpdate(DocumentEvent e) { clear(); }
        };
        startTimeField.getDocument().addDocumentListener(clearResults);
        endTimeField.getDocument().addDocumentListener(clearResults);
        peopleCountField.getDocument().addDocumentListener(clearResults);
    }

    private void resetSuggestedTimes() {
        try {
            LocalDateTime start = ReservationTimes.defaultStart();
            startTimeField.setText(start.format(ReservationTimes.FORMAT));
            endTimeField.setText(start.plusHours(2).format(ReservationTimes.FORMAT));
            resultTable.setModel(new DefaultTableModel());
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "读取默认时间失败：" + e.getMessage()); }
    }

    private void recommendLabs() {
        String[] columns = {
                "实验室ID", "实验室名称", "地点", "容量", "容量匹配差值"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            DateTimeFormatter formatter = ReservationTimes.FORMAT;

            LocalDateTime startTime = LocalDateTime.parse(
                    startTimeField.getText().trim(), formatter
            );
            LocalDateTime endTime = LocalDateTime.parse(
                    endTimeField.getText().trim(), formatter
            );
            int peopleCount = Integer.parseInt(
                    peopleCountField.getText().trim()
            );

            resultTable.setModel(model);
            for (LabRecommendation recommendation :
                    laboratoryDao.recommendAvailableLabs(
                            startTime, endTime, peopleCount
                    )) {

                model.addRow(new Object[]{
                        recommendation.getLabId(),
                        recommendation.getLabName(),
                        recommendation.getLocation(),
                        recommendation.getCapacity(),
                        recommendation.getMatchScore()
                });
            }

            resultTable.setModel(model);

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "没有符合条件的可用实验室。"
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "推荐失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void reserveSelectedLab() {
        int selectedRow = resultTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个推荐实验室。");
            return;
        }

        try {
            long laboratoryId = ((Number) resultTable
                    .getValueAt(selectedRow, 0)).longValue();

            String labName = String.valueOf(
                    resultTable.getValueAt(selectedRow, 1)
            );

            String purpose = JOptionPane.showInputDialog(
                    this,
                    "请输入预约用途：",
                    "预约 " + labName,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (purpose == null || purpose.trim().isEmpty()) {
                return;
            }

            DateTimeFormatter formatter = ReservationTimes.FORMAT;

            LocalDateTime startTime = LocalDateTime.parse(
                    startTimeField.getText().trim(),
                    formatter
            );

            LocalDateTime endTime = LocalDateTime.parse(
                    endTimeField.getText().trim(),
                    formatter
            );

            reservationDao.createReservation(
                    Session.getCurrentUser().getId(),
                    laboratoryId,
                    startTime,
                    endTime,
                    purpose.trim()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "预约提交成功，等待管理员审核。"
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "预约失败：" + e.getMessage(),
                    "预约冲突或输入错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
