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

import org.junit.Test;
import static org.junit.Assert.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Paul
 */
public class SVGGraphics2DTest {
    
    public SVGGraphics2DTest() {
    }


    /**
     * Test of draw method, of class SVGGraphics2D.
     */
    @Test
    public void testDraw() {
        var graphics = new SVGGraphics2D();
        var rect = new Rectangle2D.Double(1, 1, 3, 4);
        graphics.draw(rect);
        graphics.close();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'\n" +
"          'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>\n" +
"<svg>\n" +
"<g>\n" +
"<path style=\"stroke:#000000; stroke-width:1.0;  stroke_endcap:square; stroke-linejoin:miter; stroke-miterlimit:10.0; fill:none\"\n" +
"d=\"M 1.000000 1.000000, L 4.000000 1.000000, L 4.000000 5.000000, L 1.000000 5.000000, L 1.000000 1.000000, Z\"/>\n" +
"</g>\n" +
"</svg>\n" +
"";
        assertEquals(expected, graphics.toString());
    }
    
    /**
     * Test of fill method, of class SVGGraphics2D.
     */
    @Test
    public void testFill() {
        var graphics = new SVGGraphics2D();
        var rect = new Rectangle2D.Double(1, 1, 3, 4);
        graphics.fill(rect);
        graphics.close();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'\n" +
"          'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>\n" +
"<svg>\n" +
"<g>\n" +
"<path style=\"stroke:none; fill:#000000\"\n" +
"d=\"M 1.000000 1.000000, L 4.000000 1.000000, L 4.000000 5.000000, L 1.000000 5.000000, L 1.000000 1.000000, Z\"/>\n" +
"</g>\n" +
"</svg>\n" +
"";
        assertEquals(expected, graphics.toString());
    }

    /**
     * Test of fill method, of class SVGGraphics2D.
     */
    @Test
    public void testUseTransform() {
        var graphics = new SVGGraphics2D();
        graphics.translate(10, 5);
        var rect = new Rectangle2D.Double(1, 1, 3, 4);
        graphics.fill(rect);
        graphics.close();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'\n" +
"          'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>\n" +
"<svg>\n" +
"<g>\n" +
"<path style=\"stroke:none; fill:#000000\"\n" +
"transform=\"matrix(1.000000 0.000000 0.000000 1.000000 10.000000 5.000000)\"\n" +
"d=\"M 1.000000 1.000000, L 4.000000 1.000000, L 4.000000 5.000000, L 1.000000 5.000000, L 1.000000 1.000000, Z\"/>\n" +
"</g>\n" +
"</svg>\n" +
"";
        assertEquals(expected, graphics.toString());
    }

    /**
     * Test of setPaint method, of class SVGGraphics2D.
     */
    @Test
    public void testSetPaint() {
    }

    /**
     * Test of setStroke method, of class SVGGraphics2D.
     */
    @Test
    public void testSetStroke() {
    }



    /**
     * Test of translate method, of class SVGGraphics2D.
     */
    @Test
    public void testTranslate_int_int() {
    }

    /**
     * Test of translate method, of class SVGGraphics2D.
     */
    @Test
    public void testTranslate_double_double() {
    }

    /**
     * Test of rotate method, of class SVGGraphics2D.
     */
    @Test
    public void testRotate_double() {
    }

    /**
     * Test of rotate method, of class SVGGraphics2D.
     */
    @Test
    public void testRotate_3args() {
    }

    /**
     * Test of scale method, of class SVGGraphics2D.
     */
    @Test
    public void testScale() {
    }

    /**
     * Test of shear method, of class SVGGraphics2D.
     */
    @Test
    public void testShear() {
    }

    /**
     * Test of transform method, of class SVGGraphics2D.
     */
    @Test
    public void testTransform() {
    }

    /**
     * Test of setTransform method, of class SVGGraphics2D.
     */
    @Test
    public void testSetTransform() {
    }

    /**
     * Test of getTransform method, of class SVGGraphics2D.
     */
    @Test
    public void testGetTransform() {
    }

    /**
     * Test of getPaint method, of class SVGGraphics2D.
     */
    @Test
    public void testGetPaint() {
    }

    /**
     * Test of setBackground method, of class SVGGraphics2D.
     */
    @Test
    public void testSetBackground() {
    }

    /**
     * Test of getBackground method, of class SVGGraphics2D.
     */
    @Test
    public void testGetBackground() {
    }

    /**
     * Test of getStroke method, of class SVGGraphics2D.
     */
    @Test
    public void testGetStroke() {
    }

    /**
     * Test of getFontRenderContext method, of class SVGGraphics2D.
     */
    @Test
    public void testGetFontRenderContext() {
    }

    /**
     * Test of create method, of class SVGGraphics2D.
     */
    @Test
    public void testCreate() {
    }

    /**
     * Test of getColor method, of class SVGGraphics2D.
     */
    @Test
    public void testGetColor() {
    }

    /**
     * Test of setColor method, of class SVGGraphics2D.
     */
    @Test
    public void testSetColor() {
    }

