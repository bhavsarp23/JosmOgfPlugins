// || Swami-Shriji ||
package buildingcollision;

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
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.data.validation.tests.CrossingWays;
import org.openstreetmap.josm.tools.Geometry;

public class RemoveCollidingBuildingsAction extends JosmAction {

    public RemoveCollidingBuildingsAction() {
        super(tr("Remove Colliding Buildings"),
              "Remove Colliding Buildings",
              tr("Remove Colliding Buildings"),
              Shortcut.registerShortcut(
                "buildingcollision:removebuildings",
                "Remove Colliding Buildings",
                java.awt.event.KeyEvent.VK_R,
                Shortcut.CTRL_SHIFT
              ),
              false);

    }

    public boolean intersections(Way w1, Way w2) {
        ArrayList<Way> ways = new ArrayList<Way>();
        ways.add(w1);
        ways.add(w2);
        Set<Node> intersections = Geometry.addIntersections(ways, true, null);
        if(intersections.size() > 0) {
            return true;
        }
        return false;
    }

    public Collection<Way> findCollidingWays(Way w, Collection<Way> others) {
        Collection<Way> results = new ArrayList<Way>();
        for(Way other : others) {
            if(intersections(w, other)) {
                results.add(other);
            }
        }
        return results;

    }

    public void actionPerformed(ActionEvent e) {

        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Way> selected = ds.getSelectedWays();
        Collection<Command> commands = new ArrayList<Command>();

        // Get all buildings
        Collection<Way> ways = ds.getWays();
        List<Way> buildings = new ArrayList<Way>();
        for(Way way : ways) {
            if(way.hasKey("building")) {
                buildings.add(way);
            }
        }

        Collection<Way> buildingsToRemove = new ArrayList<Way>();

        for(Way primitive : selected) {
            buildingsToRemove = findCollidingWays(primitive, buildings);
            if(buildingsToRemove.size() > 0) {
                DeleteCommand dc = new DeleteCommand(buildingsToRemove);
                commands.add(dc);
            }
        }
        SequenceCommand sequenceOfCommands = new SequenceCommand("Remove buildings", commands, true);
        UndoRedoHandler.getInstance().add(sequenceOfCommands);
    }
}