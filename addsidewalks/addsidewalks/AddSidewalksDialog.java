// || Swami-Shriji ||
package addsidewalks;

import org.openstreetmap.josm.gui.ExtendedDialog;

public class AddSidewalksDialog extends ExtendedDialog{

    // Distance between the sidewalk and the road
    private double distance;

    // Right side of road
    private boolean rightSide;

    // Left side of road
    private boolean leftSide;

    AddSidewalksDialog() {
        super("Add Sidewalks", new String[] {"OK", "Cancel"});
        setContent("Add Sidewalks to the road");
        setButtonIcons(new String[] {"ok", "cancel"});
        setDefaultButton(1);
        setCancelButton(2);
        showDialog();
    }

    
}
