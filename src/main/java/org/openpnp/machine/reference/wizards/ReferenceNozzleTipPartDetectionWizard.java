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

package org.openpnp.machine.reference.wizards;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.openpnp.Translations;
import org.openpnp.gui.components.ComponentDecorators;
import org.openpnp.gui.components.SimpleGraphView;
import org.openpnp.gui.support.AbstractConfigurationWizard;
import org.openpnp.gui.support.DoubleConverter;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.machine.reference.ReferenceNozzleTip;
import org.openpnp.machine.reference.ReferenceNozzleTip.VacuumMeasurementMethod;
import org.openpnp.model.Configuration;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class ReferenceNozzleTipPartDetectionWizard extends AbstractConfigurationWizard {
    private final ReferenceNozzleTip nozzleTip;

    private Set<org.openpnp.model.Package> compatiblePackages = new HashSet<>();
    private JPanel panelPartOnVacuumSensing;


    public ReferenceNozzleTipPartDetectionWizard(ReferenceNozzleTip nozzleTip) {
        this.nozzleTip = nozzleTip;
        
        panelPartOnVacuumSensing = new JPanel();
        panelPartOnVacuumSensing.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                Translations.getString(
                        "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.Border.title"), //$NON-NLS-1$
                TitledBorder.LEADING, TitledBorder.TOP, null));
        contentPanel.add(panelPartOnVacuumSensing);
        panelPartOnVacuumSensing.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("max(50dlu;default)"),
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("max(50dlu;default)"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),}));
        
        lblPartOnMeasurement = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.MeasurementMethodLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(lblPartOnMeasurement, "2, 2, right, default");
        
        methodPartOn = new JComboBox(ReferenceNozzleTip.VacuumMeasurementMethod.values());
        methodPartOn.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                adaptDialog();
            }
        });
        panelPartOnVacuumSensing.add(methodPartOn, "4, 2, 3, 1");
        
        lblEstablishPartOnLevel = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.EstablishLevelLabel.text")); //$NON-NLS-1$
        lblEstablishPartOnLevel.setToolTipText(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.EstablishLevelLabel.toolTipText" //$NON-NLS-1$
        ));
        panelPartOnVacuumSensing.add(lblEstablishPartOnLevel, "2, 4, right, default");
        
        establishPartOnLevel = new JCheckBox("");
        establishPartOnLevel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                adaptDialog();
            }
        });
        panelPartOnVacuumSensing.add(establishPartOnLevel, "4, 4");
        
        lblPerformPartOnChecks = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.PerformChecksLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(lblPerformPartOnChecks, "2, 6, right, default");
        
        partOnCheckAfterPick = new JCheckBox(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.AfterPickLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(partOnCheckAfterPick, "4, 6");
        
        partOnCheckAlign = new JCheckBox(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.AlignmentLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(partOnCheckAlign, "6, 6");
        
        partOnCheckBeforePlace = new JCheckBox(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.BeforePlaceLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(partOnCheckBeforePlace, "10, 6");
        
        lblPartOnLowValue = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.LowValueLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(lblPartOnLowValue, "4, 8");
        
        lblPartOnHighValue = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.HighValueLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(lblPartOnHighValue, "6, 8");
        
        lblPartOnLastReading = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.LastReadingLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(lblPartOnLastReading, "10, 8");
        
        lblPartOnNozzle = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.VacuumRangeLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(lblPartOnNozzle, "2, 10, right, default");
        
        vacuumLevelPartOnLow = new JTextField();
        panelPartOnVacuumSensing.add(vacuumLevelPartOnLow, "4, 10");
        vacuumLevelPartOnLow.setColumns(10);
        
        vacuumLevelPartOnHigh = new JTextField();
        panelPartOnVacuumSensing.add(vacuumLevelPartOnHigh, "6, 10");
        vacuumLevelPartOnHigh.setColumns(10);
        
        vacuumLevelPartOnReading = new JTextField();
        vacuumLevelPartOnReading.setEditable(false);
        panelPartOnVacuumSensing.add(vacuumLevelPartOnReading, "10, 10, fill, top");
        vacuumLevelPartOnReading.setColumns(10);
        
        lblPartOnDifferenceRange = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.DifferenceRangeLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(lblPartOnDifferenceRange, "2, 12, right, default");
        
        vacuumDifferencePartOnLow = new JTextField();
        vacuumDifferencePartOnLow.setColumns(10);
        panelPartOnVacuumSensing.add(vacuumDifferencePartOnLow, "4, 12, fill, default");
        
        vacuumDifferencePartOnHigh = new JTextField();
        panelPartOnVacuumSensing.add(vacuumDifferencePartOnHigh, "6, 12, fill, default");
        vacuumDifferencePartOnHigh.setColumns(10);
        
        vacuumDifferencePartOnReading = new JTextField();
        vacuumDifferencePartOnReading.setEditable(false);
        panelPartOnVacuumSensing.add(vacuumDifferencePartOnReading, "10, 12, fill, default");
        vacuumDifferencePartOnReading.setColumns(10);
        
        lblLegendPartOn = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOnVacuumSensingPanel.LegendPartOnLabel.text")); //$NON-NLS-1$
        panelPartOnVacuumSensing.add(lblLegendPartOn, "2, 16, right, default");
        
        vacuumPartOnGraph = new SimpleGraphView();
        vacuumPartOnGraph.setFont(new Font("Dialog", Font.PLAIN, 11));
        panelPartOnVacuumSensing.add(vacuumPartOnGraph, "4, 16, 9, 1, default, fill");
        
        panelPartOffVacuumSensing = new JPanel();
        panelPartOffVacuumSensing.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                Translations.getString(
                        "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.Border.title"), //$NON-NLS-1$
                TitledBorder.LEADING, TitledBorder.TOP, null));
        contentPanel.add(panelPartOffVacuumSensing);
        panelPartOffVacuumSensing.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("max(50dlu;default)"),
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("max(50dlu;default)"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),}));
        
        lblPartOffMeasurement = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.MeasurementMethodLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblPartOffMeasurement, "2, 2");
        
        methodPartOff = new JComboBox(ReferenceNozzleTip.VacuumMeasurementMethod.values());
        methodPartOff.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                adaptDialog();
            }
        });
        panelPartOffVacuumSensing.add(methodPartOff, "4, 2, 3, 1");
        
        lblEstablishPartOffLevel = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.EstablishLevelLabel.text")); //$NON-NLS-1$
        lblEstablishPartOffLevel.setToolTipText(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.EstablishLevelLabel.toolTipText")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblEstablishPartOffLevel, "2, 4, right, default");
        
        establishPartOffLevel = new JCheckBox("");
        establishPartOffLevel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                adaptDialog();
            }
        });
        panelPartOffVacuumSensing.add(establishPartOffLevel, "4, 4");
        
        lblPerformPartOffChecks = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.PerformChecksLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblPerformPartOffChecks, "2, 6, right, default");
        
        partOffCheckAfterPlace = new JCheckBox(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.AfterPlaceLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(partOffCheckAfterPlace, "4, 6");
        
        partOffCheckBeforePick = new JCheckBox(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.BeforePickLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(partOffCheckBeforePick, "6, 6");
        
        lblPartOffLowValue = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.LowValueLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblPartOffLowValue, "4, 8");
        
        lblPartOffHighValue = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.HighValueLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblPartOffHighValue, "6, 8");
        
        lblPartOffLastReading = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.LastReadingLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblPartOffLastReading, "10, 8");
        
        lblPartOffNozzle = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.VacuumRangeLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblPartOffNozzle, "2, 10, right, default");
        
        vacuumLevelPartOffLow = new JTextField();
        panelPartOffVacuumSensing.add(vacuumLevelPartOffLow, "4, 10");
        vacuumLevelPartOffLow.setColumns(10);
        
        vacuumLevelPartOffHigh = new JTextField();
        panelPartOffVacuumSensing.add(vacuumLevelPartOffHigh, "6, 10");
        vacuumLevelPartOffHigh.setColumns(10);
        
        vacuumLevelPartOffReading = new JTextField();
        vacuumLevelPartOffReading.setEditable(false);
        panelPartOffVacuumSensing.add(vacuumLevelPartOffReading, "10, 10, fill, default");
        vacuumLevelPartOffReading.setColumns(10);
        
        lblProbingTimePartOff = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.ValveOpenCloseLabel.text")); //$NON-NLS-1$
        lblProbingTimePartOff.setToolTipText(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.ValveOpenCloseLabel.toolTipText")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblProbingTimePartOff, "2, 12, right, default");
        
        partOffProbingMilliseconds = new JTextField();
        panelPartOffVacuumSensing.add(partOffProbingMilliseconds, "4, 12, default, center");
        partOffProbingMilliseconds.setColumns(10);
        
        partOffDwellMilliseconds = new JTextField();
        panelPartOffVacuumSensing.add(partOffDwellMilliseconds, "6, 12, fill, default");
        partOffDwellMilliseconds.setColumns(10);
        
        lblPartOffDifferenceRange = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.DifferenceRangeLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblPartOffDifferenceRange, "2, 14, right, default");
        
        vacuumDifferencePartOffLow = new JTextField();
        panelPartOffVacuumSensing.add(vacuumDifferencePartOffLow, "4, 14, fill, default");
        vacuumDifferencePartOffLow.setColumns(10);
        
        vacuumDifferencePartOffHigh = new JTextField();
        panelPartOffVacuumSensing.add(vacuumDifferencePartOffHigh, "6, 14, fill, default");
        vacuumDifferencePartOffHigh.setColumns(10);
        
        vacuumDifferencePartOffReading = new JTextField();
        vacuumDifferencePartOffReading.setEditable(false);
        panelPartOffVacuumSensing.add(vacuumDifferencePartOffReading, "10, 14, fill, default");
        vacuumDifferencePartOffReading.setColumns(10);
        
        lblLegendPartOff = new JLabel(Translations.getString(
                "ReferenceNozzleTipPartDetectionWizard.PartOffVacuumSensingPanel.LegendPartOnLabel.text")); //$NON-NLS-1$
        panelPartOffVacuumSensing.add(lblLegendPartOff, "2, 18, right, default");
        
        vacuumPartOffGraph = new SimpleGraphView();
        vacuumPartOffGraph.setFont(new Font("Dialog", Font.PLAIN, 11));
        panelPartOffVacuumSensing.add(vacuumPartOffGraph, "4, 18, 9, 1, default, fill");
    }
    
    private JLabel lblPartOnLowValue;
    private JLabel lblPartOnHighValue;
    private JLabel lblPartOnNozzle;
    private JLabel lblPartOffNozzle;
    private JTextField vacuumLevelPartOnLow;
    private JTextField vacuumLevelPartOffLow;
    private JTextField vacuumLevelPartOnHigh;
    private JTextField vacuumLevelPartOffHigh;
    private JLabel lblPartOnMeasurement;
    private JComboBox methodPartOn;
    private JLabel lblPartOffMeasurement;
    private JComboBox methodPartOff;
    private JPanel panelPartOffVacuumSensing;
    private JLabel lblProbingTimePartOff;
    private JTextField partOffProbingMilliseconds;
    private JLabel lblPartOffDifferenceRange;
    private JTextField vacuumDifferencePartOffLow;
    private JTextField vacuumDifferencePartOffHigh;
    private JLabel lblPartOnDifferenceRange;
    private JTextField vacuumDifferencePartOnLow;
    private JTextField vacuumDifferencePartOnHigh;
    private JLabel lblPartOffLowValue;
    private JLabel lblPartOffHighValue;
    private JLabel lblPartOnLastReading;
    private JLabel lblPartOffLastReading;
    private JTextField vacuumLevelPartOnReading;
    private JTextField vacuumDifferencePartOnReading;
    private JTextField vacuumLevelPartOffReading;
    private JTextField vacuumDifferencePartOffReading;
    private SimpleGraphView vacuumPartOnGraph;
    private SimpleGraphView vacuumPartOffGraph;
    private JCheckBox establishPartOnLevel;
    private JLabel lblEstablishPartOnLevel;
    private JLabel lblEstablishPartOffLevel;
    private JCheckBox establishPartOffLevel;
    private JLabel lblLegendPartOn;
    private JLabel lblLegendPartOff;
    private JLabel lblPerformPartOnChecks;
    private JCheckBox partOnCheckAfterPick;
    private JCheckBox partOnCheckAlign;
    private JCheckBox partOnCheckBeforePlace;
    private JLabel lblPerformPartOffChecks;
    private JCheckBox partOffCheckAfterPlace;
    private JCheckBox partOffCheckBeforePick;
    private JTextField partOffDwellMilliseconds;
    
    private void adaptDialog() {
        VacuumMeasurementMethod methodOn = (VacuumMeasurementMethod)methodPartOn.getSelectedItem();
        boolean establishOn = establishPartOnLevel.isSelected();
        boolean partOn = true;
        boolean partOnDifference = true;
        boolean graphOn = establishOn || methodOn.isDifferenceMethod();
        if (methodOn == VacuumMeasurementMethod.None) {
            // hide all 
            partOn = false;
            partOnDifference = false;
            graphOn = false;
        }
        else if (!methodOn.isDifferenceMethod()) {
            partOnDifference = false;
        }
         
        lblEstablishPartOnLevel.setVisible(partOn);
        establishPartOnLevel.setVisible(partOn);
        lblPerformPartOnChecks.setVisible(partOn);
        partOnCheckAfterPick.setVisible(partOn);
        partOnCheckAlign.setVisible(partOn);
        partOnCheckBeforePlace.setVisible(partOn);
        lblPartOnLowValue.setVisible(partOn);
        lblPartOnHighValue.setVisible(partOn);
        lblPartOnLastReading.setVisible(partOn);
        lblPartOnNozzle.setVisible(partOn);
        vacuumLevelPartOnLow.setVisible(partOn);
        vacuumLevelPartOnHigh.setVisible(partOn);
        vacuumLevelPartOnReading.setVisible(partOn);
        
        lblPartOnDifferenceRange.setVisible(partOnDifference);
        vacuumDifferencePartOnLow.setVisible(partOnDifference);
        vacuumDifferencePartOnHigh.setVisible(partOnDifference);
        vacuumDifferencePartOnReading.setVisible(partOnDifference);
        lblLegendPartOn.setVisible(graphOn);
        vacuumPartOnGraph.setVisible(graphOn);
        
        VacuumMeasurementMethod methodOff = (VacuumMeasurementMethod)methodPartOff.getSelectedItem();
        boolean establishOff = establishPartOffLevel.isSelected();
        boolean partOff = true;
        boolean partOffDifference = true;
        boolean graphOff = establishOff || methodOff.isDifferenceMethod();
        if (methodOff == VacuumMeasurementMethod.None) {
            // hide all 
            partOff = false;
            partOffDifference = false;
            graphOff = false;
        }
        else if (!methodOff.isDifferenceMethod()) {
            partOffDifference = false;
        }
         
        lblEstablishPartOffLevel.setVisible(partOff);
        establishPartOffLevel.setVisible(partOff);
        lblPerformPartOffChecks.setVisible(partOff);
        partOffCheckAfterPlace.setVisible(partOff);
        partOffCheckBeforePick.setVisible(partOff);
        lblPartOffLowValue.setVisible(partOff);
        lblPartOffHighValue.setVisible(partOff);
        lblPartOffLastReading.setVisible(partOff);
        lblPartOffNozzle.setVisible(partOff);
        vacuumLevelPartOffLow.setVisible(partOff);
        vacuumLevelPartOffHigh.setVisible(partOff);
        vacuumLevelPartOffReading.setVisible(partOff);
        lblProbingTimePartOff.setVisible(partOff);
        partOffProbingMilliseconds.setVisible(partOff);
        partOffDwellMilliseconds.setVisible(partOff);
        
        lblPartOffDifferenceRange.setVisible(partOffDifference);
        vacuumDifferencePartOffLow.setVisible(partOffDifference);
        vacuumDifferencePartOffHigh.setVisible(partOffDifference);
        vacuumDifferencePartOffReading.setVisible(partOffDifference);
        lblLegendPartOff.setVisible(graphOff);
        vacuumPartOffGraph.setVisible(graphOff);
    }

    @Override
    public void createBindings() {
        DoubleConverter doubleConverter = new DoubleConverter(Configuration.get().getLengthDisplayFormat());
        IntegerConverter integerConverter = new IntegerConverter();

        addWrappedBinding(nozzleTip, "methodPartOn", methodPartOn, "selectedItem");
        addWrappedBinding(nozzleTip, "establishPartOnLevel", establishPartOnLevel, "selected");
        addWrappedBinding(nozzleTip, "partOnCheckAfterPick", partOnCheckAfterPick, "selected");
        addWrappedBinding(nozzleTip, "partOnCheckAlign", partOnCheckAlign, "selected");
        addWrappedBinding(nozzleTip, "partOnCheckBeforePlace", partOnCheckBeforePlace, "selected");
        addWrappedBinding(nozzleTip, "vacuumLevelPartOnLow", vacuumLevelPartOnLow, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumLevelPartOnHigh", vacuumLevelPartOnHigh, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumLevelPartOnReading", vacuumLevelPartOnReading, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumDifferencePartOnLow", vacuumDifferencePartOnLow, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumDifferencePartOnHigh", vacuumDifferencePartOnHigh, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumDifferencePartOnReading", vacuumDifferencePartOnReading, "text", doubleConverter);

        addWrappedBinding(nozzleTip, "methodPartOff", methodPartOff, "selectedItem");
        addWrappedBinding(nozzleTip, "establishPartOffLevel", establishPartOffLevel, "selected");
        addWrappedBinding(nozzleTip, "partOffCheckAfterPlace", partOffCheckAfterPlace, "selected");
        addWrappedBinding(nozzleTip, "partOffCheckBeforePick", partOffCheckBeforePick, "selected");
        addWrappedBinding(nozzleTip, "vacuumLevelPartOffLow", vacuumLevelPartOffLow, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumLevelPartOffHigh", vacuumLevelPartOffHigh, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumLevelPartOffReading", vacuumLevelPartOffReading, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "partOffProbingMilliseconds", partOffProbingMilliseconds, "text", integerConverter);
        addWrappedBinding(nozzleTip, "partOffDwellMilliseconds", partOffDwellMilliseconds, "text", integerConverter);
        addWrappedBinding(nozzleTip, "vacuumDifferencePartOffLow", vacuumDifferencePartOffLow, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumDifferencePartOffHigh", vacuumDifferencePartOffHigh, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumDifferencePartOffReading", vacuumDifferencePartOffReading, "text", doubleConverter);
        addWrappedBinding(nozzleTip, "vacuumPartOnGraph", vacuumPartOnGraph, "graph");
        addWrappedBinding(nozzleTip, "vacuumPartOffGraph", vacuumPartOffGraph, "graph");

        ComponentDecorators.decorateWithAutoSelect(partOffProbingMilliseconds);
        ComponentDecorators.decorateWithAutoSelect(partOffDwellMilliseconds);

        ComponentDecorators.decorateWithAutoSelect(vacuumLevelPartOnLow);
        ComponentDecorators.decorateWithAutoSelect(vacuumLevelPartOnHigh);
        ComponentDecorators.decorateWithAutoSelect(vacuumDifferencePartOnLow);
        ComponentDecorators.decorateWithAutoSelect(vacuumDifferencePartOnHigh);
        ComponentDecorators.decorateWithAutoSelect(vacuumLevelPartOffLow);
        ComponentDecorators.decorateWithAutoSelect(vacuumLevelPartOffHigh);
        ComponentDecorators.decorateWithAutoSelect(vacuumDifferencePartOffLow);
        ComponentDecorators.decorateWithAutoSelect(vacuumDifferencePartOffHigh);

        adaptDialog();
    }
}
