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

package org.fourthline.cling.workbench.plugins.contentdirectory;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.seamless.swing.Form;

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

        Form form = new Form(5);

        form.addLabelAndLastField("Title:", container.getTitle(), this);

        if (container.getCreator() != null) {
            form.addLabelAndLastField("DC Creator:", container.getCreator(), this);
        }

        form.addLabelAndLastField("UPnP Class:", container.getClazz().getValue(), this);
        form.addLabelAndLastField("ID:", container.getId(), this);
        form.addLabelAndLastField("Parent ID:", container.getParentID(), this);

        Integer childCount = container.getChildCount();
        form.addLabelAndLastField("Child Count:", childCount != null ? childCount.toString() : "-", this);
        form.addLabelAndLastField("Restricted?", Boolean.toString(container.isRestricted()), this);
        form.addLabelAndLastField("Searchable?", Boolean.toString(container.isSearchable()), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_FREE.class))
            form.addLabelAndLastField("UPnP Storage Free:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_FREE.class).toString(), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_MAX_PARTITION.class))
            form.addLabelAndLastField("UPnP Max Partition:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_MAX_PARTITION.class).toString(), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_TOTAL.class))
            form.addLabelAndLastField("UPnP Storage Total:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_TOTAL.class).toString(), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_USED.class))
            form.addLabelAndLastField("UPnP Storage Used:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_USED.class).toString(), this);

        if (container.hasProperty(DIDLObject.Property.UPNP.STORAGE_MEDIUM.class))
            form.addLabelAndLastField("UPnP Storage Medium:", container.getFirstProperty(DIDLObject.Property.UPNP.STORAGE_MEDIUM.class).toString(), this);

        if (container.getWriteStatus() != null)
            form.addLabelAndLastField("UPnP Write Status:", container.getWriteStatus().toString(), this);

        for (DIDLObject.Class searchClass : container.getSearchClasses()) {
            form.addSeparator(this);

            form.addLabelAndLastField(
                    "UPnP Search Class:",
                    searchClass.getValue(),
                    this
            );
            if (searchClass.getFriendlyName() != null) {
                form.addLabelAndLastField(
                        "Friendly Name:",
                        searchClass.getFriendlyName(),
                        this
                );
            }
            form.addLabelAndLastField(
                    "Includes Derived?",
                    Boolean.toString(searchClass.isIncludeDerived()),
                    this
            );
        }

        for (DIDLObject.Class createClass : container.getCreateClasses()) {
            form.addSeparator(this);

            form.addLabelAndLastField(
                    "UPnP Create Class:",
                    createClass.getValue(),
                    this
            );
            if (createClass.getFriendlyName() != null) {
                form.addLabelAndLastField(
                        "Friendly Name:",
                        createClass.getFriendlyName(),
                        this
                );
            }
            form.addLabelAndLastField(
                    "Includes Derived?",
                    Boolean.toString(createClass.isIncludeDerived()),
                    this
            );
        }
    }
}
