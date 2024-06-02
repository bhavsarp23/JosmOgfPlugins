// || Swami-Shriji ||
package addbuildings;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;

public class AddBuildingsPlugin extends Plugin {
    public AddBuildingsPlugin(PluginInformation info) {
        super(info);
        MainMenu.add(MainApplication.getMenu().moreToolsMenu, new AddBuildingsToHighway());
    }
}