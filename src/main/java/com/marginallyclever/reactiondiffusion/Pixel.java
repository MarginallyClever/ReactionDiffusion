package com.marginallyclever.reactiondiffusion;

public class Pixel {
    public int x, y;  // pixel on screen
    public int index;  // linear index to same pixel.

    public Pixel(int x, int y,int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }
}
