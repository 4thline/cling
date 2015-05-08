/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.fourthline.cling.workbench.plugins.contentdirectory.impl;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.workbench.plugins.contentdirectory.SelectableFieldsForm;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;

/**
 * Renders a DIDL container into a Swing form panel.
 *
 * @author Christian Bauer
 */
public class ContainerFormPanel extends JPanel {

    public ContainerFormPanel(Container container) {
        super(new GridBagLayout());

        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        SelectableFieldsForm form = new SelectableFieldsForm(5);

        form.addLabelAndSelectableLastField("Title:", container.getTitle(), this);

        if (container.getCreator() != null) {
            form.addLabelAndSelectableLastField("DC Creator:", container.getCreator(), this);
        }

        form.addLabelAndSelectableLastField("UPnP Class:", container.getClazz().getValue(), this);
        form.addLabelAndSelectableLastField("ID:", container.getId(), this);
        form.addLabelAndSelectableLastField("Parent ID:", container.getParentID(), this);

        Integer childCount = container.getChildCount();
        form.addLabelAndSelectableLastField("Child Count:", childCount != null ? childCount.toString() : "-", this);
        form.addLabelAndSelectableLastField("Restricted?", Boolean.toString(container.isRestricted()), this);
        form.addLabelAndSelectableLastField("Searchable?", Boolean.toString(container.isSearchable()), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.ICON.class))
            form.addLabelAndSelectableLastField("UPnP Icon:", container.getFirstProperty(DIDLObject.Property.UPNP.ICON.class).toString(), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_FREE.class))
            form.addLabelAndSelectableLastField("UPnP Storage Free:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_FREE.class).toString(), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_MAX_PARTITION.class))
            form.addLabelAndSelectableLastField("UPnP Max Partition:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_MAX_PARTITION.class).toString(), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_TOTAL.class))
            form.addLabelAndSelectableLastField("UPnP Storage Total:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_TOTAL.class).toString(), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_USED.class))
            form.addLabelAndSelectableLastField("UPnP Storage Used:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_USED.class).toString(), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_MEDIUM.class))
            form.addLabelAndSelectableLastField("UPnP Storage Medium:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_MEDIUM.class).toString(), this);

        if (container.getWriteStatus() != null)
            form.addLabelAndSelectableLastField("UPnP Write Status:", container.getWriteStatus().toString(), this);

        form.addSeparator(this);

        if (container.hasProperty(DIDLObject.Property.UPNP.ALBUM_ART_URI.class))
            form.addLabelAndSelectableLastField("UPnP Album Art URI:", container.getFirstProperty(DIDLObject.Property.UPNP.ALBUM_ART_URI.class).toString(), this);

        for (DIDLObject.Class searchClass : container.getSearchClasses()) {
            form.addSeparator(this);

            form.addLabelAndSelectableLastField(
                    "UPnP Search Class:",
                    searchClass.getValue(),
                    this
            );
            if (searchClass.getFriendlyName() != null) {
                form.addLabelAndSelectableLastField(
                        "Friendly Name:",
                        searchClass.getFriendlyName(),
                        this
                );
            }
            form.addLabelAndSelectableLastField(
                    "Includes Derived?",
                    Boolean.toString(searchClass.isIncludeDerived()),
                    this
            );
        }

        for (DIDLObject.Class createClass : container.getCreateClasses()) {
            form.addSeparator(this);

            form.addLabelAndSelectableLastField(
                    "UPnP Create Class:",
                    createClass.getValue(),
                    this
            );
            if (createClass.getFriendlyName() != null) {
                form.addLabelAndSelectableLastField(
                        "Friendly Name:",
                        createClass.getFriendlyName(),
                        this
                );
            }
            form.addLabelAndSelectableLastField(
                    "Includes Derived?",
                    Boolean.toString(createClass.isIncludeDerived()),
                    this
            );
        }
    }
}
