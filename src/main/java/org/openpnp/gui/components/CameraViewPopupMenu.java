/*
 * Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 * 
 * This file is part of OpenPnP.
 * 
 * OpenPnP is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * OpenPnP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with OpenPnP. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.gui.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.openpnp.gui.MainFrame;
import org.openpnp.gui.components.CameraView.RenderingQuality;
import org.openpnp.gui.components.reticle.CrosshairReticle;
import org.openpnp.gui.components.reticle.FiducialReticle;
import org.openpnp.gui.components.reticle.GridReticle;
import org.openpnp.gui.components.reticle.Reticle;
import org.openpnp.gui.components.reticle.RulerReticle;
import org.openpnp.gui.processes.EstimateObjectZCoordinateProcess;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.spi.Nozzle;
import org.openpnp.util.MovableUtils;
import org.openpnp.util.UiUtils;

// TODO: For the time being, since setting a property on the reticle doesn't re-save it we are
// making a redundant call to setReticle on every property update. Fix that somehow.
@SuppressWarnings("serial")
public class CameraViewPopupMenu extends JPopupMenu {
    private CameraView cameraView;
    private JMenu zoomIncMenu;
    private JMenu reticleMenu;
    private JMenu reticleOptionsMenu;
    private JMenu renderingQualityMenu;

    public CameraViewPopupMenu(CameraView cameraView) {
        this.cameraView = cameraView;

        // For cameras that have been calibrated at two different heights, add menu options to reset
        // the viewing plane and for estimating an object's height
        if (cameraView.isViewingPlaneChangable()) {
            JMenuItem mntmEstimateZCoordinate = new JMenuItem("Estimate Z Coordinate of Object");
            mntmEstimateZCoordinate.addActionListener(estimateZCoordinateAction);
            add(mntmEstimateZCoordinate);
        }

        // For non-movable cameras, add a menu option to move the selected nozzle to the camera
        if (cameraView.getCamera().getHead() == null) {
            JMenuItem mntmMoveSelectedNozzleToCamera = new JMenuItem("Move Selected Nozzle to Camera");
            mntmMoveSelectedNozzleToCamera.addActionListener(moveSelectedNozzleToCameraAction);
            add(mntmMoveSelectedNozzleToCamera);
        }

        zoomIncMenu = createZoomIncMenu();

        add(zoomIncMenu);

        renderingQualityMenu = createRenderingQualityMenu();

        add(renderingQualityMenu);

        reticleMenu = createReticleMenu();

        add(reticleMenu);

        JCheckBoxMenuItem chkShowImageInfo = new JCheckBoxMenuItem(showImageInfoAction);
        chkShowImageInfo.setSelected(cameraView.isShowImageInfo());
        add(chkShowImageInfo);


        if (cameraView.getDefaultReticle() != null) {
            if (cameraView.getDefaultReticle() instanceof RulerReticle) {
                setReticleOptionsMenu(createRulerReticleOptionsMenu(
                        (RulerReticle) cameraView.getDefaultReticle()));
            }
            else if (cameraView.getDefaultReticle() instanceof GridReticle) {
                setReticleOptionsMenu(createRulerReticleOptionsMenu(
                        (GridReticle) cameraView.getDefaultReticle()));
            }
            else if (cameraView.getDefaultReticle() instanceof FiducialReticle) {
                setReticleOptionsMenu(createFiducialReticleOptionsMenu(
                        (FiducialReticle) cameraView.getDefaultReticle()));
            }
            else if (cameraView.getDefaultReticle() instanceof CrosshairReticle) {
                setReticleOptionsMenu(createCrosshairReticleOptionsMenu(
                        (CrosshairReticle) cameraView.getDefaultReticle()));
            }
        }
    }

    private JMenu createZoomIncMenu() {
        JMenu subMenu = new JMenu("Zoom Increment Per Mouse Wheel Tick");
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem("10.0");
        buttonGroup.add(menuItem);
        if (cameraView.getZoomIncPerMouseWheelTick() == 10.0) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraView.setZoomIncPerMouseWheelTick(10.0);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("1.0");
        buttonGroup.add(menuItem);
        if (cameraView.getZoomIncPerMouseWheelTick() == 1.0) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraView.setZoomIncPerMouseWheelTick(1.0);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("0.1");
        buttonGroup.add(menuItem);
        if (cameraView.getZoomIncPerMouseWheelTick() == 0.1) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraView.setZoomIncPerMouseWheelTick(0.1);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("0.01");
        buttonGroup.add(menuItem);
        if (cameraView.getZoomIncPerMouseWheelTick() == 0.01) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraView.setZoomIncPerMouseWheelTick(0.01);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("0.001");
        buttonGroup.add(menuItem);
        if (cameraView.getZoomIncPerMouseWheelTick() == 0.001) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraView.setZoomIncPerMouseWheelTick(0.001);
            }
        });
        subMenu.add(menuItem);
        
        return subMenu;
    }

    private JMenu createRenderingQualityMenu() {
        JMenu subMenu = new JMenu("Rendering Quality");
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButtonMenuItem menuItem;
        
        menuItem = new JRadioButtonMenuItem("Low Quality");
        buttonGroup.add(menuItem);
        if (cameraView.getRenderingQuality() == RenderingQuality.Low) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraView.setRenderingQuality(RenderingQuality.Low);
            }
        });
        subMenu.add(menuItem);
        
        menuItem = new JRadioButtonMenuItem("High Quality");
        buttonGroup.add(menuItem);
        if (cameraView.getRenderingQuality() == RenderingQuality.High) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraView.setRenderingQuality(RenderingQuality.High);
            }
        });
        subMenu.add(menuItem);
        
        menuItem = new JRadioButtonMenuItem("Highest Quality (best scale)");
        buttonGroup.add(menuItem);
        if (cameraView.getRenderingQuality() == RenderingQuality.BestScale) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraView.setRenderingQuality(RenderingQuality.BestScale);
            }
        });
        subMenu.add(menuItem);
        
        return subMenu;
    }

    private JMenu createReticleMenu() {
        JMenu menu = new JMenu("Reticle");

        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButtonMenuItem menuItem;

        Reticle reticle = cameraView.getDefaultReticle();

        menuItem = new JRadioButtonMenuItem(noReticleAction);
        if (reticle == null) {
            menuItem.setSelected(true);
        }
        buttonGroup.add(menuItem);
        menu.add(menuItem);

        menuItem = new JRadioButtonMenuItem(crosshairReticleAction);
        if (reticle != null && reticle.getClass() == CrosshairReticle.class) {
            menuItem.setSelected(true);
        }
        buttonGroup.add(menuItem);
        menu.add(menuItem);

        menuItem = new JRadioButtonMenuItem(gridReticleAction);
        if (reticle != null && reticle.getClass() == GridReticle.class) {
            menuItem.setSelected(true);
        }
        buttonGroup.add(menuItem);
        menu.add(menuItem);

        menuItem = new JRadioButtonMenuItem(rulerReticleAction);
        if (reticle != null && reticle.getClass() == RulerReticle.class) {
            menuItem.setSelected(true);
        }
        buttonGroup.add(menuItem);
        menu.add(menuItem);

        menuItem = new JRadioButtonMenuItem(fiducialReticleAction);
        if (reticle != null && reticle.getClass() == FiducialReticle.class) {
            menuItem.setSelected(true);
        }
        buttonGroup.add(menuItem);
        menu.add(menuItem);

        return menu;
    }
    
    private JMenuItem createColorMenuItem(String name, Color color, ButtonGroup buttonGroup, CrosshairReticle reticle) {
        JMenuItem menuItem = new JRadioButtonMenuItem(name);
        buttonGroup.add(menuItem);
        if (reticle.getColor().equals(color)) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setColor(color);
                cameraView.setDefaultReticle(reticle);
            }
        });
        return menuItem;
    }

    private JMenu createCrosshairReticleOptionsMenu(final CrosshairReticle reticle) {
        JMenu menu = new JMenu("Options");

        ButtonGroup buttonGroup = new ButtonGroup();

        menu.add(createColorMenuItem("Red", Color.red, buttonGroup, reticle));
        menu.add(createColorMenuItem("Green", Color.green, buttonGroup, reticle));
        menu.add(createColorMenuItem("Yellow", Color.yellow, buttonGroup, reticle));
        menu.add(createColorMenuItem("Orange", Color.decode("#ffd35d"), buttonGroup, reticle));
        menu.add(createColorMenuItem("Blue", Color.blue, buttonGroup, reticle));
        menu.add(createColorMenuItem("White", Color.white, buttonGroup, reticle));
        menu.add(createColorMenuItem("Red", Color.red, buttonGroup, reticle));

        return menu;
    }

    private JMenu createRulerReticleOptionsMenu(final RulerReticle reticle) {
        JMenu menu = new JMenu("Options");

        JMenu subMenu;
        JRadioButtonMenuItem menuItem;
        ButtonGroup buttonGroup;

        subMenu = new JMenu("Color");
        buttonGroup = new ButtonGroup();
        subMenu.add(createColorMenuItem("Red", Color.red, buttonGroup, reticle));
        subMenu.add(createColorMenuItem("Green", Color.green, buttonGroup, reticle));
        subMenu.add(createColorMenuItem("Yellow", Color.yellow, buttonGroup, reticle));
        subMenu.add(createColorMenuItem("Orange", Color.decode("#ffd35d"), buttonGroup, reticle));
        subMenu.add(createColorMenuItem("Blue", Color.blue, buttonGroup, reticle));
        subMenu.add(createColorMenuItem("White", Color.white, buttonGroup, reticle));
        menu.add(subMenu);

        subMenu = new JMenu("Units");
        buttonGroup = new ButtonGroup();
        menuItem = new JRadioButtonMenuItem("Millimeters");
        buttonGroup.add(menuItem);
        if (reticle.getUnits() == LengthUnit.Millimeters) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnits(LengthUnit.Millimeters);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("Inches");
        buttonGroup.add(menuItem);
        if (reticle.getUnits() == LengthUnit.Inches) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnits(LengthUnit.Inches);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menu.add(subMenu);

        subMenu = new JMenu("Units Per Tick");
        buttonGroup = new ButtonGroup();
        menuItem = new JRadioButtonMenuItem("0.1");
        buttonGroup.add(menuItem);
        if (reticle.getUnitsPerTick() == 0.1) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnitsPerTick(0.1);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("0.25");
        buttonGroup.add(menuItem);
        if (reticle.getUnitsPerTick() == 0.25) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnitsPerTick(0.25);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("0.50");
        buttonGroup.add(menuItem);
        if (reticle.getUnitsPerTick() == 0.50) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnitsPerTick(0.50);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("1");
        buttonGroup.add(menuItem);
        if (reticle.getUnitsPerTick() == 1) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnitsPerTick(1);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("2");
        buttonGroup.add(menuItem);
        if (reticle.getUnitsPerTick() == 2) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnitsPerTick(2);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("5");
        buttonGroup.add(menuItem);
        if (reticle.getUnitsPerTick() == 5) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnitsPerTick(5);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("10");
        buttonGroup.add(menuItem);
        if (reticle.getUnitsPerTick() == 10) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnitsPerTick(10);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menu.add(subMenu);

        return menu;
    }

    private JMenu createFiducialReticleOptionsMenu(final FiducialReticle reticle) {
        JMenu menu = new JMenu("Options");

        JMenu subMenu;
        JRadioButtonMenuItem menuItem;
        ButtonGroup buttonGroup;

        subMenu = new JMenu("Color");
        buttonGroup = new ButtonGroup();
        subMenu.add(createColorMenuItem("Red", Color.red, buttonGroup, reticle));
        subMenu.add(createColorMenuItem("Green", Color.green, buttonGroup, reticle));
        subMenu.add(createColorMenuItem("Yellow", Color.yellow, buttonGroup, reticle));
        subMenu.add(createColorMenuItem("Orange", Color.decode("#ffd35d"), buttonGroup, reticle));
        subMenu.add(createColorMenuItem("Blue", Color.blue, buttonGroup, reticle));
        subMenu.add(createColorMenuItem("White", Color.white, buttonGroup, reticle));
        menu.add(subMenu);

        subMenu = new JMenu("Units");
        buttonGroup = new ButtonGroup();
        menuItem = new JRadioButtonMenuItem("Millimeters");
        buttonGroup.add(menuItem);
        if (reticle.getUnits() == LengthUnit.Millimeters) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnits(LengthUnit.Millimeters);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("Inches");
        buttonGroup.add(menuItem);
        if (reticle.getUnits() == LengthUnit.Inches) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setUnits(LengthUnit.Inches);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menu.add(subMenu);

        subMenu = new JMenu("Shape");
        buttonGroup = new ButtonGroup();
        menuItem = new JRadioButtonMenuItem("Circle");
        buttonGroup.add(menuItem);
        if (reticle.getShape() == FiducialReticle.Shape.Circle) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setShape(FiducialReticle.Shape.Circle);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menuItem = new JRadioButtonMenuItem("Square");
        buttonGroup.add(menuItem);
        if (reticle.getShape() == FiducialReticle.Shape.Square) {
            menuItem.setSelected(true);
        }
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setShape(FiducialReticle.Shape.Square);
                cameraView.setDefaultReticle(reticle);
            }
        });
        subMenu.add(menuItem);
        menu.add(subMenu);

        JCheckBoxMenuItem chkMenuItem = new JCheckBoxMenuItem("Filled");
        chkMenuItem.setSelected(reticle.isFilled());
        chkMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reticle.setFilled(((JCheckBoxMenuItem) e.getSource()).isSelected());
                cameraView.setDefaultReticle(reticle);
            }
        });
        menu.add(chkMenuItem);

        JMenuItem inputMenuItem = new JMenuItem("Size");
        inputMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = JOptionPane.showInputDialog(cameraView,
                        String.format("Enter the size in %s",
                                reticle.getUnits().toString().toLowerCase()),
                        reticle.getSize() + "");
                if (result != null) {
                    reticle.setSize(Double.valueOf(result));
                    cameraView.setDefaultReticle(reticle);
                }
            }
        });
        menu.add(inputMenuItem);

        return menu;
    }

    private void setReticleOptionsMenu(JMenu menu) {
        if (reticleOptionsMenu != null) {
            reticleMenu.remove(reticleMenu.getMenuComponentCount() - 1);
            reticleMenu.remove(reticleMenu.getMenuComponentCount() - 1);
        }
        if (menu != null) {
            reticleMenu.addSeparator();
            reticleMenu.add(menu);
        }
        reticleOptionsMenu = menu;
    }

    private Action showImageInfoAction = new AbstractAction("Show Image Info?") {
        @Override
        public void actionPerformed(ActionEvent e) {
            cameraView.setShowImageInfo(((JCheckBoxMenuItem) e.getSource()).isSelected());
        }
    };

    private Action noReticleAction = new AbstractAction("None") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            setReticleOptionsMenu(null);
            cameraView.setDefaultReticle(null);
        }
    };

    private Action crosshairReticleAction = new AbstractAction("Crosshair") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            CrosshairReticle reticle = new CrosshairReticle();
            JMenu optionsMenu = createCrosshairReticleOptionsMenu(reticle);
            setReticleOptionsMenu(optionsMenu);
            cameraView.setDefaultReticle(reticle);
        }
    };

    private Action gridReticleAction = new AbstractAction("Grid") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            GridReticle reticle = new GridReticle();
            JMenu optionsMenu = createRulerReticleOptionsMenu(reticle);
            setReticleOptionsMenu(optionsMenu);
            cameraView.setDefaultReticle(reticle);
        }
    };

    private Action rulerReticleAction = new AbstractAction("Ruler") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            RulerReticle reticle = new RulerReticle();
            JMenu optionsMenu = createRulerReticleOptionsMenu(reticle);
            setReticleOptionsMenu(optionsMenu);
            cameraView.setDefaultReticle(reticle);
        }
    };

    private Action fiducialReticleAction = new AbstractAction("Fiducial") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            FiducialReticle reticle = new FiducialReticle();
            JMenu optionsMenu = createFiducialReticleOptionsMenu(reticle);
            setReticleOptionsMenu(optionsMenu);
            cameraView.setDefaultReticle(reticle);
        }
    };

    /**
     * Listen for menu selection to estimate an object's height
     */
    private ActionListener estimateZCoordinateAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UiUtils.messageBoxOnException(() -> {
                new EstimateObjectZCoordinateProcess(MainFrame.get(), cameraView);
            });
        }
    };

    /**
     * Listener for menu selection to move the selected nozzle to the camera (only works for
     * non-movable cameras)
     */
    private ActionListener moveSelectedNozzleToCameraAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UiUtils.submitUiMachineTask(() -> {
                // Get the selected nozzle
                Nozzle nozzle = MainFrame.get().getMachineControls().getSelectedNozzle();
                // Add the offsets to the Camera's nozzle calibrated position.
                Location location = cameraView.getCamera().getLocation(nozzle);
                // Don't change rotation. 
                location = nozzle.getLocation().derive(location, true, true, true, false);
                // Move the nozzle to the camera
                MovableUtils.moveToLocationAtSafeZ(nozzle, location);
                MovableUtils.fireTargetedUserAction(nozzle);
            });
        }
    };

}
