package com.shao.labequipment.ui;

import com.shao.labequipment.dao.BorrowRecordDao;
import com.shao.labequipment.entity.BorrowRecord;
import com.shao.labequipment.util.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BorrowFrame extends JFrame {

    private final JTable borrowTable = new JTable();
    private final BorrowRecordDao borrowRecordDao = new BorrowRecordDao();
    private boolean managerView = Session.getCurrentUser().hasPermission("BORROW_MANAGE");
    private boolean updatingFilters;
    private final JComboBox<String> filterBox;
    private final JLabel reminderCountLabel = new JLabel();

    public BorrowFrame() {
        setTitle("设备借还管理");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        filterBox = managerView
                ? new JComboBox<>(new String[]{"全部借用记录", "全部逾期记录"})
                : new JComboBox<>(new String[]{
                "我的全部记录", "我的逾期记录", "未来24小时内到期"
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("刷新记录");
        refreshButton.addActionListener(e -> loadBorrowData());

        JButton borrowButton = new JButton("借用设备");
        borrowButton.addActionListener(e -> showBorrowDialog());

        JButton returnButton = new JButton("归还选中设备");
        returnButton.addActionListener(e -> returnSelectedDevice());

        filterBox.addActionListener(e -> { if (!updatingFilters) loadBorrowData(); });
        reminderCountLabel.setForeground(new Color(180, 0, 0));
        reminderCountLabel.setFont(
                reminderCountLabel.getFont().deriveFont(Font.BOLD)
        );

        topPanel.add(refreshButton);
        topPanel.add(borrowButton);
        topPanel.add(returnButton);
        topPanel.add(new JLabel("记录范围："));
        topPanel.add(filterBox);
        topPanel.add(reminderCountLabel);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(borrowTable), BorderLayout.CENTER);
        Session.bind(this, () -> {
            var user = Session.getCurrentUser();
            if (user == null) { dispose(); return; }
            boolean newManagerView = user.hasPermission("BORROW_MANAGE");
            if (newManagerView != managerView) {
                managerView = newManagerView;
                updatingFilters = true;
                filterBox.removeAllItems();
                String[] options = managerView
                        ? new String[]{"全部借用记录", "全部逾期记录"}
                        : new String[]{"我的全部记录", "我的逾期记录", "未来24小时内到期"};
                for (String option : options) filterBox.addItem(option);
                updatingFilters = false;
                loadBorrowData();
            }
        });

        borrowTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                boolean overdue = "已逾期".equals(
                        table.getModel().getValueAt(row, 7)
                );
                boolean dueSoon = "24小时内到期".equals(
                        table.getModel().getValueAt(row, 7)
                );

                if (overdue) {
                    component.setBackground(isSelected
                            ? new Color(190, 55, 55)
                            : new Color(255, 220, 220));
                    component.setForeground(isSelected ? Color.WHITE : new Color(150, 0, 0));
                } else if (dueSoon) {
                    component.setBackground(isSelected
                            ? new Color(210, 135, 35)
                            : new Color(255, 238, 190));
                    component.setForeground(isSelected
                            ? Color.WHITE
                            : new Color(130, 75, 0));
                } else {
                    component.setBackground(isSelected
                            ? table.getSelectionBackground()
                            : table.getBackground());
                    component.setForeground(isSelected
                            ? table.getSelectionForeground()
                            : table.getForeground());
                }

                return component;
            }
        });

        loadBorrowData();
    }

    private void loadBorrowData() {
        String[] columns = {
                "ID", "借用编号", "借用人", "设备名称",
                "借出时间", "预计归还时间", "状态", "逾期状态"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            int overdueCount = 0;
            int dueSoonCount = 0;
            List<BorrowRecord> records = managerView
                    ? borrowRecordDao.findAll()
                    : borrowRecordDao.findByUserId(
                    Session.getCurrentUser().getId()
            );

            for (BorrowRecord record : records) {
                boolean overdue = record.isOverdue();
                boolean dueSoon = record.isDueSoon();

                if (overdue) {
                    overdueCount++;
                }

                if (dueSoon) {
                    dueSoonCount++;
                }

                int filterIndex = filterBox.getSelectedIndex();
                if (filterIndex == 1 && !overdue) {
                    continue;
                }
                if (!managerView && filterIndex == 2 && !dueSoon) {
                    continue;
                }

                model.addRow(new Object[]{
                        record.getId(),
                        record.getBorrowNo(),
                        record.getBorrowerName(),
                        record.getDeviceName(),
                        record.getBorrowTimeText(),
                        record.getExpectedReturnTimeText(),
                        record.getStatus(),
                        overdue ? "已逾期"
                                : dueSoon ? "24小时内到期" : "正常"
                });
            }

            borrowTable.setModel(model);
            if (managerView) {
                reminderCountLabel.setText("全部逾期数量：" + overdueCount);
            } else {
                reminderCountLabel.setText(
                        "我的逾期：" + overdueCount
                                + "　24小时内到期：" + dueSoonCount
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "读取借还记录失败：" + e.getMessage());
        }
    }

    private void showBorrowDialog() {
        JComboBox<String> deviceBox = new JComboBox<>();

        try {
            for (String option : borrowRecordDao.findAvailableDeviceOptions()) {
                deviceBox.addItem(option);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "读取可借设备失败：" + e.getMessage());
            return;
        }

        JTextField expectedTimeField = new JTextField(
                LocalDateTime.now()
                        .plusHours(2)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("设备："));
        panel.add(deviceBox);
        panel.add(new JLabel("预计归还时间："));
        panel.add(expectedTimeField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "借用设备",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String selectedDevice = (String) deviceBox.getSelectedItem();
            long deviceId = Long.parseLong(selectedDevice.split(" - ", 2)[0]);

            LocalDateTime expectedReturnTime = LocalDateTime.parse(
                    expectedTimeField.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );

            borrowRecordDao.borrowDevice(
                    Session.getCurrentUser().getId(),
                    deviceId,
                    expectedReturnTime
            );

            JOptionPane.showMessageDialog(
                    this,
                    "借用成功。"
            );

            loadBorrowData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "借用失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void returnSelectedDevice() {
        int selectedRow = borrowTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条借用记录。");
            return;
        }

        long borrowRecordId = ((Number) borrowTable
                .getValueAt(selectedRow, 0)).longValue();

        JCheckBox repairBox = new JCheckBox("设备存在故障，需要报修");
        JTextArea faultArea = new JTextArea(4, 28);
        faultArea.setLineWrap(true);
        faultArea.setWrapStyleWord(true);
        faultArea.setEnabled(false);
        faultArea.setBackground(UIManager.getColor("TextField.inactiveBackground"));

        repairBox.addActionListener(e -> {
            faultArea.setEnabled(repairBox.isSelected());
            faultArea.setBackground(repairBox.isSelected()
                    ? Color.WHITE
                    : UIManager.getColor("TextField.inactiveBackground"));
            if (repairBox.isSelected()) {
                faultArea.requestFocusInWindow();
            }
        });

        JPanel returnPanel = new JPanel(new BorderLayout(8, 8));
        returnPanel.add(repairBox, BorderLayout.NORTH);
        returnPanel.add(new JScrollPane(faultArea), BorderLayout.CENTER);
        returnPanel.add(
                new JLabel("如需报修，请填写具体故障现象。"),
                BorderLayout.SOUTH
        );

        int option = JOptionPane.showConfirmDialog(
                this,
                returnPanel,
                "归还设备",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        boolean needRepair = repairBox.isSelected();
        String faultDescription = faultArea.getText().trim();

        if (needRepair && faultDescription.isEmpty()) {
            JOptionPane.showMessageDialog(this, "需要报修时必须填写故障说明。");
            return;
        }

        try {
            borrowRecordDao.returnDevice(
                    borrowRecordId,
                    Session.getCurrentUser().getId(),
                    needRepair,
                    faultDescription
            );

            JOptionPane.showMessageDialog(
                    this,
                    needRepair
                            ? "归还成功，故障报修记录已提交。"
                            : "归还成功。"
            );

            loadBorrowData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "归还失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
