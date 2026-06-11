package com.marginallyclever.reactiondiffusion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Reaction diffusion model.
 */
public class Model {
    private static final Logger logger = LoggerFactory.getLogger(Model.class);

    private AB [] ab;
    private AB [] ab2;

    private final double [] laplacianMatrix = {
            0.05, 0.20, 0.05,
            0.20,-1.00, 0.20,
            0.05, 0.20, 0.05
    };
    private int width, height;
    private double diffusionA = 1.0;
    private double diffusionB = 0.5;
    private double feedRate = 0.055;  // should be range 0...1?
    private double killRate = 0.062;
    private double dt = 0.0;

    private final EventListenerList listenerList = new EventListenerList();


    public boolean getInitialized() {
        return (ab[0]!=null);
    }

    public void setSize(int width,int height) {
        if (width <= 0 || height <= 0) return;

        this.width = width;
        this.height = height;
        ab = new AB[this.width * this.height];
        ab2 = new AB[this.width * this.height];

        initializeAB();
    }

    public void paint(int x, int y,double intensity) {
        if(x<0 || x>= width || y<0 || y>= height) return;
        var p = ab[y* width +x];
        p.b = Math.clamp(p.b + intensity,0,1);
    }

    private void initializeAB() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                var index = y* width +x;
                ab[index] = new AB();
                ab2[index] = new AB();
                ab[index].a = 1.0;
                ab2[index].a = 1.0;
            }
        }
    }

    public void performReactionDiffusion() {
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                AB laplacian = getLaplacian(x,y);
                int index = y * width + x;
                var p0 = ab[index];
                var p1 = ab2[index];
                double a = p0.a;
                double b = p0.b;
                double abb = a * b * b;
                p1.a = a + (diffusionA * laplacian.a - abb + feedRate * (1.0 - a)     ) * dt;
                p1.b = b + (diffusionB * laplacian.b + abb - (killRate + feedRate) * b) * dt;
            }
        }

        // swap the buffers.
        var temp = ab;
        ab = ab2;
        ab2 = temp;
    }

    private AB getLaplacian(int px, int py) {
        AB result = new AB();
        for (int y = 0; y < 3; ++y) {
            int y2 = Math.clamp(y + py - 1, 0, height-1);
            for (int x = 0; x < 3; ++x) {
                int x2 = Math.clamp(x + px - 1, 0, width-1);
                var p0 = ab[y2 * width + x2];
                double scale = laplacianMatrix[y * 3 + x];
                result.a += p0.a * scale;
                result.b += p0.b * scale;
            }
        }
        return result;
    }

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        if(dt == this.dt) return;
        this.dt = dt;
        firePropertyChange(new PropertyChangeEvent(this, "dt", null, dt));
    }

    public double getKillRate() {
        return killRate;
    }

    public void setKillRate(double killRate) {
        if(killRate == this.killRate) return;
        this.killRate = killRate;
        firePropertyChange(new PropertyChangeEvent(this, "killRate", null, killRate));
    }

    public double getFeedRate() {
        return feedRate;
    }

    public void setFeedRate(double feedRate) {
        if(feedRate == this.feedRate) return;
        this.feedRate = feedRate;
        firePropertyChange(new PropertyChangeEvent(this, "feedRate", null, feedRate));
    }

    public double getDiffusionB() {
        return diffusionB;
    }

    public void setDiffusionB(double diffusionB) {
        if(diffusionB == this.diffusionB) return;
        this.diffusionB = diffusionB;
        firePropertyChange(new PropertyChangeEvent(this, "diffusionB", null, diffusionB));
    }

    public double getDiffusionA() {
        return diffusionA;
    }

    public void setDiffusionA(double diffusionA) {
        if(diffusionA == this.diffusionA) return;
        this.diffusionA = diffusionA;
        firePropertyChange(new PropertyChangeEvent(this, "diffusionA", null, diffusionA));
    }

    private final JFileChooser fileChooser = new JFileChooser();

    public void loadImage() {
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp"));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage src = ImageIO.read(file);
                copyImageToAB(src);
            } catch (Exception e) {
                logger.error("Failed to load image.", e);
            }
        }
    }

    private void copyImageToAB(BufferedImage src) {
        int w = Math.min(src.getWidth(), this.width);
        int h = Math.min(src.getHeight(), this.height);
        for(int y=0;y<h;y++) {
            for(int x=0;x<w;x++) {
                var p0 = ab[y*this.width +x];
                var color = new Color(src.getRGB(x,y));
                double intensity = (color.getRed()/255.0 + color.getGreen()/255.0 + color.getBlue()/255.0) / 3.0;
                // intensity 1 is full A.  intensity 0 is full B.
                p0.a = Math.clamp(intensity-0.5,0.0,1.0)*2.0;
                p0.b = Math.clamp(0.5-intensity,0.0,1.0)*2.0;
            }
        }
    }

    public AB [] getAB() {
        return ab;
    }

    /**
     * Wipe the AB buffer and paint a ring.
     * The ring OD 1/3 of the max dimension.
     * The ring ID is 1/4 of the max dimension.
     */
    public void startRing() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = image.getGraphics();
        int min = Math.min(width, height);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.fillOval(width/2 - min/6, height/2 - min/6, min/3, min/3);
        g.setColor(Color.WHITE);
        g.fillOval(width/2 - min/8, height/2 - min/8, min/4, min/4);
        copyImageToAB(image);
    }

    public void startSquare() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = image.getGraphics();
        int min = Math.min(width, height);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.fillRect(width/2 - min/6, height/2 - min/6, min/3, min/3);
        copyImageToAB(image);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listenerList.add(PropertyChangeListener.class, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listenerList.remove(PropertyChangeListener.class, listener);
    }

    private void firePropertyChange(PropertyChangeEvent evt) {
        for (PropertyChangeListener listener : listenerList.getListeners(PropertyChangeListener.class)) {
            listener.propertyChange(evt);
        }
    }
}
