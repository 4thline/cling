package org.fourthline.cling.workbench.plugins.contentdirectory;

import org.seamless.swing.Form;

import javax.swing.*;
import java.awt.*;

/**
 * Allows various fields to be selectable.
 *
 * @author Sebastian Roth
 */
public class SelectableFieldsForm extends Form {
    public SelectableFieldsForm(int padding) {
        super(padding);
    }

    public void addLabelAndSelectableLastField(String s, String value, Container parent) {
        addLabel(s, parent);

        JTextField field = new JTextField(value);
        field.setBorder(null);
        field.setOpaque(false);
        field.setEditable(false);

        addLastField(field, parent);
    }
}
