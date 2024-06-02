// || Swami-Shriji ||
package addbuildings;

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

import jtsjosm.JtsJosmAction;

public class AddBuildingsToHighway extends JosmAction {

    public AddBuildingsToHighway() {
        super(tr("Add Buildings To Highway"),
              "Add Buildings To Highway",
              tr("Add Buildings To Highway"),
              Shortcut.registerShortcut(
                "addbuildings:tohighway",
                "Add buildings to highway",
                java.awt.event.KeyEvent.VK_B,
                Shortcut.CTRL_SHIFT
              ),
              false);

    }

    public Way generateBuilding(Node center, float width, float height, float angle) {
        // Generate a rectangle using jts josm plugin
        JtsJosmAction jtsAction = new JtsJosmAction();
        Way buildingWay = jtsAction.generateRectangle(center, width, height, angle);

        // Use random in a if statement
        Random rand = new Random();
        if(rand.nextBoolean()) {
            // Generate a width smaller than the width of the building
            float smallerWidth = rand.nextFloat() * width;

            // Generate a height smaller than the height of the building
            float smallerHeight = 0.2f * rand.nextFloat() * height;

            // Generate a rectangle somewhere on the rear end of the building
            Node rearEnd = new Node(center.getEastNorth().add(Geometry.getUnitVector(angle).multiply(-width)));

            Way rearBuilding = jtsAction.generateRectangle(rearEnd, smallerWidth, smallerHeight, angle);

            // Generate a rectangle on the rear end of the building
            buildingWay.put("building", "yes");

        } 
        
    }


    public void actionPerformed(ActionEvent e) {

        DataSet ds = MainApplication.getLayerManager().getEditDataSet();
        Collection<Way> selected = ds.getSelectedWays();
        Collection<Command> commands = new ArrayList<Command>();

        // Generate parallel ways


        // Interpolate the parallel ways

        // Generate building at interpolation points

        // Add buildings to layer
        

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