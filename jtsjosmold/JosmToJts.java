// || Swami-Shriji ||

package jtsexample;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Way;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.openstreetmap.josm.data.coor.LatLon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import java.util.List;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.data.osm.Relation;

class JosmToJts {


    public Coordinate nodeToCoordinate(Node node) {
        LatLon ll = node.getCoor();
        return new Coordinate(ll.lon(), ll.lat());
    }

    public CoordinateSequence nodesToCoordinateSequence(List<Node> nodes) {
        Coordinate coords[] = new Coordinate[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            coords[i] = nodeToCoordinate(nodes.get(i));
        }
        return new CoordinateArraySequence(coords);
    }

    public LineString wayToLineString(Way way) {
        return new LineString(nodesToCoordinateSequence(way.getNodes()), null);
    }

    public MultiLineString wayToMultiLineString(Way way) {
        return new MultiLineString(new LineString[] { wayToLineString(way) }, null);
    }

    public Polygon wayToPolygon(Way way) {
        return new Polygon(wayToLineString(way), null);
    }

    public MultiPolygon relationToMultiPolygon(Relation relation) {
        return new MultiPolygon(new Polygon[] { wayToPolygon(relation.getMembers()) }, null);
    }

    public JosmToJts() {
        // constructor for JosmToJts

    }

}