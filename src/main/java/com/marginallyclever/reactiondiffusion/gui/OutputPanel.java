package com.marginallyclever.reactiondiffusion.gui;

import com.marginallyclever.reactiondiffusion.Model;
import com.marginallyclever.reactiondiffusion.View;
import io.github.andrewauclair.moderndocking.ui.DefaultDockingPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Displays the reaction/diffusion
 */
public class OutputPanel extends DefaultDockingPanel {
    private static final Logger logger = LoggerFactory.getLogger(OutputPanel.class);

    private final Model model;
    private final View view;
    private final Timer renderTimer;
    private boolean mousePressed=false;
    private int mouseX,mouseY;

    public OutputPanel(Model model) {
        super("OutputPanel","Output");
        this.model = model;
        this.view = new View(model);
        setLayout(new BorderLayout());

        renderTimer = new Timer(1000 / 30, e -> this.repaint());

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                mousePressed=true;
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                mousePressed=false;
            }
        });
    }

    private void paintCircle(int x, int y, int radius) {
        for(int j = -radius; j < radius; j++) {
            for(int k = -radius; k < radius; k++) {
                var d = Math.sqrt(j*j+k*k);
                d/=radius;
                var clamped = Math.clamp(1.0-d,0,0.95);
                model.paint(x+j, y+k, clamped);
            }
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        renderTimer.restart();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        renderTimer.stop();
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        logger.info("set size {}x{}",d.width,d.height);
        model.setSize(d.width, d.height);
        view.setSize(d.width, d.height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(!model.getInitialized()) return;

        if(mousePressed) {
            paintCircle(mouseX, mouseY, 10);
        }

        view.updateImage();

        g.drawImage(view.getImage(), 0, 0, null);

        for(int i=0;i<10;++i) {
            model.performReactionDiffusion();
        }
    }

    private void setRenderHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}
