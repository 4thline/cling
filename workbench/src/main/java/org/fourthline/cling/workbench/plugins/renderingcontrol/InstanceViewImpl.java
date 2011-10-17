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
import org.seamless.swing.Application;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Christian Bauer
 */
public class InstanceViewImpl extends JPanel implements InstanceView {

    public static final ImageIcon ICON_MUTE_OFF =
            Application.createImageIcon(RenderingControlPointAdapter.class, "img/32/speaker.png");

    public static final ImageIcon ICON_MUTE_ON =
            Application.createImageIcon(RenderingControlPointAdapter.class, "img/32/speaker_mute.png");

    protected JToggleButton muteButton = new JToggleButton("Mute", ICON_MUTE_OFF);
    protected JSlider volumeSlider;
    protected int instanceId;

    protected Presenter presenter;

    @Override
    public void init(int instanceId, StateVariableAllowedValueRange volumeRange) {

        this.instanceId = instanceId;

        muteButton.setVerticalTextPosition(JToggleButton.BOTTOM);
        muteButton.setHorizontalTextPosition(JToggleButton.CENTER);
        muteButton.setFocusable(false);
        muteButton.setPreferredSize(new Dimension(60, 50));

        muteButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    muteButton.setIcon(ICON_MUTE_ON);
                    volumeSlider.setEnabled(false);
                } else {
                    muteButton.setIcon(ICON_MUTE_OFF);
                    volumeSlider.setEnabled(true);
                }
            }
        });

        muteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.onMuteSelected(getInstanceId(), muteButton.isSelected());
            }
        });

        // Get the volume range supported by the service, if there isn't any, assume 0..100
        int minVolume = 0;
        int maxVolume = 100;
        if (volumeRange != null) {
            minVolume = new Long(volumeRange.getMinimum()).intValue();
            maxVolume = new Long(volumeRange.getMaximum()).intValue();
        }

        volumeSlider = new JSlider(JSlider.HORIZONTAL, minVolume, maxVolume, maxVolume / 2);
        volumeSlider.setBorder(BorderFactory.createTitledBorder("Volume"));
        volumeSlider.setMajorTickSpacing(maxVolume / 5);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        volumeSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    int newVolume = source.getValue();
                    presenter.onVolumeSelected(getInstanceId(), newVolume);
                }
            }
        });

        setLayout(new BorderLayout());
        add(muteButton, BorderLayout.WEST);
        add(volumeSlider, BorderLayout.CENTER);
        setPreferredSize(new Dimension(300, 80));
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public int getInstanceId() {
        return instanceId;
    }

    @Override
    public void setVolume(int volume) {
        setVolumeSliderWithoutNotification(volume);
    }

    @Override
    public void setSelectionEnabled(boolean enabled) {
        muteButton.setEnabled(enabled);
        volumeSlider.setEnabled(enabled);
    }

    // Internal re-positioning, should not fire a Seek UPnP action, so we remove
    // the listener before and add it back afterwards
    // TODO: valueadjusting crap doesn't work!
    protected void setVolumeSliderWithoutNotification(int value) {
        if (value == volumeSlider.getValue()) return;
        ChangeListener[] listeners = volumeSlider.getChangeListeners();
        for (ChangeListener listener : listeners) {
            volumeSlider.removeChangeListener(listener);
        }
        volumeSlider.setValue(value);
        for (ChangeListener listener : listeners) {
            volumeSlider.addChangeListener(listener);
        }
        // Mute button state depends on volume state
        muteButton.setSelected(value == 0);
    }
}
