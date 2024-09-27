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
import java.util.Formatter;
import java.io.*;
import java.lang.management.BufferPoolMXBean;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.operation.buffer.OffsetCurveBuilder;
import org.locationtech.jts.geom.PrecisionModel;

public class JtsJosmAction extends JosmAction {

        static java.util.logging.Logger logger;

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
        this.logger =  java.util.logging.Logger.getLogger(this.getClass().getName());
        // java.util.logging.FileHandler logFile = new java.util.logging.FileHandler("jtsjosm.log");
        // this.logger.addHandler(logFile);

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

    public LinearRing wayToLinearRing(Way w) {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        for (Node n : w.getNodes()) {
            coordinates.add(nodeToCoordinate(n));
        }
        Coordinate[] coordinateArray = new Coordinate[coordinates.size()];
        coordinates.toArray(coordinateArray);
        return new GeometryFactory().createLinearRing(coordinateArray);
    }

    //  Polygon to Way
    public Way polygonToWay(Polygon p) {
        Coordinate[] coordinates = p.getCoordinates();
        List<Coordinate> coordinateList = Arrays.asList(coordinates);
        System.out.println("Num of coords in polygon: " + coordinateList.size());
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

    public Way dilateWay(Way w, double distance, boolean rightSideOnly) {
        LineString ls = wayToLineString(w);
        BufferParameters bp = new BufferParameters(5, BufferParameters.CAP_FLAT,
                                                   BufferParameters.JOIN_MITRE,
                                                   1.0);
        PrecisionModel pm = new PrecisionModel();
        OffsetCurveBuilder ocb = new OffsetCurveBuilder(pm, bp);
        Coordinate[] dwCoords = ocb.getRingCurve(ls.getCoordinates(), 1, distance);
        return coordinatesToWay(Arrays.asList(dwCoords));
    }

    public boolean overlaps(Way w1, Way w2) {
        // Check if w1 & w2 are closed
        if (!w1.isClosed()) {
            return false;
        }
        if (!w2.isClosed()) {
            return false;
        }

        // Convert w1 & w2 to polygons
        Polygon p1 = wayToPolygon(w1);
        Polygon p2 = wayToPolygon(w2);

        // Check for overlaps
        return p1.overlaps(p2);
    }

    public Way bufferWay(Way w, double distance, boolean rightSideOnly) {
        Geometry ls;
        if (w.isClosed()) {
            ls = wayToPolygon(w);
            this.logger.info("Converting Way %d to polygon.");
        } else {
            ls = wayToLineString(w);
            this.logger.info("Converting Way %d to linestring.");
        }
        //Geometry g;
        // if (rightSideOnly) {
        //g = ls.buffer(distance, 10, BufferParameters.CAP_FLAT);
        // }
        BufferParameters bp = new BufferParameters(5, BufferParameters.CAP_FLAT,
                                                   BufferParameters.JOIN_MITRE,
                                                   BufferParameters.DEFAULT_MITRE_LIMIT);
        BufferOp ops = new BufferOp(ls, bp);
        Geometry g = ops.getResultGeometry(distance);
        if (g instanceof Polygon) {
            // Get an interior ring of the polygon
            return polygonToWay((Polygon) g);
        }
        this.logger.fine("Buffer did not return a polygon; returning null.");
        return null;
    }

    public Coordinate interpolate(LineString ls, double interpolatedDist) {
        Coordinate[] coordinates = ls.getCoordinates();
        // Edge cases
        // If distance is 0, return the first node
        if (interpolatedDist == 0) {
            return coordinates[0];
        }
        // If distance is greater than length of linestring, return the last
        // node
        if (interpolatedDist >= ls.getLength()) {
            return coordinates[coordinates.length-1];
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
        return interpolatedCoord;
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
                // Linearly interpolate between the two coordinates using the
                // interpolated distance
                x = (coordinates[i+1].getX() - coordinates[i].getX()) *
                    intermediateDist / currentDist + coordinates[i].getX();
                y = (coordinates[i+1].getY() - coordinates[i].getY()) *
                    intermediateDist / currentDist + coordinates[i].getY();
                interpolatedCoord.setX(x);
                interpolatedCoord.setY(y);
                break;
            }
            cumulativeDist += currentDist;

        }
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

    public double getDistanceAlongLineString(Coordinate c, LineString ls) {
        double distance = 0.0;
        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(c);
        // Iterate through all of the line segments in the linestring
        for (int i = 0; i < ls.getNumPoints() - 1; i++) {
            Coordinate p1 = ls.getCoordinateN(i);
            Coordinate p2 = ls.getCoordinateN(i+1);
            LineSegment lineseg = new LineSegment(p1,p2);
            // Check if the current segment contains the line
            if(lineseg.toGeometry(gf).contains(p)) {
                // Add the distance to from c and p1 to the total distance and
                // return
                return (distance + p1.distance(c));

            } else {
                // If not, add the total distance of the line segment to the
                // distance total
                distance += lineseg.getLength();
            }
        }
        return 0.0;
    }

    public Coordinate projectCoordinateOntoLineString(Coordinate c, LineString ls) {
        // Check if the coordinate is already on the LineString
        GeometryFactory gf = new GeometryFactory();
        if(ls.contains(gf.createPoint(c))) {
            return c;
        }
        Coordinate resultCoord = c;
        double minDistance = Double.MAX_VALUE;
        // Iterate through line segments of the LineString
        for(int i = 0; i < ls.getNumPoints() - 1; i++) {
            // Get line segment
            Coordinate p1 = ls.getCoordinateN(i);
            Coordinate p2 = ls.getCoordinateN(i+1);
            LineSegment lineseg = new LineSegment(p1, p2);

            // Get a projected coordinate
            Coordinate proj = lineseg.project(c);

            // Check if the projected coordinate is closer to the coordinate
            // than the minimum distance
            if(c.distance(proj) < minDistance) {
                resultCoord = proj;
            }
        }
        return resultCoord;
    }

    // public Way difference(Way w1, Way w2) {
    //     // If there is no overlap, return null
    //     if (!overlaps(w1, w2)) {
    //         return null;
    //     }
    //     // Return null if either way is not closed
    //     if ((!w1.isClosed()) || (!w2.isClosed())) {
    //         return null;
    //     }
    //     // Get the intersections of the ways


    // }

    public Way subtract(Way w1, Way w2) {
        if ((!w1.isClosed()) || (!w2.isClosed())) {
            return null;
        }
        Polygon p1 = wayToPolygon(w1);
        Polygon p2 = wayToPolygon(w2);
        Geometry g = p1.difference(p2);
        if(g.getGeometryType() ==  "Polygon") {
            return polygonToWay((Polygon) g);
        }
        if(g instanceof MultiPolygon) {
            if(g.getGeometryN(0) instanceof Polygon) {
                return polygonToWay((Polygon) g);
            }
        }
        return null;
    }

    public Way union(Way w1, Way w2) {
        if ((!w1.isClosed()) || (!w2.isClosed())) {
            return null;
        }
        Polygon p1 = wayToPolygon(w1);
        Polygon p2 = wayToPolygon(w2);
        Geometry g = p1.union(p2);
        if(g instanceof Polygon) {
            return polygonToWay((Polygon) g);
        }
        if(g instanceof MultiPolygon) {
            if(g.getGeometryN(0) instanceof Polygon) {
                return polygonToWay((Polygon) g);
            }
        }
        return null;
    }

    public double getAngleOfLineStringAtCoordinate(Coordinate c, LineString ls) {
        GeometryFactory gf = new GeometryFactory();
        // Check if the coordinate is on the way
        if (ls.contains(gf.createPoint(c))) {
            c = projectCoordinateOntoLineString(c, ls);
        }
        double distance = getDistanceAlongLineString(c, ls);

        // Local linearization
        Coordinate c1 = interpolate(ls, distance - 0.1);
        Coordinate c2 = interpolate(ls, distance + 0.1);

        // Get angle of two points very close to the original point
        double dx = c1.getX() - c2.getX();
        double dy = c1.getY() - c2.getY();
        return Math.atan2(dy, dx);
    }

    public double getAngleOfWayAtNode(Node n, Way w) {
        // Check if the node is on the way
        return getAngleOfLineStringAtCoordinate(nodeToCoordinate(n),
                                                wayToLineString(w));
    }

    public void actionPerformed(ActionEvent e) {
        // Get selected ways
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Way> selectedWays = ds.getSelectedWays();
        Collection<Command> commands = new ArrayList<Command>();

        List<Way> ways = new ArrayList<Way>(selectedWays);

        Way w1 = ways.get(0);
        Way w2 = ways.get(1);
        Way w3 = subtract(w1, w2);

        for (int i = 0; i < w3.getNodes().size() - 1; i++) {
            Node n = w3.getNodes().get(i);
            if(!ds.containsNode(n)) {
                commands.add(new AddCommand(ds, n));
            }
        }
        commands.add(new AddCommand(ds, w3));

        SequenceCommand sequenceOfCommands = new SequenceCommand("JTS Action", commands, true);
        UndoRedoHandler.getInstance().add(sequenceOfCommands);

        // for(Node n : w3.getNodes()) {
        //     if(ds.containsNode(n)){
        //         commands.add(new AddCommand(ds, n));
        //     }


        // }

        // AddCommand ac = new AddCommand(ds, w3);
        // commands.add(ac);
        // SequenceCommand sequenceOfCommands = new SequenceCommand("JTS Action", commands, true);
        // UndoRedoHandler.getInstance().add(sequenceOfCommands);

        // // Find the angle of the Node n on Way way
        // double angle = getAngleOfWayAtNode(n, way);

        // double cumulativeDist = 0;
        // while (cumulativeDist < way.getLength()) {
        //     n = interpolate(way, cumulativeDist);
        //     cumulativeDist += 100.0*Math.random();
        //     ac = new AddCommand(ds, n);
        //     UndoRedoHandler.getInstance().add(ac);
        // }
    }
}
