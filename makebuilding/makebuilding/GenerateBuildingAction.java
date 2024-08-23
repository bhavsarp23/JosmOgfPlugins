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

    public Way generateBuilding(Node center, double width, double length, double angle, double setback) {
        // Generate a rectangle using jts josm plugin
        JtsJosmAction jtsAction = new JtsJosmAction();

        // Setback calculation
        double dx, dy;
        dy = setback * Math.cos(angle);
        dx = setback * Math.sin(angle);
        Node b = jtsAction.translateNode(center, dx, dy);

        Way buildingWay = jtsAction.generateRectangle(b, width, length, angle);

        buildingWay.put("building", "yes");

        return buildingWay;
    }

    public float getAngle(Node node1, Node node2) {
        float angle = (float) Math.atan2(node2.getCoor().lat() - node1.getCoor().lat(), node2.getCoor().lon() - node1.getCoor().lon());
        return angle;
    }

    public List<Node> randomInterpolation(Way way, double avgDist) {
        List<Node> interpolatedNodes = new ArrayList<Node>();
        JtsJosmAction jtsAction = new JtsJosmAction();
        double cumulativeDist = avgDist * (Math.random()+.5);
        double length = way.getLength();

        while (cumulativeDist < length) {
            interpolatedNodes.add(jtsAction.interpolate(way, cumulativeDist));
            cumulativeDist += avgDist * (Math.random()+.5);
        }
        return interpolatedNodes;
    }

    public List<Node> generateParallelNodes(Way way) {
        JtsJosmAction jtsAction = new JtsJosmAction();
        // Generate parallel ways
        // Distance of 10 is hardcoded
        Way offsetWayRight = jtsAction.offsetWay(way, 20, true);
        Way offsetWayLeft = jtsAction.offsetWay(way, -20, true);

        // Interpolate ways randomly
        List<Node> offsetNodesRight =
                   randomInterpolation(offsetWayRight, 20.0);
        List<Node> offsetNodesLeft =
                   randomInterpolation(offsetWayLeft, 20.0);

        // Combine lists
        List<Node> offsetNodes = new ArrayList<Node>(
            offsetNodesLeft.size() + offsetNodesRight.size());
        offsetNodes.addAll(offsetNodesRight);
        offsetNodes.addAll(offsetNodesLeft);

        return offsetNodes;
    }

    public List<Way> iterativeBuildings(Way w) {

        double avgWidth = 7.5;
        double avgDepth = 10;
        double avgSetback = 2;
        JtsJosmAction jtsAction = new JtsJosmAction();

        Way owr = jtsAction.offsetWay(w, 10, true);
        Way owl = jtsAction.offsetWay(w, -10, true);
        double cumulativeDist = avgWidth;
        double rightLength = owr.getLength();
        double leftLength = owl.getLength();
        double angle, depth, dx, dy;
        List<Way> ways = new ArrayList<Way>();

        // Right side first
        while (cumulativeDist < rightLength) {
            List<Node> nodes = new ArrayList<Node>();
            // Set front nodes
            nodes.add(jtsAction.interpolate(owr, cumulativeDist));
            cumulativeDist += avgWidth*(Math.random()+0.5);
            nodes.add(jtsAction.interpolate(owr, cumulativeDist));
            // Set back nodes
            // Get angle between nodes 0 & 1
            angle = getAngle(nodes.get(0), nodes.get(1));
            // Set a random depth
            depth = avgDepth*(Math.random()+0.5);
            // Translate node 0 & 1 by depth & angle
            dx = depth*Math.cos(angle + Math.PI/2);
            dy = depth*Math.sin(angle + Math.PI/2);
            nodes.add(jtsAction.translateNode(nodes.get(1), dx, dy));
            nodes.add(jtsAction.translateNode(nodes.get(0), dx, dy));
            nodes.add(nodes.get(0));

            // Add nodes to way
            Way way = new Way();
            way.setNodes(nodes);
            way.put("building", "yes");
            ways.add(way);

        }
        // Reset
        cumulativeDist = 0;

        while (cumulativeDist < leftLength) {
            List<Node> nodes = new ArrayList<Node>();
            // Set front nodes
            nodes.add(jtsAction.interpolate(owl, cumulativeDist));
            cumulativeDist += avgWidth*(Math.random()+0.5);
            nodes.add(jtsAction.interpolate(owl, cumulativeDist));
            // Set back nodes
            // Get angle between nodes 0 & 1
            angle = getAngle(nodes.get(0), nodes.get(1));
            // Set a random depth
            depth = avgDepth*(Math.random()+0.5);
            // Translate node 0 & 1 by depth & angle
            dx = -1*depth*Math.cos(angle + Math.PI/2);
            dy = -1*depth*Math.sin(angle + Math.PI/2);
            nodes.add(jtsAction.translateNode(nodes.get(1), dx, dy));
            nodes.add(jtsAction.translateNode(nodes.get(0), dx, dy));
            nodes.add(nodes.get(0));

            // Add nodes to way
            Way way = new Way();
            way.setNodes(nodes);
            way.put("building", "yes");
            ways.add(way);

        }

        return ways;

    }

    public void actionPerformed(ActionEvent e) {

        // Get selected ways
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Way> selected = ds.getSelectedWays();
        Collection<Command> commands = new ArrayList<Command>();

        // Get selected way
        float angle = 0;

        for (Way selectedWay : selected) {
            // Generate parallel nodes of the way
            List<Node> parallelNodes = generateParallelNodes(selectedWay);

            List<Way> buildings = new ArrayList<Way>(parallelNodes.size()-2);
            int index = 0;
            double width, length, setback;
            // for (Node node : parallelNodes) {
            //     index++;
            //     // Skip if node is the first item
            //     if (index == 0) {
            //         continue;
            //     }
            //     // End if it is the last item
            //     if (index == parallelNodes.size() - 1 ) {
            //         break;
            //     }
            //     // Get the angle of the node before and after
            //     angle = getAngle(parallelNodes.get(index-1), parallelNodes.get(index+1));

            //     // Generate a rectangle about the node and at the angle
            //     width = 7.5 * (Math.random()+.5);
            //     length = 10 * (Math.random()+.5);
            //     setback = 5 * (Math.random() - 0.5);

            //     buildings.add(generateBuilding(node, width, length, angle, setback));
            // }

            for (Way way : selected) {
                buildings.addAll(iterativeBuildings(way));
            }

            // Add nodes of the building to dataset
            for (Way building : buildings) {
                List<Node> nodes = building.getNodes();
                System.out.println("------------------" + nodes);
                for (int i = 0; i < nodes.size() - 1; i++) {
                    commands.add(new AddCommand(ds, nodes.get(i)));
                }
                // for (Node node : building.getNodes()) {
                //     commands.add(new AddCommand(ds, node));
                // }
                // Add the building way to dataset
                commands.add(new AddCommand(ds, building));
            }
        }


        SequenceCommand sequenceOfCommands = new SequenceCommand("Generate buildings", commands, true);
        UndoRedoHandler.getInstance().add(sequenceOfCommands);
    }
}
