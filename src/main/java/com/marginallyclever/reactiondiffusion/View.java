package com.marginallyclever.reactiondiffusion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders the model to a BufferedImage
 */
public class View {
    private static final Logger logger = LoggerFactory.getLogger(View.class);

    private final Model model;
    private BufferedImage image;
    // faster than looking it up every frame
    private int width, height;
    // coloring control
    private int colorScheme=0;
    private boolean invert=false;

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

    /**
     * use the Model {@link PetriDish} and color scheme to update a {@link BufferedImage}.
     */
    public void updateImage() {
        var ab = model.getPetriDish();
        int r,g,b;

        for(int x=0;x<width;x++) {
            for(int y=0;y<height;y++) {
                var p0 = ab[y*width+x];
                var temp = p0.a+p0.b;
                if(temp==0) temp=1.0;
                var val = Math.clamp(p0.a / temp,0.5,1)*2-1;


                switch(colorScheme) {
                    case 0: {  // grayscale
                        r=g=b=(int)(val * 255);
                        break;
                    }
                    case 1: {  // reds
                        if(val<0.5) {
                            int intensity = (int)map(val,0,0.5,0,255);
                            r=intensity;
                            g=b=0;
                        } else {
                            int intensity = (int)map(val,0.5,1,0,255);
                            r=255;
                            g=b=intensity;
                        }
                        break;
                    }
                    case 2: {  // greens
                        if(val<0.25) {
                            int intensity = (int)map(val,0,0.25,16,255);
                            g=intensity;
                            r=b=16;
                        } else {
                            int intensity = (int)map(val,0.25,1,0,255);
                            g=255;
                            r=b=intensity;
                        }
                        break;
                    }
                    case 3: {  // blues
                        if(val<0.375) {
                            int intensity = (int)map(val,0,0.375,0,255);
                            b=intensity;
                            r=g=0;
                        } else {
                            int intensity = (int)map(val,0.375,1,0,255);
                            b=255;
                            r=g=intensity;
                        }
                        break;
                    }
                    default: {
                        Color c = rainbow(val);
                        r=c.getRed();
                        g=c.getGreen();
                        b=c.getBlue();
                        break;
                    }
                }

                if(invert) {
                    r = 255-r;
                    g = 255-g;
                    b = 255-b;
                }
                // apply the color to the bufferedImage pixel.
                image.setRGB(x,y, (0xff<<24 | r<<16 | g<<8 | b));
            }
        }
    }

    private Color rainbow(double v) {
        v*=Math.PI*2.0;
        return new Color(
                (int)((Math.sin(v                )+1)*127.5),
                (int)((Math.sin(v+Math.PI    /3.0)+1)*127.5),
                (int)((Math.sin(v+Math.PI*2.0/3.0)+1)*127.5));
    }

    public void setColorScheme(int selectedIndex) {
        colorScheme = selectedIndex;
    }

    public void setInvertColors(boolean selected) {
        invert = selected;
    }
}
