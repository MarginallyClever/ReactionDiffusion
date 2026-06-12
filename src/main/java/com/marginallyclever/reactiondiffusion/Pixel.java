package com.marginallyclever.reactiondiffusion;

/**
 * Pixel is used by {@link Model#performReactionDiffusion()} for multithreading.
 */
public class Pixel {
    public int x, y;  // pixel on screen
    public int index;  // linear index to same pixel.

    public Pixel(int x, int y,int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }
}
