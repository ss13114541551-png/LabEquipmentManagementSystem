package com.shao.labequipment.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class CaptchaPanel extends JPanel {

    private static final String CHARACTERS =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final Random random = new Random();
    private String code;

    public CaptchaPanel() {
        setPreferredSize(new Dimension(100, 35));
        refresh();
    }

    public String getCode() {
        return code;
    }

    public void refresh() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            builder.append(
                    CHARACTERS.charAt(random.nextInt(CHARACTERS.length()))
            );
        }

        code = builder.toString();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;
        g.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(
                    random.nextInt(180),
                    random.nextInt(180),
                    random.nextInt(180)
            ));
            g.drawLine(
                    random.nextInt(getWidth()), random.nextInt(getHeight()),
                    random.nextInt(getWidth()), random.nextInt(getHeight())
            );
        }

        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.setColor(new Color(40, 80, 160));
        g.drawString(code, 14, 25);
    }
}