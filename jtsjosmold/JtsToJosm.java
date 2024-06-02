// || Swami-Shriji ||

package jtstojosm;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Way;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.data.osm.Relation;

class JtsToJosm {

    public JtsToJosm() {

    }

    public Node coordinateToNode(Coordinate coordinate) {
        return new Node(new EastNorth(coordinate.getX(), coordinate.getY()));
    }

    public Node pointToNode(Point point) {
        EastNorth en = new EastNorth(point.getX(), point.getY());
        return new Node(en);
    }
    
    public Way linestringToWay(LineString ls) {
        Coordinate coordinates[] = ls.getCoordinates();
        // Node nodes[] = coordinates.forEach(c -> coordinateToNode(c));

        List<Node> nodes = new ArrayList<Node>();
        

        for(Coordinate c : coordinates) {
            nodes.add(coordinateToNode(c));
        }

        Way way = new Way();
        way.setNodes(nodes);
        return way;

    }

    public Way linestringToWay(MultiLineString ms) {
        Way way = new Way();
        return way;
    }

    public Way polygonToWay(Polygon poly) {
        return linestringToWay(poly.getExteriorRing());
    }
    public Relation multipolygonToRelation(MultiPolygon mp) {
        Relation relation = new Relation();
        
        for (int i = 0; i < mp.getNumGeometries(); i++) {
            Polygon poly = (Polygon) mp.getGeometryN(i);
            Way way = polygonToWay(poly);
            relation.addMember(new RelationMember("", way));
        }
        
        return relation;
    }
    



    // public Way polygonToWay(Polygon poly) {
    //     if(poly.getBoundary() )
    //     return linestringToWay(poly.getBoundary());
    // }
}

//     public multipolygonToRelation