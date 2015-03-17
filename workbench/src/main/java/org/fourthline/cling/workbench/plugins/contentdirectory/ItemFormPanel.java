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
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.workbench.Workbench;
import org.seamless.swing.Application;
import org.seamless.swing.JPopupMenuButton;
import org.seamless.util.Text;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Renders a DIDL item into a Swing form panel.
 *
 * @author Christian Bauer
 */
public abstract class ItemFormPanel extends JPanel {

    public ItemFormPanel(Item item) {
        super(new GridBagLayout());

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        SelectableFieldsForm form = new SelectableFieldsForm(5);

        form.addLabelAndSelectableLastField("Title:", item.getTitle(), this);

        if (item.getCreator() != null) {
            form.addLabelAndSelectableLastField("DC Creator:", item.getCreator(), this);
        }

        if (item.getWriteStatus() != null) {
            form.addLabelAndSelectableLastField("UPnP Write Status:", item.getWriteStatus().toString(), this);
        }

        form.addLabelAndSelectableLastField("UPnP Class:", item.getClazz().getValue(), this);
        form.addLabelAndSelectableLastField("ID:", item.getId(), this);
        form.addLabelAndSelectableLastField("Parent ID:", item.getParentID(), this);

        if (item.getRefID() != null) {
            form.addLabelAndSelectableLastField("Reference ID:", item.getRefID(), this);
        }

        form.addSeparator(this);

        if (item.hasProperty(DIDLObject.Property.DC.CONTRIBUTOR.class))
            form.addLabelAndSelectableLastField("DC Contributor:", item.getFirstProperty(DIDLObject.Property.DC.CONTRIBUTOR.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.DC.DATE.class))
            form.addLabelAndSelectableLastField("DC Date:", item.getFirstProperty(DIDLObject.Property.DC.DATE.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.DC.DESCRIPTION.class))
            form.addLabelAndSelectableLastField("DC Description:", item.getFirstProperty(DIDLObject.Property.DC.DESCRIPTION.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.DC.LANGUAGE.class))
            form.addLabelAndSelectableLastField("DC Language:", item.getFirstProperty(DIDLObject.Property.DC.LANGUAGE.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.DC.PUBLISHER.class))
            form.addLabelAndSelectableLastField("DC Publisher:", item.getFirstProperty(DIDLObject.Property.DC.PUBLISHER.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.DC.RELATION.class))
            form.addLabelAndSelectableLastField("DC Relation:", item.getFirstProperty(DIDLObject.Property.DC.RELATION.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.DC.RIGHTS.class))
            form.addLabelAndSelectableLastField("DC Rights:", item.getFirstProperty(DIDLObject.Property.DC.RELATION.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.ARTIST.class))
            form.addLabelAndSelectableLastField("UPnP Artist:", item.getFirstProperty(DIDLObject.Property.UPNP.ARTIST.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.ALBUM.class))
            form.addLabelAndSelectableLastField("UPnP Album:", item.getFirstProperty(DIDLObject.Property.UPNP.ALBUM.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.ALBUM_ART_URI.class))
            form.addLabelAndSelectableLastField("UPnP Album Art URI:", item.getFirstProperty(DIDLObject.Property.UPNP.ALBUM_ART_URI.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.LYRICS_URI.class))
            form.addLabelAndSelectableLastField("UPnP Lyrics URI:", item.getFirstProperty(DIDLObject.Property.UPNP.LYRICS_URI.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.GENRE.class))
            form.addLabelAndSelectableLastField("UPnP Genre:", item.getFirstProperty(DIDLObject.Property.UPNP.GENRE.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.ICON.class))
            form.addLabelAndSelectableLastField("UPnP Icon:", item.getFirstProperty(DIDLObject.Property.UPNP.ICON.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.LONG_DESCRIPTION.class))
            form.addLabelAndSelectableLastField("UPnP Long Description:", item.getFirstProperty(DIDLObject.Property.UPNP.LONG_DESCRIPTION.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.PLAYLIST.class))
            form.addLabelAndSelectableLastField("UPnP Playlist:", item.getFirstProperty(DIDLObject.Property.UPNP.PLAYLIST.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.RATING.class))
            form.addLabelAndSelectableLastField("UPnP Rating:", item.getFirstProperty(DIDLObject.Property.UPNP.RATING.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.REGION.class))
            form.addLabelAndSelectableLastField("UPnP Region:", item.getFirstProperty(DIDLObject.Property.UPNP.REGION.class).toString(), this);

        if (item.hasProperty(DIDLObject.Property.UPNP.ORIGINAL_TRACK_NUMBER.class))
            form.addLabelAndSelectableLastField("UPnP Track:", item.getFirstProperty(DIDLObject.Property.UPNP.ORIGINAL_TRACK_NUMBER.class).toString(), this);

        for (final Res resource : item.getResources()) {

            form.addSeparator(this);

            form.addLabelAndSelectableLastField("Resource URI:", resource.getValue(), this);

            if (resource.getImportUri() != null)
                form.addLabelAndSelectableLastField("Import URI:", resource.getImportUri().toString(), this);

            form.addLabelAndSelectableLastField("Protocol Info:", resource.getProtocolInfo().toString(), this);

            if (resource.getSize() != null)
                form.addLabelAndSelectableLastField("Size:", Text.displayFilesize(resource.getSize()), this);

            if (resource.getDuration() != null)
                form.addLabelAndSelectableLastField("Duration:", resource.getDuration(), this);

            if (resource.getBitrate() != null)
                form.addLabelAndSelectableLastField("Bit Rate (bits/second):", Long.toString(resource.getBitrate() * 8), this);

            if (resource.getSampleFrequency() != null)
                form.addLabelAndSelectableLastField("Sample Frequency:", resource.getSampleFrequency().toString(), this);

            if (resource.getBitsPerSample() != null)
                form.addLabelAndSelectableLastField("Bits/Sample:", resource.getBitsPerSample().toString(), this);

            if (resource.getNrAudioChannels() != null)
                form.addLabelAndSelectableLastField("Audio Channels:", resource.getNrAudioChannels().toString(), this);

            if (resource.getResolution() != null)
                form.addLabelAndSelectableLastField("Resolution (Pixels):", resource.getResolution(), this);

            if (resource.getColorDepth() != null)
                form.addLabelAndSelectableLastField("Color Depth:", resource.getColorDepth().toString(), this);

            if (resource.getProtection() != null)
                form.addLabelAndSelectableLastField("Protection:", resource.getProtection(), this);


            JPopupMenu menu = new JPopupMenu();
            List<JMenuItem> menuItems = createSendToMenuItems(resource);
            for (JMenuItem menuItem : menuItems) {
                menu.add(menuItem);
            }
            final JPopupMenuButton playButton =
                    new JPopupMenuButton("Send to...", Application.createImageIcon(Workbench.class, "img/16/play.png"), menu);
            playButton.setFocusable(false);

            final JButton copyURIButton =
                    new JButton("Copy URI to clipboard", Application.createImageIcon(Workbench.class, "img/16/copyclipboard.png"));
            copyURIButton.setFocusable(false);
            copyURIButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Application.copyToClipboard(resource.getValue());
                }
            });

            final JButton copyProtocolInfoButton =
                    new JButton("Copy Protocol Info to clipboard", Application.createImageIcon(Workbench.class, "img/16/copyclipboard.png"));
            copyProtocolInfoButton.setFocusable(false);
            copyProtocolInfoButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Application.copyToClipboard(resource.getProtocolInfo().toString());
                }
            });

            JToolBar resourceToolBar = new JToolBar();
            resourceToolBar.setFloatable(false);
            resourceToolBar.add(playButton);
            resourceToolBar.add(copyURIButton);
            resourceToolBar.add(copyProtocolInfoButton);

            form.addLabelAndLastField("", resourceToolBar, this);
        }

    }

    public abstract List<JMenuItem> createSendToMenuItems(Res resource);

}
