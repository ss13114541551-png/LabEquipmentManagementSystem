package com.shao.labequipment.ui;

import com.shao.labequipment.dao.ReservationDao;
import com.shao.labequipment.entity.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import com.shao.labequipment.util.Session;
import com.shao.labequipment.util.ReservationTimes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.shao.labequipment.entity.LoginUser;

public class ReservationFrame extends JFrame {

    private final JTable reservationTable = new JTable();
    private final ReservationDao reservationDao = new ReservationDao();

    public ReservationFrame() {
        setTitle("预约管理");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton refreshButton = new JButton("刷新预约列表");
        refreshButton.addActionListener(e -> loadReservationData());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(refreshButton);

        JButton createButton = new JButton("提交预约");
        createButton.addActionListener(e -> showCreateReservationDialog());
        topPanel.add(createButton);

        JButton approveButton = new JButton("通过选中预约");
        approveButton.addActionListener(e -> auditSelectedReservation("APPROVED"));

        JButton rejectButton = new JButton("拒绝选中预约");
        rejectButton.addActionListener(e -> auditSelectedReservation("REJECTED"));

        LoginUser currentUser = Session.getCurrentUser();
        approveButton.setEnabled(currentUser.canAuditReservations());
        rejectButton.setEnabled(currentUser.canAuditReservations());

        topPanel.add(approveButton);
        topPanel.add(rejectButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(reservationTable), BorderLayout.CENTER);
        Session.bind(this, () -> {
            LoginUser user = Session.getCurrentUser();
            if (user == null) { dispose(); return; }
            createButton.setEnabled(user.hasPermission("RESERVATION_CREATE"));
            approveButton.setEnabled(user.canAuditReservations());
            rejectButton.setEnabled(user.canAuditReservations());
        });

        loadReservationData();
    }

    private void loadReservationData() {
        String[] columns = {
                "ID", "预约编号", "申请人", "实验室",
                "开始时间", "结束时间", "用途", "状态"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            for (Reservation reservation : reservationDao.findAll()) {
                model.addRow(new Object[]{
                        reservation.getId(),
                        reservation.getReservationNo(),
                        reservation.getApplicantName(),
                        reservation.getLabName(),
                        reservation.getStartTimeText(),
                        reservation.getEndTimeText(),
                        reservation.getPurpose(),
                        reservation.getStatus()
                });
            }

            reservationTable.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "读取预约数据失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void showCreateReservationDialog() {
        JComboBox<String> labBox = new JComboBox<>();

        try {
            for (String option : reservationDao.findAvailableLabOptions()) {
                labBox.addItem(option);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "读取实验室失败：" + e.getMessage());
            return;
        }

        LocalDateTime suggestedStart;
        try {
            Session.requirePermission("RESERVATION_CREATE");
            suggestedStart = ReservationTimes.defaultStart();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "无法初始化预约：" + e.getMessage());
            return;
        }
        if (labBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "当前没有开放的实验室。");
            return;
        }
        JTextField startTimeField = new JTextField(suggestedStart.format(ReservationTimes.FORMAT), 20);
        JTextField endTimeField = new JTextField(suggestedStart.plusHours(2).format(ReservationTimes.FORMAT), 20);
        JTextField purposeField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("实验室："));
        panel.add(labBox);
        panel.add(new JLabel("开始时间："));
        panel.add(startTimeField);
        panel.add(new JLabel("结束时间："));
        panel.add(endTimeField);
        panel.add(new JLabel("使用用途："));
        panel.add(purposeField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "提交预约",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            DateTimeFormatter formatter = ReservationTimes.FORMAT;

            String selectedLab = (String) labBox.getSelectedItem();
            long laboratoryId = Long.parseLong(selectedLab.split(" - ", 2)[0]);

            LocalDateTime startTime =
                    LocalDateTime.parse(startTimeField.getText().trim(), formatter);
            LocalDateTime endTime =
                    LocalDateTime.parse(endTimeField.getText().trim(), formatter);

            reservationDao.createReservation(
                    Session.getCurrentUser().getId(),
                    laboratoryId,
                    startTime,
                    endTime,
                    purposeField.getText().trim()
            );

            JOptionPane.showMessageDialog(this, "预约提交成功，等待管理员审核。");
            loadReservationData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "预约失败：" + e.getMessage(),
                    "预约冲突或输入错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void auditSelectedReservation(String newStatus) {
        int selectedRow = reservationTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条预约记录。");
            return;
        }

        long reservationId = ((Number) reservationTable
                .getValueAt(selectedRow, 0)).longValue();

        try {
            reservationDao.auditReservation(
                    reservationId,
                    Session.getCurrentUser().getId(),
                    newStatus
            );

            String message = "APPROVED".equals(newStatus)
                    ? "预约已通过。" : "预约已拒绝。";

            JOptionPane.showMessageDialog(this, message);
            loadReservationData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "审核失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
