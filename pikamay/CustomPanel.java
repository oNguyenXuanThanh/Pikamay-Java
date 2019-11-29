/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pikamay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public class CustomPanel extends JPanel {

    public Point[] points;
    public boolean hasBackground = false;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(3));
        if (hasBackground) {
            g2.drawImage(Toolkit.getDefaultToolkit().getImage("./src/img/background.png"), 0, 0, getParent());
        }
        if (points != null) {
            for (int i = 0; i < points.length - 1; i++) {
                g2.drawLine(points[i].x, points[i].y, points[i + 1].x, points[i + 1].y);
            }
        }
    }

}
