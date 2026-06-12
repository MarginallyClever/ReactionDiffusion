package com.marginallyclever.reactiondiffusion;

/**
 * Stores the A and B components in a 2 chemical reaction diffusion model.
 */
public class PetriDish {
    public double a, b;
    // could add diffusionA, diffusionB, feedRate, killRate, dt.

    public PetriDish() {}
    
    public PetriDish(PetriDish c) {
        this.a = c.a;
        this.b = c.b;
    }
}
