/*
 * Copyright (C) 2018 Paul Wolfgang
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
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.Closeable;
import java.util.NoSuchElementException;
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
 * @author Paul Wolfgang
 */
public class SVGGraphics2D extends Graphics2D implements Closeable {
    
    private Font font;
    private final Canvas canvas = new Canvas();
    private Color color;
    private BasicStroke stroke;
    private AffineTransform transform;
    
    /**
     * The XML Document to be generated.
     */
    private final StringBuilder stb;
    
    /**
     * Construct an SVGGraphics2D object. The constructor initializes the
     * StringBuilder with the XML header and initial &lt;svg&gt; and &lt;g&gt;
     * tags. It also initializes the color, stroke, transform, and font.
     */
    public SVGGraphics2D() {
        stb = new StringBuilder();
        stb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        stb.append("<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'\n" +
"          'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>\n");
        stb.append("<svg>\n");
        stb.append("<g>\n");
        color = Color.BLACK;
        stroke = new BasicStroke();
        font = new Font("Dialog", Font.PLAIN, 12);
        transform = new AffineTransform();
    }
    
    /**
     * Close the SVGGraphics2D. Appends closing &lt;/g&gt; and &lt;svg&gt; tags.
     */
    @Override
    public void close() {
        stb.append("</g>\n");
        stb.append("</svg>\n");
    }
    
    /**
     * Returns the contents of the StringBuilder as a string.
     * @return The SVG XML data.
     */
    @Override
    public String toString() {
        return stb.toString();
    }
    
