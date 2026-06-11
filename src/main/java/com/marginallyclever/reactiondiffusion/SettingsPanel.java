package com.marginallyclever.reactiondiffusion;

import io.github.andrewauclair.moderndocking.ui.DefaultDockingPanel;

import javax.swing.*;
import java.awt.*;
import java.security.Provider;
import java.util.function.Consumer;

public class SettingsPanel extends DefaultDockingPanel {
    private final Model model;

    public SettingsPanel(Model model) {
        super("SettingsPanel", "Settings");
        this.model = model;

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;

        addControl(c,"Diffusion A",model.getDiffusionA(), model::setDiffusionA,"diffusionA");
        addControl(c,"Diffusion B",model.getDiffusionB(), model::setDiffusionB,"diffusionB");
        addControl(c,"Feed rate",model.getFeedRate(), model::setFeedRate,"feedRate");
        addControl(c,"Kill rate",model.getKillRate(), model::setKillRate,"killRate");

        c.gridx=0;
        add(new JLabel("Paused?"),c);
        c.gridx++;
        c.gridwidth=2;
        var t = new JButton("Yes");
        t.addActionListener(e -> {
            if( model.getDt() == 0 ) {
                model.setDt(1);
                t.setText("No");
            } else {
                model.setDt(0);
                t.setText("Yes");
            }
        });
        add(t,c);
    }

    private void addControl(GridBagConstraints c,String label, double start, Consumer<Double> consumer,String propertyName) {
        c.weightx=0.0;
        c.gridx=0;
        add(new JLabel(label),c);
        c.gridx++;

        int scale = 10000;
        double iscale = 1.0/(double)scale;

        var slider = new JSlider(JSlider.HORIZONTAL, 0, scale, (int)(start*scale) );
        slider.addChangeListener(e -> consumer.accept((double) ((JSlider) e.getSource()).getValue() *iscale) );
        c.weightx=1.0;
        add(slider,c);
        c.gridx++;

        var a = new JSpinner(new SpinnerNumberModel(start, 0.0, 1.0, iscale));
        a.addChangeListener(e -> consumer.accept((double) ((JSpinner) e.getSource()).getValue()) );
        a.setEditor(new JSpinner.NumberEditor(a, "0.0000"));
        c.weightx=0.0;
        add(a, c);

        model.addPropertyChangeListener(e -> {
            if(e.getPropertyName().equals(propertyName)) {
                double v = (double) e.getNewValue();
                slider.setValue((int)(v*scale));
                a.setValue(v);
            }
        });

        c.gridy++;
    }
}
