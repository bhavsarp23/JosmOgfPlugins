// || Swami-Shriji ||
package randomnamegen;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;

public class RandomNameGenPlugin extends Plugin {
    public RandomNameGenPlugin(PluginInformation info) {
        super(info);
        MainMenu.add(MainApplication.getMenu().moreToolsMenu, new AssignRandomNameAction());
    }
}