// || Swami-Shriji ||
package addsidewalks;

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
import java.util.concurrent.ConcurrentLinkedQueue;  
import java.util.function.Predicate;  

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.dialogs.MenuItemSearchDialog.Action;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.Tag;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.tools.Shortcut;
import org.openstreetmap.josm.actions.mapmode.ParallelWays;
import org.openstreetmap.josm.gui.util.KeyPressReleaseListener;

import jtsjosm.JtsJosmAction;

public class AddSidewalksAction extends JosmAction implements KeyPressReleaseListener{

    public AddSidewalksAction() {
        super(tr("Add Sidewalks."),
              "Add Sidewalks.",
              tr("Assigns a random name."),
              Shortcut.registerShortcut(
                "add:sidewalks",
                "Add Sidewalks",
                java.awt.event.KeyEvent.VK_9,
                Shortcut.CTRL_SHIFT
              ),
            false);

    }

    public void actionPerformed(ActionEvent e) {
      // Get all the selected ways
      DataSet ds = MainApplication.getLayerManager().getEditDataSet();
      Collection<Way> selectedWays = ds.getSelectedWays();

      // Print debug msg
      System.out.println("Completed selection of ways -------------------------------");

      // Buffer the ways by 7.5 meters using jtsjosm
      JtsJosmAction jts = new JtsJosmAction();
      Collection<Way> bufferedWays = new ArrayList<Way>();
      for(Way w : selectedWays) {
        bufferedWays.add(jts.offsetWay(w, 7.5, false));
      }

      // Print debug msg
      System.out.println("Completed buffer of ways -------------------------------");

      // Add the tag 'highway' = 'path' to the buffered ways
      for(Way w : bufferedWays) {
        w.put("highway", "path");
      }
      // Print debug msg
      System.out.println("Completed tagging of ways -------------------------------");

      // Add the buffered ways to the dataset
      Collection<Command> commands = new ArrayList<Command>();
      for(Way w : bufferedWays) {
        // Add nodes of the way
        for(Node n : w.getNodes()) {
          AddCommand acn = new AddCommand(ds, n);
          commands.add(acn);
        }
        AddCommand ac = new AddCommand(ds, w);
        commands.add(ac);
      }

      // Print debug msg
      System.out.println("Completed adding of ways -------------------------------");

      // Add the commands to the undo/redo handler
      SequenceCommand sequenceCommand = new SequenceCommand(tr("Add new ways"), commands, true);
      UndoRedoHandler.getInstance().add(sequenceCommand);
    }

    @Override
    public void doKeyPressed(KeyEvent e) {
        // Get the key code
        int keyCode = e.getKeyCode();

        // If the key code is ctrl + shift + 9, then show the dialog
        if (keyCode == KeyEvent.VK_9 && e.isControlDown() && e.isShiftDown()) {
            e.consume();
            new AddSidewalksConfigDialog().showDialog();
        }
    }

}