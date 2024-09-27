// || Swami-Shriji ||
package makebuilding;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;

public class MakeBuildingPlugin extends Plugin {

    private double width, depth, setback, lotSpacing;

    public MakeBuildingPlugin(PluginInformation info) {
        super(info);
        BuildingDialogAction buildingDialog = new BuildingDialogAction();
        MainMenu.add(MainApplication.getMenu().moreToolsMenu, buildingDialog);
        MainMenu.add(MainApplication.getMenu().moreToolsMenu, new GenerateBuildingAction(buildingDialog));
    }
}
