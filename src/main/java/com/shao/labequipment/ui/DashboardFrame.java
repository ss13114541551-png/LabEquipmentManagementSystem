package com.shao.labequipment.ui;

import com.shao.labequipment.dao.DashboardDao;
import com.shao.labequipment.entity.DashboardSummary;
import com.shao.labequipment.entity.DeviceUsage;
import com.shao.labequipment.entity.LaboratoryOverview;
import com.shao.labequipment.util.CsvExporter;
import com.shao.labequipment.util.Session;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.regex.Pattern;

public class DashboardFrame extends JFrame {
    private final JLabel laboratoryCountLabel = valueLabel();
    private final JLabel deviceCountLabel = valueLabel();
    private final JLabel pendingCountLabel = valueLabel();
    private final JLabel borrowingCountLabel = valueLabel();
    private final JTable deviceTable = table();
    private final JTable labTable = table();
    private final JTextField deviceKeyword = new JTextField(16);
    private final JTextField labKeyword = new JTextField(16);
    private final JLabel deviceRows = new JLabel();
    private final JLabel labRows = new JLabel();
    private final JComboBox<String> deviceStatus = new JComboBox<>(new String[]{"全部", "可用", "借出", "维修中", "停用"});
    private final JComboBox<String> labStatus = new JComboBox<>(new String[]{"全部", "开放", "维护中", "停用"});
    private final JButton deviceExport = new JButton("导出设备统计CSV");
    private final JButton labExport = new JButton("导出实验室概况CSV");
    private final DashboardDao dashboardDao = new DashboardDao();

