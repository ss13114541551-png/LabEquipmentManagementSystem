package com.shao.labequipment.ui;

import com.shao.labequipment.dao.DeviceDao;
import com.shao.labequipment.entity.Device;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import com.shao.labequipment.entity.LoginUser;
import com.shao.labequipment.util.Session;
import com.shao.labequipment.util.CsvExporter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private final JTextField deviceNoField = new JTextField(10);
    private final JTextField deviceNameField = new JTextField(12);

    private final JComboBox<String> categoryBox = new JComboBox<>(
            new String[]{"全部"}
    );

    private final JComboBox<String> statusBox = new JComboBox<>(
            new String[]{"全部", "AVAILABLE", "BORROWED", "MAINTENANCE", "DISABLED"}
    );

    private final JTable deviceTable = new JTable();
    private final DeviceDao deviceDao = new DeviceDao();
    private boolean refreshingPermissions;

    public MainFrame() {
        setTitle("实验室设备预约、借用与资源管理系统");
        setSize(1050, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel queryPanel = new JPanel(new BorderLayout());
        queryPanel.setBorder(BorderFactory.createTitledBorder("设备查询"));
        JPanel queryFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        JPanel queryActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JPanel deviceActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        deviceActionPanel.setBorder(BorderFactory.createTitledBorder("设备操作"));
        JPanel businessPanel = new JPanel(new GridLayout(2, 4, 10, 8));
        businessPanel.setBorder(BorderFactory.createTitledBorder("业务功能"));

        queryFieldsPanel.add(new JLabel("设备编号："));
        queryFieldsPanel.add(deviceNoField);

        queryFieldsPanel.add(new JLabel("设备名称："));
        queryFieldsPanel.add(deviceNameField);

        queryFieldsPanel.add(new JLabel("设备类别："));
        queryFieldsPanel.add(categoryBox);

        queryFieldsPanel.add(new JLabel("设备状态："));
        queryFieldsPanel.add(statusBox);

        JButton searchButton = new JButton("查询");
        searchButton.addActionListener(e -> loadDeviceData());
        queryActionsPanel.add(searchButton);

        JButton resetButton = new JButton("重置");
        resetButton.addActionListener(e -> {
            deviceNoField.setText("");
            deviceNameField.setText("");
            categoryBox.setSelectedIndex(0);
            statusBox.setSelectedIndex(0);
            loadDeviceData();
        });
        queryActionsPanel.add(resetButton);

        JButton refreshButton = new JButton("刷新");
        refreshButton.setToolTipText("按照当前查询条件重新读取数据库中的最新设备信息");
        refreshButton.addActionListener(e -> loadDeviceData());
        queryActionsPanel.add(refreshButton);
        JButton permissionButton = new JButton("刷新权限");
        permissionButton.addActionListener(e -> refreshPermissions());
        queryActionsPanel.add(permissionButton);
        queryPanel.add(queryFieldsPanel, BorderLayout.CENTER);
        queryPanel.add(queryActionsPanel, BorderLayout.SOUTH);

        JButton addButton = new JButton("新增设备");
        addButton.addActionListener(e -> showAddDeviceDialog());
        deviceActionPanel.add(addButton);

        JButton editButton = new JButton("修改选中设备");
        editButton.addActionListener(e -> showEditDeviceDialog());
        deviceActionPanel.add(editButton);

        JButton deleteButton = new JButton("删除选中设备");
        deleteButton.addActionListener(e -> deleteSelectedDevice());
        deviceActionPanel.add(deleteButton);

        JButton exportButton = new JButton("导出CSV");
        exportButton.addActionListener(e -> exportToCsv());
        deviceActionPanel.add(exportButton);

        JButton reservationButton = new JButton("预约管理");
        reservationButton.addActionListener(
                e -> new ReservationFrame().setVisible(true)
        );
        businessPanel.add(reservationButton);

        JButton borrowButton = new JButton("借还管理");
        borrowButton.addActionListener(e -> new BorrowFrame().setVisible(true));
        businessPanel.add(borrowButton);

        JButton maintenanceButton = new JButton("维修管理");
        maintenanceButton.addActionListener(
                e -> openManagerWindow(MaintenanceFrame::new, "MAINTENANCE_MANAGE")
        );
        maintenanceButton.setEnabled(
                Session.getCurrentUser().canManageDevices()
        );
        businessPanel.add(maintenanceButton);

        JButton recommendButton = new JButton("智能实验室推荐");
        recommendButton.addActionListener(
                e -> new LabRecommendationFrame().setVisible(true)
        );
        businessPanel.add(recommendButton);

        JButton profileButton = new JButton("个人资料");
        profileButton.addActionListener(e -> new ProfileFrame().setVisible(true));
        businessPanel.add(profileButton);

        JButton dashboardButton = new JButton("管理员数据看板");
        dashboardButton.addActionListener(
                e -> openManagerWindow(DashboardFrame::new, null)
        );
        dashboardButton.setEnabled(Session.getCurrentUser().canManageDevices());
        businessPanel.add(dashboardButton);

        JButton auditLogButton = new JButton("操作审计日志");
        auditLogButton.addActionListener(
                e -> openManagerWindow(AuditLogFrame::new, null)
        );
        auditLogButton.setEnabled(Session.getCurrentUser().canManageDevices());
        businessPanel.add(auditLogButton);

        JButton roleButton = new JButton("用户角色授权");
        roleButton.addActionListener(e -> openManagerWindow(UserRoleFrame::new, "ROLE_MANAGE"));
        roleButton.setEnabled(Session.getCurrentUser().canManageRoles());
        businessPanel.add(roleButton);


        LoginUser currentUser = Session.getCurrentUser();

        setTitle("实验室设备预约、借用与资源管理系统 - "
                + currentUser.getRealName()
                + "（" + currentUser.getRoleName() + "）");

        addButton.setEnabled(currentUser.canManageDevices());
        editButton.setEnabled(currentUser.canManageDevices());
        deleteButton.setEnabled(currentUser.canManageDevices());
        exportButton.setEnabled(currentUser.canExport());
        Session.bind(this, () -> {
            LoginUser user = Session.getCurrentUser();
            if (user == null) {
                for (Window window : Window.getWindows()) {
                    if (window.isDisplayable()) window.dispose();
                }
                JOptionPane.showMessageDialog(null, "账号已停用或删除，请重新登录。");
                new LoginFrame().setVisible(true);
                return;
            }
            setTitle("实验室设备预约、借用与资源管理系统 - " + user.getRealName()
                    + "（" + user.getRoleName() + "）");
            addButton.setEnabled(user.hasPermission("DEVICE_ADD"));
            editButton.setEnabled(user.hasPermission("DEVICE_EDIT"));
            deleteButton.setEnabled(user.hasPermission("DEVICE_DELETE"));
            exportButton.setEnabled(user.canExport());
            maintenanceButton.setEnabled(user.hasPermission("MAINTENANCE_MANAGE"));
            dashboardButton.setEnabled(user.canViewDashboard());
            auditLogButton.setEnabled(user.canViewDashboard());
            roleButton.setEnabled(user.canManageRoles());
            recommendButton.setEnabled(true);
        });

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(queryPanel);
        topPanel.add(deviceActionPanel);
        topPanel.add(businessPanel);
        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(deviceTable), BorderLayout.CENTER);

        loadDeviceCategories();
        loadDeviceData();
        Timer permissionTimer = new Timer(3000, e -> refreshPermissions());
        permissionTimer.start();
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { permissionTimer.stop(); }
            @Override public void windowActivated(WindowEvent e) { refreshPermissions(); }
        });
    }

    private void refreshPermissions() {
        if (refreshingPermissions || Session.getCurrentUser() == null) return;
        refreshingPermissions = true;
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                Session.refreshCurrentUser(); return null;
            }
            @Override protected void done() {
                refreshingPermissions = false;
                try { get(); }
                catch (Exception e) {
                    java.util.logging.Logger.getLogger(MainFrame.class.getName())
                            .log(java.util.logging.Level.FINE, "后台权限刷新失败", e);
                }
            }
        }.execute();
    }

    private void openManagerWindow(java.util.function.Supplier<JFrame> factory, String permission) {
        try {
            if (permission == null) Session.requireManager();
            else Session.requirePermission(permission);
            factory.get().setVisible(true);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage(), "权限提示", JOptionPane.WARNING_MESSAGE); }
    }

    private void loadDeviceCategories() {
        try {
            categoryBox.removeAllItems();
            categoryBox.addItem("全部");

            for (String categoryName : deviceDao.findAllCategoryNames()) {
                categoryBox.addItem(categoryName);
            }

            categoryBox.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "读取设备类别失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadDeviceData() {
        String[] columns = {
                "ID", "设备编号", "设备名称", "设备分类", "所属实验室", "状态"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            for (Device device : deviceDao.search(
                    deviceNoField.getText().trim(),
                    deviceNameField.getText().trim(),
                    (String) categoryBox.getSelectedItem(),
                    (String) statusBox.getSelectedItem()
            )) {
                model.addRow(new Object[]{
                        device.getId(),
                        device.getDeviceNo(),
                        device.getDeviceName(),
                        device.getCategoryName(),
                        device.getLabName(),
                        device.getStatus()
                });
            }

            deviceTable.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "读取设备数据失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void showAddDeviceDialog() {
        JTextField deviceNoField = new JTextField();
        JTextField deviceNameField = new JTextField();
        JTextField categoryIdField = new JTextField();
        JTextField labIdField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();

        JComboBox<String> statusComboBox = new JComboBox<>(
                new String[]{"AVAILABLE", "MAINTENANCE", "DISABLED"}
        );

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("设备编号："));
        panel.add(deviceNoField);
        panel.add(new JLabel("设备名称："));
        panel.add(deviceNameField);
        panel.add(new JLabel("分类 ID："));
        panel.add(categoryIdField);
        panel.add(new JLabel("实验室 ID："));
        panel.add(labIdField);
        panel.add(new JLabel("品牌："));
        panel.add(brandField);
        panel.add(new JLabel("型号："));
        panel.add(modelField);
        panel.add(new JLabel("状态："));
        panel.add(statusComboBox);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "新增设备",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            deviceDao.insert(
                    deviceNoField.getText().trim(),
                    deviceNameField.getText().trim(),
                    Long.parseLong(categoryIdField.getText().trim()),
                    Long.parseLong(labIdField.getText().trim()),
                    brandField.getText().trim(),
                    modelField.getText().trim(),
                    (String) statusComboBox.getSelectedItem()
            );

            JOptionPane.showMessageDialog(this, "新增设备成功！");
            loadDeviceData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "新增失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void showEditDeviceDialog() {
        int selectedRow = deviceTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一台设备。");
            return;
        }

        long id = ((Number) deviceTable.getValueAt(selectedRow, 0)).longValue();

        JTextField deviceNameField = new JTextField(
                String.valueOf(deviceTable.getValueAt(selectedRow, 2))
        );

        JComboBox<String> statusComboBox = new JComboBox<>(
                new String[]{"AVAILABLE", "BORROWED", "MAINTENANCE", "DISABLED"}
        );
        statusComboBox.setSelectedItem(
                String.valueOf(deviceTable.getValueAt(selectedRow, 5))
        );

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("设备名称："));
        panel.add(deviceNameField);
        panel.add(new JLabel("设备状态："));
        panel.add(statusComboBox);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "修改设备",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            deviceDao.updateBasic(
                    id,
                    deviceNameField.getText().trim(),
                    (String) statusComboBox.getSelectedItem()
            );

            JOptionPane.showMessageDialog(this, "修改设备成功！");
            loadDeviceData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "修改失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void deleteSelectedDevice() {
        int selectedRow = deviceTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一台设备。");
            return;
        }

        long id = ((Number) deviceTable.getValueAt(selectedRow, 0)).longValue();
        String deviceName = String.valueOf(deviceTable.getValueAt(selectedRow, 2));

        int result = JOptionPane.showConfirmDialog(
                this,
                "确定删除设备“" + deviceName + "”吗？",
                "删除确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            deviceDao.deleteById(id);
            JOptionPane.showMessageDialog(this, "删除设备成功！");
            loadDeviceData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e instanceof java.sql.SQLIntegrityConstraintViolationException
                            ? "删除失败：该设备已有借用或维修记录，不能直接删除。"
                            : "删除失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void exportToCsv() {
        CsvExporter.export(this, deviceTable, "设备查询结果.csv");
    }

}
