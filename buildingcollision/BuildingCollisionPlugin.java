// || Swami-Shriji ||
package buildingcollision;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;

public class BuildingCollisionPlugin extends Plugin {
    public BuildingCollisionPlugin(PluginInformation info) {
        super(info);
        MainMenu.add(MainApplication.getMenu().moreToolsMenu, new RemoveCollidingBuildingsAction());
    }
}