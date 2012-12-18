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

package org.fourthline.cling.bridge.ui;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.bridge.ui.backend.Bridge;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.support.igd.callback.GetExternalIP;
import org.seamless.swing.AbstractController;
import org.seamless.swing.Controller;
import org.seamless.swing.Form;
import org.seamless.swing.logging.LogMessage;
import org.seamless.util.URIUtil;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;

/**
 * @author Christian Bauer
 */
public class SettingsController extends AbstractController<JPanel> {

    // Dependencies
    final protected UpnpService upnpService;
    final protected Bridge bridge;

    // View
    final protected JTextField wanHostField = new JTextField("IP/hostname of this host on WAN");
    final protected JButton getIPButton = new JButton("Get External IP");

    final protected JTextField lanHostField = new JTextField();

    final protected JTextField portField = new JTextField();
    final protected JCheckBox mapPortCheckbox = new JCheckBox("Map Port");

    final protected JTextField activeLocalURLField = new JTextField();
    final protected JPanel startStopBridgePanel = new JPanel();
    final protected JButton startButton = new JButton("Start Bridge");
    final protected JButton stopButton = new JButton("Stop Bridge");

    // Model
    final protected Service connectionService;

    public SettingsController(Controller parentController, UpnpService upnpService, Bridge bridge) {
        super(new JPanel(new GridBagLayout()), parentController);

        this.upnpService = upnpService;
        this.bridge = bridge;

        this.connectionService = findConnectionService();
        if (connectionService == null) {
            getIPButton.setEnabled(false);
            lanHostField.setEnabled(false);
            mapPortCheckbox.setEnabled(false);
            Workbench.log(new LogMessage(
                    Level.INFO,
                    "Bridge",
                    "No InternetGatewayDevice for automatic NAT port mapping discovered."
            ));
            Workbench.log(new LogMessage(
                    Level.INFO,
                    "Bridge",
                    "Ensure this host is reachable from the WAN via TCP and the shown port!"
            ));
        }

        mapPortCheckbox.setSelected(true);
        activeLocalURLField.setEditable(false);
        startStopBridgePanel.setLayout(new BoxLayout(startStopBridgePanel, BoxLayout.X_AXIS));
        startStopBridgePanel.add(startButton);
        startStopBridgePanel.add(stopButton);


        getIPButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                retrieveExternalIP();
            }
        });

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                startBridge();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                stopBridge();
            }
        });

        Form form = new Form(3);
        form.lastConstraints.weightx = 0;
        setLabelAndTextField(form, "WAN Host:", wanHostField, getIPButton);
        setLabelAndTextField(form, "TCP Port:", portField, null);
        setLabelAndTextField(form, "LAN Host:", lanHostField, mapPortCheckbox);
        setLabelAndTextField(form, "Local URL:", activeLocalURLField, startStopBridgePanel);

        if (getBridge().getBridgeWebServer() != null) {
            // Bridge is running
            wanHostField.setText(getBridge().getLocalURL().getHost());
            lanHostField.setText(getBridge().getLanHost());
            portField.setText(Integer.toString(getBridge().getLocalURL().getPort()));
            mapPortCheckbox.setSelected(getBridge().isMapPort());
            activeLocalURLField.setText(getBridge().getLocalURL().toString());
            toggleStoppable();
        } else {
            // Bridge is not running
            retrieveExternalIP();
            String localAddress = findLocalAddress();
            lanHostField.setText(localAddress);
            Integer localPort = findUsablePort(localAddress);
            if (localPort != null) portField.setText(localPort.toString());
            toggleStartable();
        }
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public Service getConnectionService() {
        return connectionService;
    }

    protected void setLabelAndTextField(Form form, String l, Component middle, Component last) {
        JLabel label = new JLabel(l);
        label.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        form.addLabel(label, getView());
        form.addMiddleField(middle != null ? middle : new JLabel(), getView());
        form.addLastField(last != null ? last : new JLabel(), getView());
    }

    protected URL getLocalURL() {
        if (wanHostField.getText().length() == 0 || portField.getText().length() == 0) return null;
        if (!wanHostField.getText().matches("[a-zA-Z0-9-\\.]+")) return null;
        if (!portField.getText().matches("[0-9]+")) return null;

        return URIUtil.toURL(URI.create(
                "http://" + wanHostField.getText() + ":" + portField.getText()
        ));
    }

    protected String getLANHost() {
        if (lanHostField.getText().length() == 0) return null;
        if (!lanHostField.getText().matches("[a-zA-Z0-9-\\.]+")) return null;
        return lanHostField.getText();
    }

    protected Service findConnectionService() {
        Collection<Device> devices;

        // First device and service that matches
        devices = getUpnpService().getRegistry().getDevices(new UDAServiceType("WANIPConnection"));
        if (devices.size() > 0)
            return devices.iterator().next().findService(new UDAServiceType("WANIPConnection"));

        // First device and service that matches
        devices = getUpnpService().getRegistry().getDevices(new UDAServiceType("WANPPPConnection"));
        if (devices.size() > 0)
            return devices.iterator().next().findService(new UDAServiceType("WANPPPConnection"));

        return null;
    }

    protected String findLocalAddress() {
        // Try to find the local address on which we discovered the IGD connection service
        if (getConnectionService() != null && getConnectionService().getDevice() instanceof RemoteDevice) {
            return ((RemoteDevice) getConnectionService().getDevice())
                    .getIdentity().getDiscoveredOnLocalAddress().getHostAddress();
        } else {
            // Take whatever is the first network interface on the local machine
            return getUpnpService().getRouter().getNetworkAddressFactory().getBindAddresses()[0].getHostAddress();
        }
    }

    protected Integer findUsablePort(String address) {
        // Try to find an ephemeral port
        try {
            ServerSocket sock = new ServerSocket();
            sock.bind(address != null ? new InetSocketAddress(address, 0) : null);
            Integer port = sock.getLocalPort();
            sock.close();
            return port;
        } catch (Exception ex) {
            Workbench.log(new LogMessage(
                    Level.INFO,
                    "Bridge",
                    "Couldn't discover free ephemeral port on LAN address '" + address + "': " + ex
            ));
        }
        return null;
    }


    protected void retrieveExternalIP() {
        if (getConnectionService() == null) return;
        getUpnpService().getControlPoint().execute(
                new GetExternalIP(getConnectionService()) {
                    @Override
                    protected void success(final String externalIPAddress) {
                        Workbench.log(new LogMessage(
                                Level.INFO,
                                "Bridge",
                                "Retrieved external IP address from WANIPConnection: " + externalIPAddress
                        ));
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                wanHostField.setText(externalIPAddress);
                            }
                        });
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        Workbench.log(new LogMessage(
                                Level.INFO,
                                "Bridge",
                                "Can't retrieve external IP: " + defaultMsg
                        ));
                    }
                }
        );
    }

    protected void startBridge() {

        final String lanHost = getLANHost();
        if (lanHost == null) {
            Workbench.log(new LogMessage(
                    Level.WARNING,
                    "Bridge",
                    "Invalid LAN hostname/IP, can't start bridge"
            ));
        }

        final URL localURL = getLocalURL();
        if (localURL == null) {
            Workbench.log(new LogMessage(
                    Level.WARNING,
                    "Bridge",
                    "Invalid WAN hostname/IP or port, can't start bridge"
            ));
            return;
        }

        try {

            getBridge().start(lanHost, localURL, getConnectionService(), mapPortCheckbox.isSelected());

            Workbench.log(new LogMessage(
                    Level.INFO,
                    "Bridge",
                    "Started bridge with local base URL: " + localURL
            ));

            toggleStoppable();
            fireEventGlobal(new BridgeStartedEvent(getBridge()));

        } catch (Exception ex) {
            Workbench.log(new LogMessage(Level.WARNING, "Bridge", ex.toString()));
        }
    }

    protected void stopBridge() {
        getBridge().stop(false);
        Workbench.log(new LogMessage(Level.INFO, "Bridge", "Stopped bridge"));
        toggleStartable();
        fireEventGlobal(new BridgeStoppedEvent(getBridge()));
    }

    protected void toggleStartable() {
        activeLocalURLField.setText("Start bridge to generate local URL");
        startButton.setVisible(true);
        stopButton.setVisible(false);
    }

    protected void toggleStoppable() {
        activeLocalURLField.setText(
                getBridge().getBridgeUpnpService().getConfiguration().getLocalEndpointURLWithCredentials().toString()
        );
        startButton.setVisible(false);
        stopButton.setVisible(true);
    }

}
