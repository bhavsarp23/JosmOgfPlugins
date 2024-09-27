// || Swami-Shriji ||
package makebuilding;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

import org.openstreetmap.josm.gui.MainApplication;
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
import org.openstreetmap.josm.data.coor.LatLon;

import jtsjosm.JtsJosmAction;

public class GenerateBuildingAction extends JosmAction {

    private BuildingDialogAction buildingDialog;

    public GenerateBuildingAction(BuildingDialogAction buildingDialog) {
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
        this.buildingDialog = buildingDialog;

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

        double avgWidth = buildingDialog.width;
        double avgDepth = buildingDialog.depth;
        double avgSetback = buildingDialog.setback;
        JtsJosmAction jtsAction = new JtsJosmAction();

        Way owr = jtsAction.offsetWay(w, 10, true);
        Way owl = jtsAction.offsetWay(w, -10, true);
        double cumulativeDist = avgWidth;
        double rightLength = owr.getLength();
        double leftLength = owl.getLength();
        double angle, depth, dx, dy;
        List<Way> ways = new ArrayList<Way>();

        // Right side first
        if (buildingDialog.rightSide) {
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
                Way previousWay = new Way();
                if(ways.size() > 1){
                    previousWay = ways.get(ways.size()-1);
                }
                if (jtsAction.overlaps(way, previousWay)) {
                    Way newWay;
                    // 50 / 50 union vs difference
                    if (Math.random() > 0.75) {
                        newWay = jtsAction.subtract(way, previousWay);
                    } else {
                        newWay = jtsAction.union(previousWay, way);
                        // Remove the old way
                        ways.remove(previousWay);
                    }
                    newWay.put("building", "yes");
                    ways.add(newWay);
                } else {
                    way.put("building", "yes");
                    ways.add(way);
                }
                cumulativeDist += buildingDialog.lotSpacing;

            }
        }
        // Reset
        cumulativeDist = 0;

        if (buildingDialog.leftSide) {
            while (cumulativeDist < leftLength) {
                List<Node> nodes = new ArrayList<Node>();
                // Set front nodes
                // double setbackVariance = 3*(Math.random()-0.5);
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
                Way previousWay = new Way();
                if(ways.size() > 1){
                    previousWay = ways.get(ways.size()-1);
                }
                if (jtsAction.overlaps(way, previousWay)) {
                    Way newWay;
                    // 25 / 75 union vs difference
                    if (Math.random() > 0.75) {
                        newWay = jtsAction.subtract(way, previousWay);
                    } else {
                        newWay = jtsAction.union(previousWay, way);
                        // Remove the old way
                        ways.remove(previousWay);
                    }
                    newWay.put("building", "yes");
                    ways.add(newWay);
                } else {
                    way.put("building", "yes");
                    ways.add(way);
                }
                cumulativeDist += buildingDialog.lotSpacing;
            }
        }
        return ways;
    }

    public int getGridHash(Way way, int gridX, int gridY) {
        // Get the bounding box

        // Subdivide bounding box into smaller boxes gridX x gridY wide

        // Assign the way a box hash given its centroid


        return 0;
    }

    public boolean buildingObstructsWithWaysFast(Way building, Collection<Way> ways) {
        // Get the x displacement and y displacement of the building

        // Assign each way in ways a hash based on the x & y displacements

        // Get the adjacent hashes

        // Get the ways in those adjacent hashes

        // Check if a building obstructs with any of the ways
        return false;
    }

    public boolean intersects(Way w1, Way w2) {

        List<Node> nodes1 = w1.getNodes();
        List<Node> nodes2 = w2.getNodes();

        Geometry.PolygonIntersection pi = Geometry.polygonIntersection(nodes1, nodes2);

        if (pi == Geometry.PolygonIntersection.OUTSIDE) {
            return false;
        }
        return true;
    }

    public Node getCentroid(Way way) {
        if (way == null || way.getNodesCount() == 0) {
            return null; // Handle case where way is null or has no nodes
        }

        double sumLat = 0.0;
        double sumLon = 0.0;

        for (Node node : way.getNodes()) {
            sumLat += node.getCoor().lat();
            sumLon += node.getCoor().lon();
        }

        double centroidLat = sumLat / way.getNodesCount();
        double centroidLon = sumLon / way.getNodesCount();

        return new Node(new LatLon(centroidLat, centroidLon));
    }

    boolean buildingObstructsWithWays(Way building, SpatialHashGrid hashGrid) {
        JtsJosmAction jtsAction = new JtsJosmAction();

        List<Way> nearbyWays = hashGrid.getAdjacentWays(building);

        for (Way way : nearbyWays) {
            if (building == way) {
                continue;
            }
            if (jtsAction.overlaps (building, way)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Get selected ways
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Way> selected = ds.getSelectedWays();
        Collection<Command> commands = new ArrayList<Command>();
        Node center = new Node(MainApplication.getMap().mapView.getCenter());
        SpatialHashGrid hashGrid = new SpatialHashGrid(center, 30, 1000, 1000);

        // Get selected way
        float angle = 0;

        for (Way way : selected) {
            // Generate parallel nodes of the way
            // List<Node> parallelNodes = generateParallelNodes(way);

            List<Way> buildings = new ArrayList<Way>();
            int index = 0;
            double width, length, setback;

            buildings.addAll(iterativeBuildings(way));

            // Add all buildings to hash grid
            for(Way building : buildings) {
                hashGrid.addToHash(building);
            }

            // Add nodes of the building to dataset
            for (Way building : buildings) {
                if (buildingObstructsWithWays(building, hashGrid)) {
                    // 50 / 50 whether to union it or difference it

                }

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
