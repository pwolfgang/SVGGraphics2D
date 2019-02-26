/*
 * Copyright (C) 2019 Paul
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pwolfgang.svggraphics2d;

import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Paul
 */
public class VisualTest {

    private static class TestPicture extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(10, 10);
            g2d.scale(10, 10);
            g2d.drawLine(0, 0, 4, 3);
            g2d.translate(20, 0);
            g2d.drawRect(0, 0, 4, 3);
            g2d.translate(20, 0);
            g2d.drawOval(0, 0, 4, 3);
            g2d.translate(-40, 20);
            g2d.drawArc(0, 0, 4, 3, 90, 120);
            g2d.translate(20, 0);
            int[] x = new int[]{0, 4, 4, 0};
            int[] y = new int[]{0, 0, 3, 3};
            g2d.drawPolyline(x, y, 4);
            g2d.translate(20, 0);
            g2d.drawPolygon(x, y, 4);
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        TestPicture panel = new TestPicture();
        panel.setPreferredSize(new Dimension(100, 100));
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        SVGGraphics2D svg = new SVGGraphics2D();
        panel.paintComponent(svg);
        svg.close();
        try (FileWriter out = new FileWriter("test.svg");  
                PrintWriter pw = new PrintWriter(out)) {
            pw.print(svg.toString());

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