    /**
     * Test of setPaintMode method, of class SVGGraphics2D.
     */
    @Test
    public void testSetPaintMode() {
    }

    /**
     * Test of setXORMode method, of class SVGGraphics2D.
     */
    @Test
    public void testSetXORMode() {
    }

    /**
     * Test of getFont method, of class SVGGraphics2D.
     */
    @Test
    public void testGetFont() {
    }

    /**
     * Test of setFont method, of class SVGGraphics2D.
     */
    @Test
    public void testSetFont() {
    }

    /**
     * Test of getFontMetrics method, of class SVGGraphics2D.
     */
    @Test
    public void testGetFontMetrics() {
    }

    /**
     * Test of drawLine method, of class SVGGraphics2D.
     */
    @Test
    public void testDrawLine() {
    }

    /**
     * Test of fillRect method, of class SVGGraphics2D.
     */
    @Test
    public void testFillRect() {
    }

    /**
     * Test of clearRect method, of class SVGGraphics2D.
     */
    @Test
    public void testClearRect() {
    }

    /**
     * Test of drawRoundRect method, of class SVGGraphics2D.
     */
    @Test
    public void testDrawRoundRect() {
    }

    /**
     * Test of fillRoundRect method, of class SVGGraphics2D.
     */
    @Test
    public void testFillRoundRect() {
    }

    /**
     * Test of drawOval method, of class SVGGraphics2D.
     */
    @Test
    public void testDrawOval() {
    }

    /**
     * Test of fillOval method, of class SVGGraphics2D.
     */
    @Test
    public void testFillOval() {
    }

    /**
     * Test of drawArc method, of class SVGGraphics2D.
     */
    @Test
    public void testDrawArc() {
    }

    /**
     * Test of fillArc method, of class SVGGraphics2D.
     */
    @Test
    public void testFillArc() {
    }

    /**
     * Test of drawPolyline method, of class SVGGraphics2D.
     */
    @Test
    public void testDrawPolyline() {
    }

    /**
     * Test of drawPolygon method, of class SVGGraphics2D.
     */
    @Test
    public void testDrawPolygon() {
    }

    /**
     * Test of fillPolygon method, of class SVGGraphics2D.
     */
    @Test
    public void testFillPolygon() {
    }
    
    @Test
    public void testOpenPath() {
        int[] x = {0, 3, 3, 0};
        int[] y = {0, 0, 4, 4};
        var itr = new SVGGraphics2D.Path(x, y, 4);
        int[] expected = {
            PathIterator.SEG_MOVETO,
            PathIterator.SEG_LINETO,
            PathIterator.SEG_LINETO,
            PathIterator.SEG_LINETO
        };
        double[][] expectedCoords = 
            {
                {0, 0},
                {3, 0},
                {3, 4},
                {0, 4},
                {0, 0}
            };
        int count = 0;
        while (!itr.isDone()) {
            double[] coords = new double[6];
            int retvalue = itr.currentSegment(coords);
            assertEquals(expected[count], retvalue);
            assertEquals(expectedCoords[count][0], coords[0], 1e-9);
            itr.next();
            count++;
        }
        assertEquals(4, count);
        
    }
    
    @Test
    public void testClosedPath() {
        int[] x = {0, 3, 3, 0, 0};
        int[] y = {0, 0, 4, 4, 0};
        var itr = new SVGGraphics2D.Path(x, y, 5);
        int[] expected = {
            PathIterator.SEG_MOVETO,
            PathIterator.SEG_LINETO,
            PathIterator.SEG_LINETO,
            PathIterator.SEG_LINETO,
            PathIterator.SEG_CLOSE
        };
        double[][] expectedCoords = 
            {
                {0, 0},
                {3, 0},
                {3, 4},
                {0, 4},
                {0, 0}
            };
        int count = 0;
        while (!itr.isDone()) {
            double[] coords = new double[6];
            int retvalue = itr.currentSegment(coords);
            assertEquals(expected[count], retvalue);
            assertEquals(expectedCoords[count][0], coords[0], 1e-9);
            itr.next();
            count++;
        }
        assertEquals(5, count);
        
    }

    @Test
    public void testClosedExplicitlyClosedPath() {
        int[] x = {0, 3, 3, 0};
        int[] y = {0, 0, 4, 4};
        var itr = new SVGGraphics2D.Path(x, y, 4, true);
        int[] expected = {
            PathIterator.SEG_MOVETO,
            PathIterator.SEG_LINETO,
            PathIterator.SEG_LINETO,
            PathIterator.SEG_LINETO,
            PathIterator.SEG_CLOSE
        };
        double[][] expectedCoords = 
            {
                {0, 0},
                {3, 0},
                {3, 4},
                {0, 4},
                {0, 0}
            };
        int count = 0;
        while (!itr.isDone()) {
            double[] coords = new double[6];
            int retvalue = itr.currentSegment(coords);
            assertEquals(expected[count], retvalue);
            assertEquals(expectedCoords[count][0], coords[0], 1e-9);
            itr.next();
            count++;
        }
        assertEquals(5, count);
        
    }
    
}
