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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Christian Bauer
 */
public class ContentDirectoryViewImpl extends JDialog implements ContentDirectoryView {

    @Inject
    protected TreeView treeView;

    @Inject
    protected DetailView detailView;

    protected Presenter presenter;

    @PostConstruct
    public void init() {
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        dispose();
                    }
                }
        );

        JScrollPane containerTreePane;
        containerTreePane = new JScrollPane(treeView.asUIComponent());
        containerTreePane.setMinimumSize(new Dimension(180, 200));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        splitPane.setLeftComponent(containerTreePane);
        splitPane.setRightComponent(detailView.asUIComponent());
        splitPane.setResizeWeight(0.65);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(700, 500));
        setMinimumSize(new Dimension(500, 250));

        add(splitPane, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public TreeView getTreeView() {
        return treeView;
    }

    @Override
    public DetailView getDetailView() {
        return detailView;
    }
}
