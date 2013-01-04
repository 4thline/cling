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
import org.fourthline.cling.support.avtransport.impl.state.NoMediaPresent;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.model.AVTransport;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;

import java.net.URI;

/**
 * <p>
 * The initial state has only one possible transition and an action that
 * triggers this transition:
 * </p>
 * <a class="citation" href="javacode://this" style="include: INC1"/>
 * <p>
 * When a client sets a new URI for playback, you have to prepare your renderer
 * accordingly. You typically want to change the <code>MediaInfo</code> of your
 * <code>AVTransport</code> to reflect the new "current" track, and you might
 * want to expose information about the track, such as the playback duration.
 * How you do this (e.g. you could actually already retrieve the file behind
 * the URL and analyze it) is up to you.
 * </p>
 * <p>
 * The <code>LastChange</code> object is how you notify control points about
 * any changes of state, here we tell the control points that there is a new
 * "AVTransportURI" as well as a new "CurrentTrackURI". You can add more
 * variables and their values to the <code>LastChange</code>, depending on
 * what actually changed - note that you should do this within a single
 * call of <code>setEventedValue(...)</code> if you consider several changes
 * to be atomic. (The <code>LastChange</code> will be polled and send to
 * control points periodically in the background, more about this later.)
 * </p>
 * <p>
 * The <code>AVTransport</code> will transition to the Stopped state after
 * the URI has been set.
 * </p>
 */
public class MyRendererNoMediaPresent extends NoMediaPresent { // DOC:INC1

    public MyRendererNoMediaPresent(AVTransport transport) {
        super(transport);
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {

        getTransport().setMediaInfo(
                new MediaInfo(uri.toString(), metaData)
        );

        // If you can, you should find and set the duration of the track here!
        getTransport().setPositionInfo(
                new PositionInfo(1, metaData, uri.toString())
        );

        // It's up to you what "last changes" you want to announce to event listeners
        getTransport().getLastChange().setEventedValue(
                getTransport().getInstanceId(),
                new AVTransportVariable.AVTransportURI(uri),
                new AVTransportVariable.CurrentTrackURI(uri)
        );
        
        return MyRendererStopped.class;
    }
} // DOC:INC1