    /**
     * Construct a color string that represents the current color.
     * @return A string in the form #rrggbb
     */
    private String getColorString() {
        return String.format("#%02x%02x%02x", 
                color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * Construct the stroke attribute.
     * @return The stroke style attributes.
     */
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
    
    /**
     * Construct the dash array attribute.
     * @return string-dasharray attribute.
     */
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
    
    /**
     * Construct the line cap attribute.
     * @return stroke-endcap attribute.
     */
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
    
    /**
     * Construct the line join attribute.
     * @return stroke-linejoin attribute.
     */
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

    /**
     * Draw a shape.
     * @param s The shape to be drawn.
     */
    @Override
    public void draw(Shape s) {
        drawOrFillShape(s, "none", getStrokeString());
    }

    /**
     * Draw an image. Not supported
     * @param image
     * @param transform
     * @param observer
     * @return 
     * @throws UnsupportedOperationException.
     */
    @Override
    public boolean drawImage(Image image, AffineTransform transform, ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /**
     * Draw an image. Not supported
     * @param image
     * @param op
     * @param x
     * @param y
     * @throws UnsupportedOperationException.
     */
    @Override
    public void drawImage(BufferedImage image, BufferedImageOp op, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /**
     * Draw an image. Not supported
     * @param image
     * @param transform
     * @throws UnsupportedOperationException.
     */
    @Override
    public void drawRenderedImage(RenderedImage image, AffineTransform transform) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /**
     * Draw an image. Not supported
     * @param image
     * @param transform
     * @throws UnsupportedOperationException.
     */
    @Override
    public void drawRenderableImage(RenderableImage image, AffineTransform transform) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    /**
     * Draw a string. The baseline of the first character is at position (x, y) in the User Space.
     * @param s The string to be drawn.
     * @param x The x coordinate 
     * @param y The y coordinate
     */
    @Override
    public void drawString(String s, int x, int y) {
        drawString(s, (float)x, (float)y);
    }

    /**
     * Draw a string. The baseline of the first character is at position (x, y) in the User Space.
     * @param s The string to be drawn.
     * @param x The x coordinate 
     * @param y The y coordinate
     */
    @Override
    public void drawString(String s, float x, float y) {
        var frc = getFontRenderContext();
        var textLayout = new TextLayout(s, font, frc);
        textLayout.draw(this, x, y);      
    }

    /**
     * Draw a string. The baseline of the first character is at position 
     * (x, y) in the User Space.
     * @param s The string to be drawn.
     * @param x The x coordinate 
     * @param y The y coordinate
     */
    @Override
    public void drawString(AttributedCharacterIterator s, int x, int y) {
        drawString(s, (float)x, (float)y);
    }

    /**
     * Draw a string. The baseline of the first character is at position 
     * (x, y) in the User Space.
     * @param s The string to be drawn.
     * @param x The x coordinate 
     * @param y The y coordinate
     */
    @Override
    public void drawString(AttributedCharacterIterator s, float x, float y) {
        var frc = getFontRenderContext();
        var textLayout = new TextLayout(s, frc);
        textLayout.draw(this, x, y);
    }

    /**
     * Draw a GlyphVector.
     * @param g The GlyphVector to be drawn.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Shape outline = g.getOutline(x, y);
        fill(outline);
    }

    /**
     * Fill a shape.
     * @param s The shape to be filled. 
     */
    @Override
    public void fill(Shape s) {
        drawOrFillShape(s, getColorString(), "stroke:none;"); 
    }
    
    /**
     * Draw or fill a shape. Generates and appends to the SVG commands to draw
     * or fill an outline defined by the shape.
     * @param s The shape to be drawn or filled.
     * @param fill If true, the shape is filled.
     * @param stroke The stroke attributes for to draw the shape.
     */
    private void drawOrFillShape(Shape s, String fill, String storke) {
        drawOrFillPath(s.getPathIterator(null), fill, storke);
    }

    /**
     * Draw for fill a path. Generates and appends to the SVG commands to draw
     * or fill an outline defined by the shape.
     * @param s The shape to be drawn or filled.
     * @param fill If true, the shape is filled.
     * @param stroke The stroke attributes for to draw the shape.
     */
    private void drawOrFillPath(PathIterator pathIterator, String fill, String stroke) {
        stb.append(String.format("<path style=\"%s fill:%s\"\n%sd=\"", 
                stroke, fill, getTransformAttribute()));
        StringJoiner sj = new StringJoiner(", ");
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

    /**
     * Checks whether or not the specified Shape intersects the specified Rectangle.
     * Not supported.
     * @param arg0
     * @param arg1
     * @param arg2
     * @return
     * @throws UnsupportedOperationException.
     */
    @Override
    public boolean hit(Rectangle arg0, Shape arg1, boolean arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns the device configuration associated with this Graphics2D.
     * Not supported.
     * @return
     * @throws UnsupportedOperationException
     */
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Sets the Composite for the Graphics2D context
     * Not supported.
     * @throws UnsupportedOperationException
     */
    @Override
    public void setComposite(Composite arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    /** 
     * Sets the Paint attribute for the Graphics2D context.
     * Not supported.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void setPaint(Paint arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Sets the Stroke attribute. Only instances of BasicStroke are supported.
     * @param stroke The stroke to be set 
     * @throws RuntimeException if the stroke is not a BasicStroke instance.
     */
    @Override
    public void setStroke(Stroke stroke) {
        if (stroke instanceof BasicStroke) {
            this.stroke = (BasicStroke)stroke;
        } else {
            throw new RuntimeException(stroke.getClass().getSimpleName() + " not supported");
        }
    }

    /** 
     * Set the specified rendering hint.
     * Not supported.
     * @param arg0
     * @param arg1
     * @throws UnsupportedOperationException.
     */
    @Override
    public void setRenderingHint(RenderingHints.Key arg0, Object arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** 
     * Get the specified rendering hint.Not supported.
     * @param arg0
     * @throws UnsupportedOperationException.
     */
    @Override
    public Object getRenderingHint(RenderingHints.Key arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** 
     * Set the rendering hints.
     * Not supported.
     * @param arg0
     * @throws UnsupportedOperationException.
     */
    @Override
    public void setRenderingHints(Map<?, ?> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** 
     * Add the rendering hints.
     * Not supported.
     * @param arg0
     * @throws UnsupportedOperationException.
     */
    @Override
    public void addRenderingHints(Map<?, ?> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** 
     * Get the rendering hints.
     * Not supported.
     * @return
     * @throws UnsupportedOperationException.
     */
    @Override
    public RenderingHints getRenderingHints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Translates the origin of the Graphics2D context to the point (x, y) in 
     * the current coordinate system.
     * @param x The new x-coordinate origin
     * @param y The new y-coordinate origin
     */
    @Override
    public void translate(int x, int y) {
        transform.translate(x, y);
    }

    /**
     * Translates the origin of the Graphics2D context to the point (x, y) in 
     * the current coordinate system.
     * @param x The new x-coordinate origin
     * @param y The new y-coordinate origin
     */
    @Override
    public void translate(double x, double y) {
        transform.translate(x, y);
    }

    /**
     * Concatenates the current Graphics2D Transform with a rotation transform. 
     * Subsequent rendering is rotated by the specified radians relative to 
     * the previous origin.
     * @param theta The angle of rotation in radians. 
     */
    @Override
    public void rotate(double theta) {
        transform.rotate(theta);
    }

    /**
     * Concatenates the current Graphics2D Transform with a rotation transform. 
     * Subsequent rendering is rotated by the specified radians relative to 
     * the specified origin.
     * @param theta The angle of rotation in radians.
     * @param x The x-coordinate of the rotation point.
     * @param y The y-coordinate of the rotation point.
     */
    @Override
    public void rotate(double theta, double x, double y) {
        transform.rotate(theta, x, y);
    }

    /**
     * Apply a scale transform to the current transform.
     * @param sx The x factor.
     * @param sy The y factor
     */
    @Override
    public void scale(double sx, double sy) {
        transform.scale(sx, sy);
    }

    /**
     * Apply a shear transform to the current transform.
     * @param sx The x factor.
     * @param sy The y factor
     */
    @Override
    public void shear(double sx, double sy) {
        transform.shear(sx, sy);
    }

    /**
     * Append the provided transform to the current transform.
     * @param trans The transform to be appended to the current transform. 
     */
    @Override
    public void transform(AffineTransform trans) {
        transform.concatenate​(trans);
    }

    /**
     * Sets the transform to the provided transform.
     * @param trans 
     */
    @Override
    public void setTransform(AffineTransform trans) {
        transform = trans;
    }

    /**
     * Gets the current transform.
     * @return The current transform 
     */
    @Override
    public AffineTransform getTransform() {
        return transform;
    }
    
    /**
     * Generate the transform attribute.
     */
    private String getTransformAttribute() {
        if (transform.isIdentity()) {
            return "";
        }
        StringJoiner sj = new StringJoiner(" ", "(", ")");
        double[] matrix = new double[6];
        transform.getMatrix(matrix);
        for (double d : matrix) {
            sj.add(String.format("%.6f", d));
        }
        
        return "transform=\"matrix" + sj.toString() + "\"\n";
    }

    /**
     * Returns the current Paint of the Graphics2D context.
     * @return
     * @throws UnsupportedOperationException.
     */
    @Override
    public Paint getPaint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns the current Composite in the Graphics2D context.
     * @return
     * @throws UnsupportedOperationException.
     */
    @Override
    public Composite getComposite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Sets the Background in the Graphics2D context.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void setBackground(Color arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns the current Background in the Graphics2D context.
     * @return
     * @throws UnsupportedOperationException.
     */
    @Override
    public Color getBackground() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns the current Stroke.
     * @return the current Stroke.
     */
    @Override
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * Intersects the current Clip with the interior of the specified Shape 
     * and sets the Clip to the resulting intersection.
     * @param s the Shape to be intersected with the current Clip. 
     * If s is null, this method clears the current Clip.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void clip(Shape s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the current FontRenderingContext.
     * @return The current FontRenderingContext.
     */
    @Override
    public FontRenderContext getFontRenderContext() {
        return canvas.getFontMetrics(font).getFontRenderContext();
    }

    /**
     * Creates a new Graphics object that is a copy of this Graphics object.
     * @return A copy of this Graphics object.
     * @throws UnsupportedOperationException.
     */
    @Override
    public Graphics create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the current color.
     * @return The current color.
     */
    @Override
    public Color getColor() {
        return color;
    }

    /**
     * Set the color.
     * @param c The color to be set.
     */
    @Override
    public void setColor(Color c) {
        color = c;
    }

    /**
     * Set the paint mode.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void setPaintMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Set the XOR mode.
     * @throws UnsupportedOperationException
     */
    @Override
    public void setXORMode(Color arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the current Font.
     * @return The current font.
     */
    @Override
    public Font getFont() {
        return font;
    }

    /**
     * Set the Font
     * @param font
     */
    @Override
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Get the FontMetrics for the specified font.
     * @param font The font requested.
     * @return The FontMetrics for the specified font.
     */
    @Override
    public FontMetrics getFontMetrics(Font font) {
        return canvas.getFontMetrics(font);
    }

    /**
     * Returns the bounding rectangle of the current clipping area.
     * @return 
     * @throws UnsupportedOperationException.
     */
    @Override
    public Rectangle getClipBounds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Intersects the current clip with the specified rectangle.
     * @throws UnsupportedOperationException.
     */
    @Override
    public void clipRect(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Sets the current clip to the rectangle specified by the given coordinates. 
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @throws UnsupportedOperationException.
     */
    @Override
    public void setClip(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns the current clipping area.
     * @return 
     * @throws UnsupportedOperationException.
     */
    @Override
    public Shape getClip() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Sets the current clipping area to an arbitrary clip shape.
     * @param arg0
     * @throws UnsupportedOperationException.
     */
    @Override
    public void setClip(Shape arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Copies an area of the component
     * @param x
     * @param y
     * @param width
     * @param height
     * @param dx
     * @param dy
     * @throws UnsupportedOperationException.
     */
    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Draw a line.
     * @param x0 The x-coordinate of the origin.
     * @param y0 The y-coordinate of the origin.
     * @param x1 The x-coordinate of the destination.
     * @param y1 The y-coordinate of the destination.
     */
    @Override
    public void drawLine(int x0, int y0, int x1, int y1) {
        draw(new Line2D.Double(x0, y0, x1, y1));
    }

    /**
     * Draw a rectangle.
     * @param x0 The x-coordinate of the origin.
     * @param y0 The y-coordinate of the origin.
     * @param w The width of the rectangle
     * @param h The height of the rectangle
     */
    @Override
    public void drawRect(int x0, int y0, int w, int h) {
        draw(new Rectangle2D.Double(x0, y0, w, h));
    }

    /**
     * Fill a rectangle.
     * @param x0 The x-coordinate of the origin.
     * @param y0 The y-coordinate of the origin.
     * @param w The width of the rectangle
     * @param h The height of the rectangle
     */
    @Override
    public void fillRect(int x0, int y0, int w, int h) {
        fill(new Rectangle2D.Double(x0, y0, w, h));
    }

    /**
     * Fill a rectangle with the background color.
     * Currently the background color is set to white.
     * @param x0 The x-coordinate of the origin.
     * @param y0 The y-coordinate of the origin.
     * @param w The width of the rectangle
     * @param h The height of the rectangle
     */
    @Override
    public void clearRect(int x0, int y0, int w, int h) {
        Color currentColor = color;
        color = Color.WHITE;
        fill(new Rectangle2D.Double(x0, y0, w, h));
        color = currentColor;
    }

    /**
     * Draw a rounded rectangle.
     * @param x0 The x-coordinate of the origin.
     * @param y0 The y-coordinate of the origin.
     * @param w The width of the rectangle
     * @param h The height of the rectangle
     * @param arcw the width of the arc to use to round off the corners 
     * @param arch the height of the arc to use to round off the corners 
     */
    @Override
    public void drawRoundRect(int x0, int y0, int w, int h, int arcw, int arch) {
        draw (new RoundRectangle2D.Double(x0, y0, w, h, arcw, arch));
    }

    /**
     * Fill a rounded rectangle with the current color.
     * @param x0 The x-coordinate of the origin.
     * @param y0 The y-coordinate of the origin.
     * @param w The width of the rectangle
     * @param h The height of the rectangle
     * @param arcw the width of the arc to use to round off the corners 
     * @param arch the height of the arc to use to round off the corners 
     */
    @Override
    public void fillRoundRect(int x0, int y0, int w, int h, int arcw, int arch) {
        fill (new RoundRectangle2D.Double(x0, y0, w, h, arcw, arch));
    }


    /**
     * Draw an ellipse that fits within the specified rectangle.
     * @param x The x-coordinate of the upper left corner
     * @param y The y-coordinate of the upper left corner
     * @param w The width
     * @param h The height
     */
    @Override
    public void drawOval(int x, int y, int w, int h) {
        draw(new Ellipse2D.Double(x, y, w, h));
    }

    /**
     * Fill an ellipse that fits within the specified rectangle.
     * @param x The x-coordinate of the upper left corner
     * @param y The y-coordinate of the upper left corner
     * @param w The width
     * @param h The height
     */
    @Override
    public void fillOval(int x, int y, int w, int h) {
        fill(new Ellipse2D.Double(x, y, w, h));
    }

    /**
     * Draw the outline of a circular or elliptical arc covering the specified rectangle. 
     * @param x x-coordinate of upper-left bounding rectangle
     * @param y y-coordinate of upper-left bounding rectangle
     * @param w width of bounding rectangle
     * @param h height of bounding rectangle
     * @param start start angle 
     * @param extent angular extent of the arc, relative to the start angle 
     */
    @Override
    public void drawArc(int x, int y, int w, int h, int start, int extent) {
        draw(new Arc2D.Double(x, y, w, h, start, extent, Arc2D.OPEN));
    }

    /**
     * Fill the outline of a circular or elliptical arc covering the specified rectangle. 
     * @param x x-coordinate of upper-left bounding rectangle
     * @param y y-coordinate of upper-left bounding rectangle
     * @param w width of bounding rectangle
     * @param h height of bounding rectangle
     * @param start start angle 
     * @param extent angular extent of the arc, relative to the start angle 
     */
    @Override
    public void fillArc(int x, int y, int w, int h, int start, int extent) {
        fill(new Arc2D.Double(x, y, w, h, start, extent, Arc2D.CHORD));
    }

    /**
     * Draw a sequence of connected lines defined by arrays of x and y coordinates.
     * @param x The x-coordinates
     * @param y The y-coordinates
     * @param num The number of points. 
     */
    @Override
    public void drawPolyline(int[] x, int[] y, int num) {
        PathIterator itr = new Path(x, y, num);
        drawOrFillPath(itr, "none", getStrokeString());
    }

    /**
     * Draw a closed polygon defined by arrays of x and y coordinates..
     * @param x The x-coordinates
     * @param y The y-coordinates
     * @param num The number of points. 
     */
    @Override
    public void drawPolygon(int[] x, int[] y, int num) {
        PathIterator itr = new Path(x, y, num, true);
        drawOrFillPath(itr, "none", getStrokeString());   
    }

    /**
     * Fills a closed polygon defined by arrays of x and y coordinates..
     * @param x The x-coordinates
     * @param y The y-coordinates
     * @param num The number of points. 
     */
    @Override
    public void fillPolygon(int[] x, int[] y, int num) {
        PathIterator itr = new Path(x, y, num, true);
        drawOrFillPath(itr, getColorString(), "stroke:none;");   
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, ImageObserver arg5) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, Color arg3, ImageObserver arg4) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, Color arg5, ImageObserver arg6) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, ImageObserver arg9) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, Color arg9, ImageObserver arg10) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    public static class Path implements PathIterator {
        
        private final int[] x;
        private final int[] y;
        private int numPoints;
        boolean closed;
        private int index;
        boolean done;
        
        public Path(int[] x, int[] y, int numPoints, boolean closed) {
            this.x = x;
            this.y = y;
            this.numPoints = numPoints;
            this.closed = closed;
            if (closed && x[0] == x[numPoints-1] && y[0] == y[numPoints-1]) {
                this.numPoints--;
            }
            done = false;
            index = 0;
        }

        public Path(int[] x, int[] y, int numPoints) {
            this.x = x;
            this.y = y;
            this.numPoints = numPoints;
            this.closed = x[0] == x[numPoints-1] && y[0] == y[numPoints-1];
            if (closed) {
                this.numPoints--;
            }
            done = false;
            index = 0;
        }

        @Override
        public int getWindingRule() {
            return WIND_EVEN_ODD;
        }

        @Override
        public boolean isDone() {
            if (done) {
                return done;
            }
            if (closed) {
                done = index==numPoints+1;
            } else {
                done = index==numPoints;
            }
            return done;
        }

        @Override
        public void next() {
            if (!done) {
                index++;
            }
        }

        @Override
        public int currentSegment(float[] coords) {
            double[] doubleCoords = new double[6];
            int currentSeg = currentSegment(doubleCoords);
            for (int i = 0; i < 6; i++) {
                coords[i] = (float)doubleCoords[i];
            }
            return currentSeg;
        }

        @Override
        public int currentSegment(double[] coords) {
            if (done) {
                throw new NoSuchElementException();
            }
            if (index == 0) {
                coords[0] = x[0];
                coords[1] = y[0];
                return SEG_MOVETO;
            }
            if (index < numPoints) {
                coords[0] = x[index];
                coords[1] = y[index];
                return SEG_LINETO;
            }
            if (index == numPoints && closed) {
                return SEG_CLOSE;
            }
            throw new NoSuchElementException();
        }
        
    }
    
}
