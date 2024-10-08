// || Swami-Shriji ||
package addsidewalks;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;

public class AddSidewalksPlugin extends Plugin {
    public AddSidewalksPlugin(PluginInformation info) {
        super(info);
        SidewalkDialogAction sidewalkDialog = new SidewalkDialogAction();
        MainMenu.add(MainApplication.getMenu().moreToolsMenu, sidewalkDialog);
        MainMenu.add(MainApplication.getMenu().moreToolsMenu, new AddSidewalksAction( sidewalkDialog));
    }
}
