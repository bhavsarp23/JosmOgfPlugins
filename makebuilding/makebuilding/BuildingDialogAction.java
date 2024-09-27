// || Swami-Shriji ||
package makebuilding;

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

public class BuildingDialogAction extends JosmAction {

  public static double width, depth, lotSpacing, setback;
  public static boolean rightSide, leftSide;

  public BuildingDialogAction() {
              super(tr("Building Configuration"),
                          "Building Configuration",
                          tr("Building Configuration"),
                          Shortcut.registerShortcut(
                              "buildingconfig:tohighway",
                              "Building Configuration",
                              java.awt.event.KeyEvent.VK_B,
                              Shortcut.CTRL_SHIFT
                          ),
                          false);

    // Default values
    width = 8;
    depth = 17;
    lotSpacing = 5;
    setback = 8;
    rightSide = true;
    leftSide = true;

  }

  private static void showPopOutDialog(Frame parent) {
      // Create the dialog
      JDialog dialog = new JDialog(parent, "Pop-Out Dialog", false); // 'false' for non-modal
      dialog.setSize(300, 150);
      //dialog.setLocationRelativeTo(parent);
      dialog.setLayout(new BorderLayout());

      // Create a panel for the content
      JPanel contentPanel = new JPanel();
      contentPanel.setLayout(new GridLayout(2, 5));

      // Add a label and a text field
      JLabel depthLabel = new JLabel("Depth");
      SpinnerModel depthModel = new SpinnerNumberModel(0.0, 0.0, 50, 0.5);
      JSpinner depthSpinner = new JSpinner(depthModel);

      JLabel widthLabel = new JLabel("Width");
      SpinnerModel widthModel = new SpinnerNumberModel(0.0, 0.0, 50, 0.5);
      JSpinner widthSpinner = new JSpinner(widthModel);

      JLabel setbackLabel = new JLabel("Setback");
      SpinnerModel setbackModel = new SpinnerNumberModel(0.0, 0.0, 50, 0.5);
      JSpinner setbackSpinner = new JSpinner(setbackModel);

      JLabel lotSpacingLabel = new JLabel("Lot Spacing");
      SpinnerModel lotSpacingModel = new SpinnerNumberModel(0.0, 0.0, 50, 0.5);
      JSpinner lotSpacingSpinner = new JSpinner(lotSpacingModel);


      JCheckBox rBox = new JCheckBox("Right side");
      JCheckBox lBox = new JCheckBox("Left side");

      contentPanel.add(depthLabel);
      contentPanel.add(depthSpinner);

      contentPanel.add(widthLabel);
      contentPanel.add(widthSpinner);

      contentPanel.add(setbackLabel);
      contentPanel.add(setbackSpinner);

      contentPanel.add(lotSpacingLabel);
      contentPanel.add(lotSpacingSpinner);

      contentPanel.add(rBox);
      contentPanel.add(lBox);



      // Add a button to close the dialog
      JButton okButton = new JButton("OK");
      okButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              // Get the value of the spinner
              width = (double) widthSpinner.getValue();
              depth = (double) depthSpinner.getValue();
              setback = (double) setbackSpinner.getValue();
              lotSpacing = (double) lotSpacingSpinner.getValue();
              rightSide = rBox.isSelected();
              leftSide = lBox.isSelected();


              dialog.dispose(); // Close the dialog
          }
      });

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

    return;
  }
}
