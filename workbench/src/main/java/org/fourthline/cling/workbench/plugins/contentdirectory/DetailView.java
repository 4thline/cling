/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
