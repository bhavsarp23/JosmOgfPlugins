// || Swami-Shriji ||

package jtsjosmtest;
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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class JtsJosmTest extends Plugin {
    // Constructor
    public JtsJosmTest(PluginInformation info) {
        super(info);
        MainMenu.add(MainApplication.getMenu().moreToolsMenu, new AddBuildingToWay());
    }
}