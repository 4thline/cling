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
package example.mediarenderer;

import org.fourthline.cling.support.avtransport.impl.state.AbstractState;
import org.fourthline.cling.support.avtransport.impl.state.Stopped;
import org.fourthline.cling.support.model.AVTransport;
import org.fourthline.cling.support.model.SeekMode;

import java.net.URI;

/**
 * <p>
 * The Stopped state has many possible transitions, from here a control point
 * can decide to play, seek, skip to the next track, and so on. The following
 * example is really not doing much, how you implement these triggers and
 * state transitions is completely dependend on the design of your playback
 * engine - this is only the scaffolding:
 * </p>
 * <a class="citation" href="javacode://this" style="include: INC1"/>
 * <p>
 * Each state can have two magic methods: <code>onEntry()</code> and
 * <code>onExit()</code> - they do exactly what the name says. Don't forget
 * to call the superclass' method if you decide to use them!
 * </p>
 */
public class MyRendererStopped extends Stopped { // DOC:INC1

    public MyRendererStopped(AVTransport transport) {
        super(transport);
    }

    public void onEntry() {
        super.onEntry();
        // Optional: Stop playing, release resources, etc.
    }

    public void onExit() {
        // Optional: Cleanup etc.
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        // This operation can be triggered in any state, you should think
        // about how you'd want your player to react. If we are in Stopped
        // state nothing much will happen, except that you have to set
        // the media and position info, just like in MyRendererNoMediaPresent.
        // However, if this would be the MyRendererPlaying state, would you
        // prefer stopping first?
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> stop() {
        /// Same here, if you are stopped already and someone calls STOP, well...
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> play(String speed) {
        // It's easier to let this classes' onEntry() method do the work
        return MyRendererPlaying.class;
    }

    @Override
    public Class<? extends AbstractState> next() {
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> previous() {
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> seek(SeekMode unit, String target) {
        // Implement seeking with the stream in stopped state!
        return MyRendererStopped.class;
    }
} // DOC:INC1