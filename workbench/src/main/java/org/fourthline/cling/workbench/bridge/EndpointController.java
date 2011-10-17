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

package org.fourthline.cling.workbench.bridge;

import org.fourthline.cling.bridge.BridgeUpnpService;
import org.fourthline.cling.bridge.BridgeUpnpServiceConfiguration;
import org.fourthline.cling.bridge.Constants;
import org.fourthline.cling.bridge.auth.AuthCredentials;
import org.fourthline.cling.bridge.auth.HashCredentials;
import org.fourthline.cling.bridge.auth.SecureHashAuthManager;
import org.fourthline.cling.bridge.link.Endpoint;
import org.fourthline.cling.bridge.link.EndpointResource;
import org.fourthline.cling.bridge.link.LinkManagementListener;
import org.fourthline.cling.bridge.link.LinkManager;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.bridge.backend.Bridge;
import org.seamless.http.Query;
import org.seamless.swing.AbstractController;
import org.seamless.swing.Application;
import org.seamless.swing.Controller;
import org.seamless.swing.DefaultAction;
import org.seamless.swing.DefaultEvent;
import org.seamless.swing.DefaultEventListener;
import org.seamless.swing.Event;
import org.seamless.swing.EventListener;
import org.seamless.swing.Form;
import org.seamless.swing.logging.LogMessage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Christian Bauer
 */
public class EndpointController extends AbstractController<JPanel> {

    // Actions
    public static String[] ACTION_CONNECT = {"Connect", "connect"};

    // Dependencies
    final protected Bridge bridge;

    // View
    final protected JScrollPane endpointListScrollPane = new JScrollPane();
    final protected JTextField remoteURLField = new JTextField();
    final protected JButton connectButton = new JButton(ACTION_CONNECT[0]);

