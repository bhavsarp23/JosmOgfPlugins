// || Swami-Shriji ||

// This file is part of JOSM.
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
import java.awt.event.ActionEvent;

public class AddBuildingToWayAction extends JosmAction {
    public AddBuildingToWayAction() {
        super(tr("Add building to way."), 
            "Add buildings",
            tr("Add building to way."),
            Shortcut.registerShortcut(
                "add:buildingtoway",
                "Add building to way",
                java.awt.event.KeyEvent.VK_8,
                Shortcut.CTRL_SHIFT
            ),
            false);
    }

    
    public void actionPerformed(ActionEvent e) {
        // Get selected ways
        Collection<Way> ways = getSelectedWays();
        // In each way, add a single rectangle in parallel to the way
        
        for(Way way : ways) {
            Collection<Way> resultantWays = new ArrayList<Way>();
            resultantWays.add(way);
            ParallelWays pw = new ParallelWays(resultantWays, false, 0);
            pw.commit();
        }
    } way {

            )

    }
}