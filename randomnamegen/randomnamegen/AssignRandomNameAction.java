// || Swami-Shriji ||
package randomnamegen;

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

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.Tag;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.tools.Shortcut;

public class AssignRandomNameAction extends JosmAction {

    private List<String> names = new ArrayList();
    private List<String> roads = new ArrayList();
    
    public AssignRandomNameAction() {
        super(tr("Assign random name."),
              "Assign random name.",
              tr("Assigns a random name."),
              Shortcut.registerShortcut(
                "assign:randomName",
                "Assign random name",
                java.awt.event.KeyEvent.VK_O,
                Shortcut.CTRL_SHIFT
              ),
              false);
        roads.add("मार्ग");
        roads.add("सड़क");
        roads.add("रास्ता");
        roads.add("पथ");
        roads.add("सरणी");
        roads.add("बाज़ार ");
        File fileName = new File("/home/parthbhavsar/Documents/javapract/RandomNameGen/englishHindiDict.csv");
        try (Scanner sc = new Scanner(fileName)) {
            sc.useDelimiter(",");
            while(sc.hasNextLine()) {
                names.add(sc.nextLine());
            }
        } catch (Exception e) {
            System.out.println("Oh no!");
        }
    }



    public String getRandomName() {

        Random random = new Random();
        String name = names.get(random.nextInt(names.size()));
        name = name.replace("\"", "").replace(",", "");
        return name;
    }
        // return names.get( random.nextInt(names.size()) );

    public void actionPerformed(ActionEvent e) {
        // Get the data set
        DataSet ds = MainApplication.getLayerManager().getEditDataSet();

        String name;
        Random random = new Random();

        // Get selected
        Collection<OsmPrimitive> selected = ds.getSelected();
        Collection<Command> commands = new ArrayList<Command>();

        // Fill up cdc with random names
        for(OsmPrimitive primitive : selected) {
            name = getRandomName();
            // If the primitive has the tag "highway", append a random string from roads to the name
            if (primitive.hasKey("highway")) {
                name = name + " " + roads.get(random.nextInt(roads.size()));
            }
            ChangePropertyCommand cpc = new ChangePropertyCommand(primitive, "name", name);
            commands.add(cpc);
        }

        SequenceCommand sequenceOfCommands = new SequenceCommand("Random name assignment", commands, true);

        UndoRedoHandler.getInstance().add(sequenceOfCommands);
    }
}