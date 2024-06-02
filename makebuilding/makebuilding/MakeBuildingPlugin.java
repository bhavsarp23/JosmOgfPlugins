// || Swami-Shriji ||
package makebuilding;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;

public class MakeBuildingPlugin extends Plugin {
    public MakeBuildingPlugin(PluginInformation info) {
        super(info);
        MainMenu.add(MainApplication.getMenu().moreToolsMenu, new GenerateBuildingAction());
    }
}