package com.marginallyclever.reactiondiffusion.gui;

import com.marginallyclever.reactiondiffusion.Model;
import com.marginallyclever.reactiondiffusion.View;
import io.github.andrewauclair.moderndocking.Dockable;
import io.github.andrewauclair.moderndocking.DockableTabPreference;
import io.github.andrewauclair.moderndocking.DockingRegion;
import io.github.andrewauclair.moderndocking.app.AppState;
import io.github.andrewauclair.moderndocking.app.Docking;
import io.github.andrewauclair.moderndocking.app.RootDockingPanel;
import io.github.andrewauclair.moderndocking.exception.DockingLayoutException;
import io.github.andrewauclair.moderndocking.ext.ui.DockingUI;
import io.github.andrewauclair.moderndocking.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

public class MainWindow extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);
    private final Model model = new Model();
    private final View view = new View(model);
    private final OutputPanel output = new OutputPanel(model,view);

    public MainWindow() {
        super("Reaction Diffusion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        // Initialise Modern Docking - Must happen before any dockable is created.
        Docking.initialize(this);
        DockingUI.initialize();  // installs toolbar / tab chrome
        Settings.setDefaultTabPreference(DockableTabPreference.TOP_ALWAYS);
        Settings.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Root panel (Modern Docking requires this as content pane) ─
        RootDockingPanel root = new RootDockingPanel(this);
        add(root, BorderLayout.CENTER);
        setVisible(true);

        SettingsPanel settings = new SettingsPanel(model,view);

        Docking.registerDockable(output);
        Docking.registerDockable(settings);

        Docking.dock(output, this, DockingRegion.CENTER);
        Docking.dock(settings, output, DockingRegion.SOUTH);

        // auto-persist
        AppState.setPersistFile(new File("layout.xml"));
        AppState.setAutoPersist(true);
        try {
            AppState.restore();
        } catch (DockingLayoutException e) {
            logger.error("Failed to restore docking layout.", e);
        }

        // Optional: menu bar with View menu for toggling panels ─────
        setJMenuBar(buildMenuBar(output));
    }

    private JMenuBar buildMenuBar(Dockable... dockables) {
        var bar = new JMenuBar();
        bar.add(createFileMenu());

        // view menu
        var view = new JMenu("View");
        bar.add(view);
        for (Dockable d : dockables) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(d.getTabText(), true);
            item.addActionListener(e -> {
                if (item.isSelected()) {
                    Docking.dock(d, this);
                } else {
                    Docking.undock(d);
                }
            });
            view.add(item);
        }

        return bar;
    }

    private JMenu createFileMenu() {
        var file = new JMenu("File");

        var load = new JMenuItem("Load");
        load.addActionListener(e -> model.loadImage());
        file.add(load);

        var start = new JMenu("Starter");
        file.add(start);

        addStarter(start,"Ring",e->{
            model.startRing();
            output.repaint();
        });
        addStarter(start,"Square",e->{
            model.startSquare();
            output.repaint();
        });
        addStarter(start,"Gradient",e->{
            model.startGradient();
            output.repaint();
        });

        file.addSeparator();

        var quit = new JMenuItem("Quit");
        quit.addActionListener(e -> System.exit(0));
        file.add(quit);

        return file;
    }

    private void addStarter(JMenu menu,String label, Consumer<Model> starter) {
        var item = new JMenuItem(label);
        item.addActionListener(e -> starter.accept(model));
        menu.add(item);
    }
}
