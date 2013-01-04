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

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.shared.View;

import java.util.List;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public interface DetailView extends View<DetailView.Presenter> {

    public static final int SUPPORTED_INSTANCES = 8;

    public interface Presenter extends TreeDetailPresenter {

        void init(DetailView view);

        Service getMatchingAVTransportService(Device device, List<ProtocolInfo> infos, Res resource);

        void onSendToMediaRenderer(int instanceId, Service avTransportService, String uri);
    }

    void showContainer(Container container);

    void showItem(Item item, Map<Device, List<ProtocolInfo>> mediaRenderers);
}
