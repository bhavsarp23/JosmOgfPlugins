// || Swami-Shriji ||

package jtsjosm;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.tools.Shortcut;

// import jtsjosm.JtsJosmPlugin;

import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Tag;
import org.openstreetmap.josm.data.osm.TagCollection;
import org.openstreetmap.josm.data.osm.TagMap;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.RelationMemberData;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;


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
import java.lang.management.BufferPoolMXBean;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.operation.buffer.OffsetCurveBuilder;
import org.locationtech.jts.geom.PrecisionModel;

public class JtsJosmAction extends JosmAction {

    public JtsJosmAction() {
        super(tr("JtsExampleAction"),
              "JtsExampleAction",
              tr("JtsExampleAction"),
              Shortcut.registerShortcut(
                "jts:jts",
                "JtsExampleAction",
                java.awt.event.KeyEvent.VK_9,
                Shortcut.CTRL_SHIFT
              ),
              false);
    }

    // This method produces a node from a coordinate
    public Node coordinateToNode(Coordinate coordinate) {
        return new Node(new EastNorth(coordinate.getX(), coordinate.getY()));
    }

    // This method produces a way from a list of coordinates
    public Way coordinatesToWay(List<Coordinate> coordinates) {
        Way w = new Way();
        for(Coordinate c : coordinates) {
            w.addNode(coordinateToNode(c));
        }
        return w;
    }

    // This method produces a closed way from a list of coordinates
    // It adds the first node twice - at the beginning and at the end
    public Way coordinatesToClosedWay(List<Coordinate> coordinates) {
        Way w = new Way();
        for(Coordinate c : coordinates) {
            w.addNode(coordinateToNode(c));
        }
        w.addNode(w.getNodes().get(0));
        return w;
    }

    // This method produces a way from a LineString
    public Way lineStringToWay(LineString ls) {
        Coordinate[] coordinates = ls.getCoordinates();
        List<Coordinate> coordinateList = Arrays.asList(coordinates);
        return coordinatesToWay(coordinateList);
    }

    // Node to coordinate
    public Coordinate nodeToCoordinate(Node n) {
        EastNorth en = n.getEastNorth();
        return new Coordinate(en.getX(), en.getY());
    }

    // Way to LineString
    public LineString wayToLineString(Way w) {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        for(Node n : w.getNodes()) {
            coordinates.add(nodeToCoordinate(n));
        }
        Coordinate[] coordinateArray = new Coordinate[coordinates.size()];
        coordinates.toArray(coordinateArray);
        return new GeometryFactory().createLineString(coordinateArray);
    }

    // Way to Polygon
    public Polygon wayToPolygon(Way w) {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        for(Node n : w.getNodes()) {
            coordinates.add(nodeToCoordinate(n));
        }
        Coordinate[] coordinateArray = new Coordinate[coordinates.size()];
        coordinates.toArray(coordinateArray);
        return new GeometryFactory().createPolygon(coordinateArray);
    }

    //  Polygon to Way
    public Way polygonToWay(Polygon p) {
        Coordinate[] coordinates = p.getCoordinates();
        List<Coordinate> coordinateList = Arrays.asList(coordinates);
        return coordinatesToClosedWay(coordinateList);
    }

    public Way offsetWay(Way w, double distance, boolean rightSideOnly) {
        LineString ls = wayToLineString(w);
        Geometry g;
        BufferParameters bp = new BufferParameters();
        bp.setSingleSided(true);
        PrecisionModel pm = new PrecisionModel();
        OffsetCurveBuilder ocb = new OffsetCurveBuilder(pm, bp);
        // Convert linestring to array of coordinates
        Coordinate[] coordinates = ls.getCoordinates();

        // Get the offset curve
        Coordinate[] offsetCoordinates = ocb.getOffsetCurve(coordinates, distance);

        // Convert the offset curve to a way
        List<Coordinate> coordinateList = Arrays.asList(offsetCoordinates);
        return coordinatesToWay(coordinateList);
    }


    public Way bufferWay(Way w, double distance, boolean rightSideOnly) {
        LineString ls = wayToLineString(w);
        Geometry g;
        if (rightSideOnly) {
            g = ls.buffer(distance, 1, BufferParameters.CAP_SQUARE);
        } else {
            g = ls.buffer(distance, 1, BufferParameters.CAP_SQUARE);
        }
        if (g instanceof Polygon) {
            return polygonToWay((Polygon) g);
        }
        return null;
    }

