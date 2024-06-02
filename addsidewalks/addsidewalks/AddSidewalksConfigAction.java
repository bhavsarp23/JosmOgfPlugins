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

public class AddSidewalksConfigAction {

    public AddSidewalksConfigAction() {
        super(tr("Configure Sidewalks."),
              "Configure Sidewalks.",
              tr("Config sidewalks."),
              Shortcut.registerShortcut(
                "cfg:sidewalks",
                "Config Sidewalks",
                java.awt.event.KeyEvent.VK_9,
                Shortcut.CTRL_SHIFT
              ),
            false);

    }

    public void actionPerformed(ActionEvent e) {
      // Show the configuration dialog
        AddSidewalksSettings settings = new AddSidewalksSettings();
        AddSidewalksConfigDialog dialog = new AddSidewalksConfigDialog(settings);
        dialog.showDialog();
    
    }
    
}