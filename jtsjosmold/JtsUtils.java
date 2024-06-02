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
import org.locationtech.jts.operation.buffer.BufferBuilder;

class JtsUtils {

    public Way parallelOffset(Way w, double offset) {
        LineString ls = JtsToJosm.wayToLinestring(w);
        Geometry offsetgeo = BufferBuilder.buffer(ls, offset);
        if(offsetgeo instanceof LineString) {
            return JtsToJosm.linestringToWay(offsetgeo);
        }
        return null;
    }

}