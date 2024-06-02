// || Swami-Shriji ||
package makebuilding;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
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

public class GenerateBuildingAction extends JosmAction {

    public GenerateBuildingAction() {
            super(tr("Generate Buildings To Highway"),
                        "Generate Buildings To Highway",
                        tr("Generate Buildings To Highway"),
                        Shortcut.registerShortcut(
                            "generatebuildings:tohighway",
                            "Generate buildings to highway",
                            java.awt.event.KeyEvent.VK_B,
                            Shortcut.CTRL_SHIFT
                        ),
                        false);

    }

    public Way generateBuilding(Node center, float width, float height, float angle) {
        // Generate a rectangle using jts josm plugin
        JtsJosmAction jtsAction = new JtsJosmAction();
        Way buildingWay = jtsAction.generateRectangle(center, width, height, angle);

        buildingWay.put("building", "yes");

        return buildingWay;
    }

    public float getAngle(Node node1, Node node2) {
        float angle = (float) Math.atan2(node2.getCoor().lat() - node1.getCoor().lat(), node2.getCoor().lon() - node1.getCoor().lon());
        return angle;
    }


    public void actionPerformed(ActionEvent e) {

        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Way> selected = ds.getSelectedWays();
        Collection<Command> commands = new ArrayList<Command>();

        // Obtain the coordinates of the cursor
        Point2D mousePosition = MainApplication.getMap().mapView.getMousePosition();
        EastNorth mousePositionEn;

        // If the cursor is not on the map, return the center of the map
        if (mousePosition == null) {
            // Return the center of the map
            mousePositionEn = MainApplication.getMap().mapView.getCenter();
            // Convert EastNorth to Point2D
            mousePosition = MainApplication.getMap().mapView.getPoint2D(mousePositionEn);
        }

        // Get selected way
        Way selectedWay = selected.iterator().next();
        float angle = 0;

        // If null, return 0
        if (selectedWay == null) {
            angle = 0;
        } else {
            // Get the angle of the selected way
            angle = getAngle(selectedWay.firstNode(),selectedWay.lastNode());
        }

        // Create a node at the cursor
        Node center = new Node(MainApplication.getMap().mapView.getLatLon(mousePosition.getX(), mousePosition.getY()));

        // Generate a building
        Way building = generateBuilding(center, 10, 10, angle);

        // Add nodes of the building to dataset
        for (Node node : building.getNodes()) {
            commands.add(new AddCommand(ds, node));
        }
        
        // Add the building way to dataset
        commands.add(new AddCommand(ds, building));
        
        SequenceCommand sequenceOfCommands = new SequenceCommand("Generate buildings", commands, true);
        UndoRedoHandler.getInstance().add(sequenceOfCommands);
    }
}