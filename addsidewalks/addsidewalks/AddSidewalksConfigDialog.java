// || Swami-Shriji ||
package addsidewalks;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.datatransfer.ClipboardUtils;
import org.openstreetmap.josm.gui.datatransfer.importers.TextTagPaster;
import org.openstreetmap.josm.gui.widgets.HistoryComboBox;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.TextTagParser;

public class AddSidewalksConfigDialog extends ExtendedDialog {
    AddSidewalksSettings settings;

    JPanel buildContentPanel() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        
        // Add two checkboxes for right and left sidewalks
        JCheckBox rightSidewalk = new JCheckBox(tr("Right Sidewalk"));
        rightSidewalk.setSelected(settings.right);
        contentPanel.add(rightSidewalk, GBC.eol());

        JCheckBox leftSidewalk = new JCheckBox(tr("Left Sidewalk"));
        leftSidewalk.setSelected(settings.left);
        contentPanel.add(leftSidewalk, GBC.eol());

        // Add a text field for the offset
        JLabel offsetLabel = new JLabel(tr("Offset:"));
        contentPanel.add(offsetLabel, GBC.std().insets(0, 0, 5, 0));
        JFormattedTextField offsetField = new JFormattedTextField(NumberFormat.getNumberInstance());
        offsetField.setValue(settings.offset);
        offsetField.setColumns(10);
        contentPanel.add(offsetField, GBC.eol());        
        
        return contentPanel;
    }

    AddSidewalksConfigDialog(AddSidewalksSettings settings) {
        super(MainApplication.getMainFrame(), tr("Add Sidewalks Configuration"), new String[] {tr("OK"), tr("Cancel")});
        this.settings = settings;

        setButtonIcons(new String[] {"ok", "cancel"});
        setContent(buildContentPanel());
        setupDialog();

    }

    @Override
    public ExtendedDialog showDialog() {
        super.showDialog();
        if (getValue() == 1) {
            settings.right = ((JCheckBox) getComponent(0)).isSelected();
            settings.left = ((JCheckBox) getComponent(1)).isSelected();
            try {
                settings.offset = ((Number) ((JFormattedTextField) getComponent(3)).getValue()).floatValue();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(MainApplication.getMainFrame(), tr("Invalid offset value"), tr("Error"), JOptionPane.ERROR_MESSAGE);
                return this;
            }
        }
        return this;
    }
}