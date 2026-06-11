package com.marginallyclever.reactiondiffusion;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders the model
 */
public class View {
    private final Model model;
    private BufferedImage image;
    private int width, height;

    public View(Model model) {
        this.model = model;
    }

    public void setSize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * map value from one range to another.
     * @param value the value to map
     * @param min1 starting range min
     * @param max1 starting range max
     * @param min2 ending range min
     * @param max2 ending range max
     * @return the new value
     */
    double map(double value, double min1, double max1, double min2, double max2) {
        return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
    }
    
    public void updateImage() {
        // use AB to update the image pixels.
        double maxA=0;
        double maxB=0;
/*
        for(int x=0;x<w;x++) {
            for (int y = 0; y < h; y++) {
                var p0 = ab[y * w + x];
                maxA = Math.max(p0.a, maxA);
                maxB = Math.max(p0.b, maxB);
            }
        }*/
        if(maxA==0) maxA=1;
        if(maxB==0) maxB=1;

        var ab = model.getAB();

        for(int x=0;x<width;x++) {
            for(int y=0;y<height;y++) {
                var p0 = ab[y*width+x];
                var temp = p0.a+p0.b;
                if(temp==0) temp=1.0;
                var val = p0.a / temp;

                int intensity = (int)map(val,0,1,0,255);
                intensity = Math.clamp(intensity,0,255);
                //var c = new Color(intensity,intensity,intensity);
                var c = rainbow(intensity/255.0);
                image.setRGB(x,y,c.getRGB());
            }
        }
        //logger.debug("Max {}\t{}",maxA,maxB);
    }

    private Color rainbow(double v) {
        v*=Math.PI*2.0;
        return new Color(
                (int)((Math.sin(v                )+1)*127.5),
                (int)((Math.sin(v+Math.PI    /3.0)+1)*127.5),
                (int)((Math.sin(v+Math.PI*2.0/3.0)+1)*127.5));
    }
}
