package com.marginallyclever.reactiondiffusion;

/**
 * Stores the A and B components in a 2 chemical reaction diffusion model.
 */
public class PetriDish {
    public double a, b;
    // could add per-pixel kill rate, feed rate, speed, etc.
    public double diffusionA = 1.0;
    public double diffusionB = 0.5;
    public double feedRate = 0.055;  // should be range 0...1?
    public double killRate = 0.062;
    public double dt = 0.0;

    public PetriDish() {}
    
    public PetriDish(PetriDish c) {
        this.a = c.a;
        this.b = c.b;
        this.diffusionA = c.diffusionA;
        this.diffusionB = c.diffusionB;
        this.feedRate = c.feedRate;
        this.killRate = c.killRate;
        this.dt = c.dt;
    }
}
