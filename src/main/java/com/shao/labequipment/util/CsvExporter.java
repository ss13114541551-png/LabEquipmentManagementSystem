package com.shao.labequipment.util;

import javax.swing.*;
import java.awt.Component;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Locale;

public final class CsvExporter {
    private CsvExporter() { }

    public static void export(Component parent, JTable table, String filename) {
        try {
            Session.requirePermission("REPORT_EXPORT");
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File(filename));
            if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;
            Path path = chooser.getSelectedFile().toPath().toAbsolutePath();
            if (!path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".csv")) {
                path = path.resolveSibling(path.getFileName() + ".csv");
            }
            if (Files.exists(path) && JOptionPane.showConfirmDialog(parent,
                    "文件已存在，是否覆盖？", "确认覆盖", JOptionPane.YES_NO_OPTION)
                    != JOptionPane.YES_OPTION) return;
            Session.requirePermission("REPORT_EXPORT");
            writeTable(path, table);
            JOptionPane.showMessageDialog(parent, "已导出 " + table.getRowCount() + " 条记录：\n" + path);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "导出失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void writeTable(Path path, JTable table) throws Exception {
        Path temp = Files.createTempFile(path.toAbsolutePath().getParent(), "lab-export-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                writer.write('\uFEFF');
                for (int col = 0; col < table.getColumnCount(); col++) {
                    if (col > 0) writer.write(',');
                    writer.write(cell(table.getColumnName(col)));
                }
                writer.write("\r\n");
                for (int row = 0; row < table.getRowCount(); row++) {
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        if (col > 0) writer.write(',');
                        writer.write(cell(table.getValueAt(row, col)));
                    }
                    writer.write("\r\n");
                }
            }
            try { Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException e) { Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }

    private static String cell(Object value) {
        String text = value == null ? "" : value.toString();
        if (value instanceof String && text.matches("^[=+@\\-].*")) text = "'" + text;
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