    public Node interpolate(Way w, double interpolatedDist) {
        LineString ls = wayToLineString(w);
        Coordinate[] coordinates = ls.getCoordinates();
        // Edge cases
        // If distance is 0, return the first node
        if (interpolatedDist == 0) {
            return coordinateToNode(coordinates[0]);
        }
        // If distance is greater than length of linestring, return the last
        // node
        if (interpolatedDist >= ls.getLength()) {
            return coordinateToNode(coordinates[coordinates.length-1]);
        }

        double cumulativeDist = 0;
        double currentDist, intermediateDist, x, y;
        Coordinate interpolatedCoord = new Coordinate(0.0,0.0);
        for (int i = 0; i < coordinates.length - 1; i++) {
            // Calculate the distance between the current coordinate and the
            // next coordinate
            currentDist = coordinates[i].distance(coordinates[i+1]);

            if (cumulativeDist + currentDist > interpolatedDist) {
                // Subtract the cumulative distance from the interpolated
                // distance
                intermediateDist = interpolatedDist - cumulativeDist;

                System.out.println("Current dist: " + currentDist);
                System.out.println("Interm dist: " + intermediateDist);
                System.out.println("Interp dist: " + interpolatedDist);
                System.out.println("Cumu dist: " + cumulativeDist);

                // Linearly interpolate between the two coordinates using the
                // interpolated distance
                x = (coordinates[i+1].getX() - coordinates[i].getX()) *
                    intermediateDist / currentDist + coordinates[i].getX();
                y = (coordinates[i+1].getY() - coordinates[i].getY()) *
                    intermediateDist / currentDist + coordinates[i].getY();
                System.out.println("--------------5" + x + "," + y);
                interpolatedCoord.setX(x);
                interpolatedCoord.setY(y);
                break;
            }
            cumulativeDist += currentDist;

        }
        System.out.println("--------------" + interpolatedCoord.getX() + interpolatedCoord.getY());
        return coordinateToNode(interpolatedCoord);
    }

    public List<Node> interpolateWayByDistance(Way w, double distance) {
        List<Node> interpolatedNodes = new ArrayList<Node>();
        LineString ls = wayToLineString(w);
        Coordinate[] coordinates = ls.getCoordinates();
        for (int i = 0; i < coordinates.length - 1; i++) {
            Coordinate c1 = coordinates[i];
            Coordinate c2 = coordinates[i + 1];
            double dx = c2.x - c1.x;
            double dy = c2.y - c1.y;
            double length = Math.sqrt(dx * dx + dy * dy);
            int numInterpolatedNodes = (int) Math.floor(length / distance);
            for (int j = 0; j < numInterpolatedNodes; j++) {
                double x = c1.x + dx * j / numInterpolatedNodes;
                double y = c1.y + dy * j / numInterpolatedNodes;
                Node n = coordinateToNode(new Coordinate(x, y));
                interpolatedNodes.add(n);
            }
        }
        return interpolatedNodes;
    }

    public Node translateNode(Node n, double x, double y) {
        Coordinate c  = nodeToCoordinate(n);
        c.setX(c.getX() + x);
        c.setY(c.getY() + y);
        return coordinateToNode(c);
    }

    public Way generateRectangle(Node center, double width, double height, double angle) {
        Coordinate c = nodeToCoordinate(center);
        Coordinate[] coordinates = new Coordinate[4];
        coordinates[0] = new Coordinate(c.x - width/2, c.y - height/2);
        coordinates[1] = new Coordinate(c.x + width/2, c.y - height/2);
        coordinates[2] = new Coordinate(c.x + width/2, c.y + height/2);
        coordinates[3] = new Coordinate(c.x - width/2, c.y + height/2);

        // Rotate the coordinates by angle in radians
        for (int i = 0; i < 4; i++) {
            double x = coordinates[i].x - c.x;
            double y = coordinates[i].y - c.y;
            coordinates[i].x = x * Math.cos(angle) - y * Math.sin(angle) + c.x;
            coordinates[i].y = x * Math.sin(angle) + y * Math.cos(angle) + c.y;
        }

        return coordinatesToClosedWay(Arrays.asList(coordinates));
    }

    public void actionPerformed(ActionEvent e) {
        // Get selected ways
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Way> selectedWays = ds.getSelectedWays();

        Way way = selectedWays.iterator().next();
        Node n = interpolate(way, 34.0);
        AddCommand ac = new AddCommand(ds, n);
        UndoRedoHandler.getInstance().add(ac);

        double cumulativeDist = 0;
        while (cumulativeDist < way.getLength()) {
            n = interpolate(way, cumulativeDist);
            cumulativeDist += 100.0*Math.random();
            ac = new AddCommand(ds, n);
            UndoRedoHandler.getInstance().add(ac);
        }

        // // Offset the selected ways by a distance of 7.5
        // for (Way w : selectedWays) {
        //     Way offsetWay = offsetWay(w, 7.5, true);
        //     // Interpolate the offset way
        //     List<Node> interpolatedNodes = interpolateWayByDistance(offsetWay, 50.0);
        //     // Add the interpolated nodes to the dataset
        //     for (Node n : interpolatedNodes) {
        //         AddCommand acn = new AddCommand(ds, n);
        //         UndoRedoHandler.getInstance().add(acn);
        //     }
        //     // Add nodes to the dataset
        //     for (Node n : offsetWay.getNodes()) {
        //         AddCommand acn = new AddCommand(ds, n);
        //         UndoRedoHandler.getInstance().add(acn);
        //     }
        //     AddCommand ac = new AddCommand(ds, offsetWay);
        //     UndoRedoHandler.getInstance().add(ac);

        //     Node n = interpolate(w, 100.0);
        //     ac = new AddCommand(ds, n);
        //     UndoRedoHandler.getInstance().add(ac);
        // }
    }
}