    public DashboardFrame() {
        setTitle("管理员数据看板");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(1250, screen.width - 32), Math.min(700, screen.height - 32));
        setLocationRelativeTo(null);
        JButton refresh = new JButton("刷新全部数据");
        refresh.addActionListener(e -> loadDashboardData());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(refresh);
        toolbar.add(new JLabel("实验室“开放”不代表所选时段空闲；使用时长仅累计已归还记录。"));
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
        cards.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        cards.add(card("实验室总数", laboratoryCountLabel, new Color(66, 133, 244)));
        cards.add(card("设备总数", deviceCountLabel, new Color(52, 168, 83)));
        cards.add(card("待审核预约", pendingCountLabel, new Color(210, 145, 0)));
        cards.add(card("活跃借用", borrowingCountLabel, new Color(210, 65, 55)));
        JPanel header = new JPanel(new BorderLayout());
        header.add(toolbar, BorderLayout.NORTH); header.add(cards, BorderLayout.CENTER);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("全部设备使用统计", dataPage(deviceTable, deviceKeyword, deviceStatus, deviceExport, deviceRows));
        tabs.addTab("全部实验室概况", dataPage(labTable, labKeyword, labStatus, labExport, labRows));
        add(header, BorderLayout.NORTH); add(tabs, BorderLayout.CENTER);
        deviceExport.addActionListener(e -> CsvExporter.export(this, deviceTable, "设备使用统计.csv"));
        labExport.addActionListener(e -> CsvExporter.export(this, labTable, "实验室概况.csv"));
        watch(deviceKeyword, () -> filter(deviceTable, deviceKeyword, deviceStatus, 4, deviceRows));
        watch(labKeyword, () -> filter(labTable, labKeyword, labStatus, 4, labRows));
        deviceStatus.addActionListener(e -> filter(deviceTable, deviceKeyword, deviceStatus, 4, deviceRows));
        labStatus.addActionListener(e -> filter(labTable, labKeyword, labStatus, 4, labRows));
        Session.bind(this, () -> {
            var user = Session.getCurrentUser();
            if (user == null || !user.canViewDashboard()) { dispose(); return; }
            deviceExport.setEnabled(user.canExport()); labExport.setEnabled(user.canExport());
        });
        loadDashboardData();
    }

    private JPanel dataPage(JTable table, JTextField keyword, JComboBox<String> status, JButton export, JLabel rows) {
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        tools.add(new JLabel("关键词：")); tools.add(keyword);
        tools.add(new JLabel("状态：")); tools.add(status); tools.add(export); tools.add(rows);
        JPanel p = new JPanel(new BorderLayout()); p.add(tools, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER); return p;
    }
    private static JTable table() {
        JTable t = new JTable(); t.setRowHeight(28); t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true); return t;
    }
    private JLabel valueLabel() {
        JLabel l = new JLabel("0", SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 28f)); l.setForeground(Color.WHITE); return l;
    }
    private JPanel card(String title, JLabel value, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 6)); p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        JLabel label = new JLabel(title, SwingConstants.CENTER); label.setForeground(Color.WHITE);
        p.add(label, BorderLayout.NORTH); p.add(value, BorderLayout.CENTER); return p;
    }
    private DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (getRowCount() == 0) return Object.class;
                Object value = getValueAt(0, col); return value == null ? Object.class : value.getClass();
            }
        };
    }
    private String status(String code, boolean lab) {
        return switch(code) {
            case "AVAILABLE" -> lab ? "开放" : "可用";
            case "BORROWED" -> "借出";
            case "MAINTENANCE" -> lab ? "维护中" : "维修中";
            case "DISABLED" -> "停用";
            default -> code;
        };
    }
    private void loadDashboardData() {
        try {
            DashboardSummary summary = dashboardDao.getSummary();
            DefaultTableModel devices = model("设备编号", "设备名称", "分类", "所属实验室", "当前状态", "借用次数", "累计使用分钟");
            for (DeviceUsage d : dashboardDao.findDeviceUsage()) devices.addRow(new Object[]{
                    d.getDeviceNo(), d.getDeviceName(), d.getCategoryName(), d.getLabName(),
                    status(d.getStatus(), false), d.getBorrowCount(), d.getTotalBorrowMinutes()});
            DefaultTableModel labs = model("实验室编号", "实验室名称", "地点", "容量", "开放状态", "设备总数", "可用设备", "借出设备", "维修设备", "停用设备", "待审核预约");
            for (LaboratoryOverview l : dashboardDao.findLaboratoryOverview()) labs.addRow(new Object[]{
                    l.labCode(), l.labName(), l.location(), l.capacity(), status(l.status(), true),
                    l.deviceCount(), l.availableCount(), l.borrowedCount(), l.maintenanceCount(),
                    l.disabledCount(), l.pendingReservationCount()});
            deviceTable.setModel(devices); labTable.setModel(labs);
            laboratoryCountLabel.setText(String.valueOf(summary.getLaboratoryCount()));
            deviceCountLabel.setText(String.valueOf(summary.getDeviceCount()));
            pendingCountLabel.setText(String.valueOf(summary.getPendingReservationCount()));
            borrowingCountLabel.setText(String.valueOf(summary.getBorrowingCount()));
            filter(deviceTable, deviceKeyword, deviceStatus, 4, deviceRows);
            filter(labTable, labKeyword, labStatus, 4, labRows);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "读取看板失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    @SuppressWarnings("unchecked")
    private void filter(JTable table, JTextField keyword, JComboBox<String> status, int statusColumn, JLabel count) {
        if (table.getColumnCount() == 0) return;
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        java.util.List<RowFilter<DefaultTableModel, Integer>> filters = new java.util.ArrayList<>();
        if (!keyword.getText().isBlank()) filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(keyword.getText().trim())));
        if (status.getSelectedIndex() > 0) filters.add(RowFilter.regexFilter("^" + Pattern.quote((String) status.getSelectedItem()) + "$", statusColumn));
        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
        count.setText("显示 " + table.getRowCount() + " / " + table.getModel().getRowCount() + " 条");
    }
    private void watch(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { action.run(); }
            public void removeUpdate(DocumentEvent e) { action.run(); }
            public void changedUpdate(DocumentEvent e) { action.run(); }
        });
    }
}
