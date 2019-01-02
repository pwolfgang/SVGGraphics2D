/*
 * Copyright (C) 2018 Paul
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

import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_BEVEL;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.BasicStroke.JOIN_ROUND;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.awt.geom.PathIterator;
import java.util.StringJoiner;

/**
 * This class is a minimal implementation of the Graphics2D class that produces
 * a SVG Document. This class only supports the following methods:
 * <ul>
 * <li>draw</li>
 * <li>fill</li>
 * <li>drawGlyphVector</li>
 * <li>drawString</li>
 * <li>setColor</li>
 * <li>getColor</li>
 * <li>setFont</li>
 * <li>getFont</li>
 * <li>getStroke</li>
 * <li>setStroke</li>
 * </ul>
 * @author Paul
 */
public class SVGGraphics2D extends Graphics2D {
    
    private Font font;
    private final Canvas canvas = new Canvas();
    private Color color;
    private BasicStroke stroke;
    
    /**
     * The XML Document to be generated.
     */
    private final StringBuilder stb = new StringBuilder();
    
    public SVGGraphics2D() {
        stb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        stb.append("<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'\n" +
"          'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>\n");
        stb.append("<svg>\n");
        stb.append("<g>\n");
        color = Color.BLACK;
        stroke = new BasicStroke();
        font = new Font("Dialog", Font.PLAIN, 12);

    }
    
    public void close() {
        stb.append("</g>\n");
        stb.append("</svg>\n");
    }
    
    @Override
    public String toString() {
        return stb.toString();
    }
    
    private String getColorString() {
        return String.format("#%02x%02x%02x", 
                color.getRed(), color.getGreen(), color.getBlue());
    }
    
    private String getStrokeString() {
        String strokeColor = String.format("stroke:%s;", getColorString());
        String strokeWidth = String.format("stroke-width:%.1f;", stroke.getLineWidth());
        String dasharray = getDashArray();
        String lineCap = getLineCap();
        String lineJoin = getLineJoin();
        String miterLimit = String.format("stroke-miterlimit:%.1f;", stroke.getMiterLimit());
        return String.format("%s %s %s %s %s %s", strokeColor, strokeWidth, 
                dasharray, lineCap, lineJoin, miterLimit);
    }
    
    private String getDashArray() {
        float[] dashArray = stroke.getDashArray();
        if (dashArray == null || dashArray.length == 0) {
            return "";
        }
        StringJoiner sj = new StringJoiner(", ");
        for (float f : dashArray) {
            sj.add(String.format("%.1f", f));
        }
        return String.format("string-dasharray:%s;", sj.toString());
    }
    
    private String getLineCap() {
        int lineCap = stroke.getEndCap();
        switch (lineCap) {
            case CAP_BUTT:
                return "stroke-endcap:butt;";
            case CAP_ROUND:
                return "stroke_endcap:round;";
            case CAP_SQUARE:
                return "stroke_endcap:square;";
        }
        return "";
    }
    
    private String getLineJoin() {
        int lineJoin = stroke.getLineJoin();
        switch (lineJoin) {
            case JOIN_BEVEL:
                return "stroke-linejoin:bevel;";
            case JOIN_MITER:
                return "stroke-linejoin:miter;";
            case JOIN_ROUND:
                return "stroke-linejoin:round;";
        }
        return "";
    }

    @Override
    public void draw(Shape arg0) {
        drawOrFillPath(arg0, "none", getStrokeString());
    }

    @Override
    public boolean drawImage(Image arg0, AffineTransform arg1, ImageObserver arg2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawImage(BufferedImage arg0, BufferedImageOp arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawRenderedImage(RenderedImage arg0, AffineTransform arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawRenderableImage(RenderableImage arg0, AffineTransform arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawString(String s, int x, int y) {
        drawString(s, (float)x, (float)y);
    }

    @Override
    public void drawString(String s, float x, float y) {
        var frc = getFontRenderContext();
        var textLayout = new TextLayout(s, font, frc);
        textLayout.draw(this, x, y);      
    }

    @Override
    public void drawString(AttributedCharacterIterator arg0, int x, int y) {
        drawString(arg0, (float)x, (float)y);
    }

    @Override
    public void drawString(AttributedCharacterIterator arg0, float x, float y) {
        var frc = getFontRenderContext();
        var textLayout = new TextLayout(arg0, frc);
        textLayout.draw(this, x, y);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Shape outline = g.getOutline(x, y);
        fill(outline);
    }

    @Override
    public void fill(Shape arg0) {
        drawOrFillPath(arg0, getColorString(), "stroke:none;"); 
    }

    private void drawOrFillPath(Shape arg0, String fill, String stroke) {
        stb.append(String.format("<path style=\"%s fill:%s\"%nd=\"", stroke, fill));
        StringJoiner sj = new StringJoiner(", ");
        PathIterator pathIterator = arg0.getPathIterator(null);
        float[] coords = new float[6];
        while (!pathIterator.isDone()) {
            int segType = pathIterator.currentSegment(coords);
            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    sj.add(String.format("M %f %f", coords[0], coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    sj.add(String.format("L %f %f", coords[0], coords[1]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    sj.add(String.format("C %f %f %f %f %f %f",
                            coords[0], coords[1], coords[2], coords[3],
                            coords[4], coords[5]));
                    break;
                case PathIterator.SEG_QUADTO:
                    sj.add(String.format("Q %f %f %f %f%n",
                            coords[0], coords[1], coords[2], coords[3]));
                    break;
                case PathIterator.SEG_CLOSE:
                    sj.add("Z");
                    break;
            }
            pathIterator.next();
        }
        stb.append(sj.toString());
        stb.append("\"/>\n");
    }

    @Override
    public boolean hit(Rectangle arg0, Shape arg1, boolean arg2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setComposite(Composite arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPaint(Paint arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStroke(Stroke arg0) {
        if (arg0 instanceof BasicStroke) {
            this.stroke = (BasicStroke)arg0;
        } else {
            throw new RuntimeException(arg0.getClass().getSimpleName() + " not supported");
        }
    }

    @Override
    public void setRenderingHint(RenderingHints.Key arg0, Object arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRenderingHints(Map<?, ?> arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addRenderingHints(Map<?, ?> arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RenderingHints getRenderingHints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void translate(int arg0, int arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void translate(double arg0, double arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rotate(double arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rotate(double arg0, double arg1, double arg2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scale(double arg0, double arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void shear(double arg0, double arg1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void transform(AffineTransform arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTransform(AffineTransform arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AffineTransform getTransform() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Paint getPaint() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Composite getComposite() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBackground(Color arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Color getBackground() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void clip(Shape arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return canvas.getFontMetrics(font).getFontRenderContext();
    }

    @Override
    public Graphics create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color arg0) {
        color = arg0;
    }

    @Override
    public void setPaintMode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setXORMode(Color arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font arg0) {
        font = arg0;
    }

    @Override
    public FontMetrics getFontMetrics(Font arg0) {
        return canvas.getFontMetrics(arg0);
    }

    @Override
    public Rectangle getClipBounds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clipRect(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setClip(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Shape getClip() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setClip(Shape arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawLine(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fillRect(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearRect(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fillRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawOval(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fillOval(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fillArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawPolyline(int[] arg0, int[] arg1, int arg2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawPolygon(int[] arg0, int[] arg1, int arg2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fillPolygon(int[] arg0, int[] arg1, int arg2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, ImageObserver arg5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, Color arg3, ImageObserver arg4) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, Color arg5, ImageObserver arg6) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, ImageObserver arg9) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, Color arg9, ImageObserver arg10) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
