package com.marginallyclever.reactiondiffusion;

/**
 * Stores the A and B components in a 2 chemical reaction diffusion model.
 */
public class AB {
    public double a, b;

    public AB() {}
    
    public AB(AB c) {
        this.a = c.a;
        this.b = c.b;
    }
}