    public EndpointController(Controller parentController, Bridge bridge) {
        super(new JPanel(new BorderLayout()), parentController);

        this.bridge = bridge;

        registerAction(
                connectButton,
                ACTION_CONNECT[1],
                new DefaultAction() {
                    public void actionPerformed(ActionEvent e) {
                        connectEndpoint(remoteURLField.getText());
                    }
                }
        );

        registerEventListener(
                BridgeStartedEvent.class,
                new DefaultEventListener<Bridge>() {
                    public void handleEvent(DefaultEvent<Bridge> e) {
                        getLinkManager().addListener(
                                new LinkManagementListener() {
                                    public void endpointRegistered(Endpoint endpoint) {
                                        update();
                                    }

                                    public void endpointDeregistered(Endpoint endpoint) {
                                        update();
                                    }

                                    protected void update() {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                updateEndpointList();
                                            }
                                        });
                                    }
                                }
                        );

                        connectButton.setEnabled(true);
                        updateEndpointList();
                    }
                }
        );

        registerEventListener(
                BridgeStoppedEvent.class,
                new EventListener() {
                    public void handleEvent(Event e) {
                        connectButton.setEnabled(false);
                        endpointListScrollPane.setViewportView(null);
                    }
                }
        );

        Form form = new Form(3);
        form.lastConstraints.weightx = 0;
        JPanel remoteURLPanel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("Remote URL:");
        label.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        form.addLabel(label, remoteURLPanel);
        form.addMiddleField(remoteURLField, remoteURLPanel);
        form.addLastField(connectButton, remoteURLPanel);

        endpointListScrollPane.setPreferredSize(new Dimension(550, 100));
        
        getView().add(endpointListScrollPane, BorderLayout.CENTER);
        getView().add(remoteURLPanel, BorderLayout.SOUTH);

        if (bridge.isRunning()) {
            connectButton.setEnabled(true);
            updateEndpointList();
        } else {
            connectButton.setEnabled(false);
        }
    }

    public Bridge getBridge() {
        return bridge;
    }

    public BridgeUpnpService getBridgeUpnpService() {
        return getBridge().getBridgeUpnpService();
    }

    public LinkManager getLinkManager() {
        return getBridgeUpnpService().getLinkManager();
    }

    protected void updateEndpointList() {
        List<EndpointResource> resources =
                new ArrayList(getBridge().getUpnpService().getRegistry().getResources(EndpointResource.class));

        // Sort by link id
        Collections.sort(resources, new Comparator<Resource<Endpoint>>() {
            public int compare(Resource<Endpoint> a, Resource<Endpoint> b) {
                return a.getModel().getId().compareTo(b.getModel().getId());
            }
        });

        endpointListScrollPane.setViewportView(new EndpointList(resources));
    }

    protected void connectEndpoint(String url) {
        int timeoutSeconds = Constants.LINK_DEFAULT_TIMEOUT_SECONDS;

        // TODO: Code duplication with LinkResource

        URL remoteURL;
        String authHash;
        try {
            if (url.length() == 0) {
                Workbench.log(new LogMessage(Level.WARNING, "Bridge", "No remote URL"));
                return;
            }
            remoteURL = new URL(url);

            Query query = new Query(remoteURL.getQuery());
            if ((authHash = query.get(SecureHashAuthManager.QUERY_PARAM_AUTH)) == null || authHash.length() == 0) {
                Workbench.log(new LogMessage(Level.WARNING, "Bridge", "Missing auth query parameter in remote URL: " + url));
                return;
            }

            // Cut off query parameter (only used for auth)
            remoteURL = new URL(remoteURL.getProtocol(), remoteURL.getHost(), remoteURL.getPort(), remoteURL.getPath());

        } catch (MalformedURLException ex) {
            Workbench.log(new LogMessage(Level.WARNING, "Bridge", "Invalid remote URL: " + url));
            return;
        }

        // Try to prevent double-links between two hosts - of course our best bet is the host name which is
        // absolutely not unique. So there is no fool proof way how we can stop people from linking two hosts
        // twice...
        for (EndpointResource r : getBridgeUpnpService().getRegistry().getResources(EndpointResource.class)) {
            URL existingCallbackURL = r.getModel().getCallback();
            if (existingCallbackURL.getAuthority().equals(remoteURL.getAuthority()) &&
                    existingCallbackURL.getProtocol().equals(remoteURL.getProtocol())) {
                Workbench.log(new LogMessage(
                        Level.WARNING,
                        "Bridge",
                        "Link exists with the host of the callback URL, not creating endpoint: " + r.getModel()
                ));
                return;
            }
        }

        AuthCredentials credentials = new HashCredentials(authHash);

        Endpoint endpoint = new Endpoint(UUID.randomUUID().toString(), remoteURL, true, credentials);
        EndpointResource endpointResource = createEndpointResource(endpoint);

        boolean success = getLinkManager().registerAndPut(endpointResource, timeoutSeconds);
        if (success) {
            Workbench.log(new LogMessage(
                    Level.INFO,
                    "Bridge",
                    "Created link between this and remote bridge: " + remoteURL
            ));
        } else {
            Workbench.log(new LogMessage(
                    Level.WARNING,
                    "Bridge",
                    "Link creation failed with remote: " + remoteURL
            ));
        }
    }

    protected EndpointResource createEndpointResource(Endpoint endpoint) {
        BridgeUpnpServiceConfiguration cfg = getBridgeUpnpService().getConfiguration();
        return new EndpointResource(
                cfg.getNamespace().getEndpointPath(endpoint.getId()),
                cfg.getLocalEndpointURL(),
                endpoint
        ) {
            @Override
            public LinkManager getLinkManager() {
                return EndpointController.this.getLinkManager();
            }
        };
    }


    class EndpointList extends JPanel {

        public EndpointList(List<EndpointResource> resources) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE);
            for (EndpointResource resource : resources) {
                add(new EndpointListItem(resource));
            }
        }

    }

    class EndpointListItem extends JPanel {

        EndpointListItem(final EndpointResource endpointResource) {

            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setBackground(Color.WHITE);
            setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel iconButtonPanel = new JPanel();
            iconButtonPanel.setLayout(new BoxLayout(iconButtonPanel, BoxLayout.Y_AXIS));
            JLabel iconLabel = new JLabel(Application.createImageIcon(Workbench.class, "img/48/items.png"));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            iconButtonPanel.add(iconLabel);

            JPanel labelsPanel = new JPanel();
            labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
            /*
            JLabel idLabel = new JLabel(endpointResource.getModel().getId());
            idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            labelsPanel.add(idLabel);
            */
            JLabel callbackLabel = new JLabel(endpointResource.getModel().getCallbackString());
            callbackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            labelsPanel.add(callbackLabel);

            JButton disconnectButton = new JButton("Disconnect");
            disconnectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            labelsPanel.add(disconnectButton);

            disconnectButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getBridge().getBridgeUpnpService().getLinkManager().deregisterAndDelete(endpointResource);
                }
            });

            iconButtonPanel.setBackground(Color.WHITE);
            labelsPanel.setBackground(Color.WHITE);

            add(iconButtonPanel);
            add(labelsPanel);
        }
    }
}
