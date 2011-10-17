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

package org.fourthline.cling.workbench.plugins.renderingcontrol;

import org.fourthline.cling.model.meta.StateVariableAllowedValueRange;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public class RenderingControlViewImpl extends JDialog implements RenderingControlView {

    private final JTabbedPane tabs = new JTabbedPane();

    protected Presenter presenter;

    @Inject
    protected Instance<InstanceView> renderingControlViewInstance;

    final Map<Integer, InstanceView> controlViews = new LinkedHashMap();

    public void init(StateVariableAllowedValueRange volumeRange) {
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        dispose();
                        presenter.onViewDisposed();
                    }
                }
        );

        tabs.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        for (int i = 0; i < SUPPORTED_INSTANCES; i++) {
            InstanceView controlView = renderingControlViewInstance.get();
            controlView.init(i, volumeRange);
            controlViews.put(i, controlView);
            tabs.addTab(Integer.toString(i), controlView.asUIComponent());
        }

        getContentPane().add(tabs, BorderLayout.CENTER);
        setResizable(false);
        pack();
        setVisible(true);
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        for (InstanceView controlView : controlViews.values()) {
            controlView.setPresenter(presenter);
        }
    }

    @Override
    public InstanceView getInstanceView(int instanceId) {
        return controlViews.get(instanceId);
    }
}
