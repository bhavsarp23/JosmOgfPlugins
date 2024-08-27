// || Swami-Shriji ||
package addsidewalks;

import static org.openstreetmap.josm.tools.I18n.tr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.Random;
import java.awt.geom.Point2D;
import java.util.Set;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.Tag;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.data.validation.tests.CrossingWays;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.RemoveNodesCommand;

import jtsjosm.JtsJosmAction;


import org.openstreetmap.josm.gui.ExtendedDialog;

public class SidewalkDialogAction extends JosmAction {

    // Distance between the sidewalk and the road
    public static double distance;

    // Sides of road
    public static boolean rightSide, leftSide;

    // Strings
    public static String key, value;

    public SidewalkDialogAction() {
        super(tr("Sidewalk Configuration"),
            "Sidewalk Configuration",
            tr("Sidewalk Configuration"),
            null,
            false
            );

        // Default values
        distance = 7.5;
        rightSide = true;
        leftSide = true;
        key = "highway";
        value = "path";
    }

    private void showPopOutDialog(Frame parent) {
        // Create the dialog
        JDialog dialog = new JDialog(parent, "Pop-Out Dialog", false);
        dialog.setSize(300, 150);
        //dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        // Create a panel for the content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(2, 4));

        JLabel distanceLabel = new JLabel("Distance: ");
        SpinnerModel distanceModel = new SpinnerNumberModel(7.5, 0.0, 50, 0.5);
        JSpinner distanceSpinner = new JSpinner(distanceModel);

        JCheckBox rightBox = new JCheckBox("Right side", true);
        JCheckBox leftBox = new JCheckBox("Left side", true);

        JLabel keyLabel = new JLabel("Key: ");
        JTextField keyField = new JTextField("highway");

        JLabel valueLabel = new JLabel("Value: ");
        JTextField valueField = new JTextField("path");

        contentPanel.add(distanceLabel);
        contentPanel.add(distanceSpinner);
        contentPanel.add(rightBox);
        contentPanel.add(leftBox);
        contentPanel.add(keyLabel);
        contentPanel.add(keyField);
        contentPanel.add(valueLabel);
        contentPanel.add(valueField);

        // Add a button to close the dialog
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                distance = (double) distanceSpinner.getValue();
                rightSide = rightBox.isSelected();
                leftSide = leftBox.isSelected();
                key = keyField.getText();
                value = valueField.getText();
                dialog.dispose();
            }
        }
        );

        // Add components to the dialog
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(okButton, BorderLayout.SOUTH);

        // Show the dialog
        dialog.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Main Frame");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);

        // Create and show the pop-out dialog
        showPopOutDialog(frame);
    }
}
