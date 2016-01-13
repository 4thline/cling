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

package org.fourthline.cling.support.contentdirectory.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerEightBytes;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;

import java.util.logging.Logger;

/**
 * Invokes a "Browse" action, parses the result.
 *
 * @author Christian Bauer
 */
public abstract class Browse extends ActionCallback {

    public static final String CAPS_WILDCARD = "*";

    public enum Status {
        NO_CONTENT("No Content"),
        LOADING("Loading..."),
        OK("OK");

        private String defaultMessage;

        Status(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }

    private static Logger log = Logger.getLogger(Browse.class.getName());

    /**
     * Browse with first result 0 and {@link #getDefaultMaxResults()}, filters with {@link #CAPS_WILDCARD}.
     */
    public Browse(Service service, String containerId, BrowseFlag flag) {
        this(service, containerId, flag, CAPS_WILDCARD, 0, null);
    }

    /**
     * @param maxResults Can be <code>null</code>, then {@link #getDefaultMaxResults()} is used.
     */
    public Browse(Service service, String objectID, BrowseFlag flag,
                                String filter, long firstResult, Long maxResults, SortCriterion... orderBy) {

        super(new ActionInvocation(service.getAction("Browse")));

        log.fine("Creating browse action for object ID: " + objectID);

        getActionInvocation().setInput("ObjectID", objectID);
        getActionInvocation().setInput("BrowseFlag", flag.toString());
        getActionInvocation().setInput("Filter", filter);
        getActionInvocation().setInput("StartingIndex", new UnsignedIntegerEightBytes(firstResult));
        getActionInvocation().setInput("RequestedCount",
                new UnsignedIntegerEightBytes(maxResults == null ? getDefaultMaxResults() : maxResults)
        );
        getActionInvocation().setInput("SortCriteria", SortCriterion.toString(orderBy));
    }

    @Override
    public void run() {
        updateStatus(Status.LOADING);
        super.run();
    }

    public void success(ActionInvocation invocation) {
        log.fine("Successful browse action, reading output argument values");

        BrowseResult result = new BrowseResult(
                invocation.getOutput("Result").getValue().toString(),
                (UnsignedIntegerEightBytes) invocation.getOutput("NumberReturned").getValue(),
                (UnsignedIntegerEightBytes) invocation.getOutput("TotalMatches").getValue(),
                (UnsignedIntegerEightBytes) invocation.getOutput("UpdateID").getValue()
        );

        boolean proceed = receivedRaw(invocation, result);

        if (proceed && result.getCountLong() > 0 && result.getResult().length() > 0) {

            try {

                DIDLParser didlParser = new DIDLParser();
                DIDLContent didl = didlParser.parse(result.getResult());
                received(invocation, didl);
                updateStatus(Status.OK);

            } catch (Exception ex) {
                invocation.setFailure(
                        new ActionException(ErrorCode.ACTION_FAILED, "Can't parse DIDL XML response: " + ex, ex)
                );
                failure(invocation, null);
            }

        } else {
            received(invocation, new DIDLContent());
            updateStatus(Status.NO_CONTENT);
        }
    }

    /**
     * Some media servers will crash if there is no limit on the maximum number of results.
     *
     * @return The default limit, 999.
     */
    public long getDefaultMaxResults() {
        return 999;
    }

    public boolean receivedRaw(ActionInvocation actionInvocation, BrowseResult browseResult) {
        /*
        if (log.isLoggable(Level.FINER)) {
            log.finer("-------------------------------------------------------------------------------------");
            log.finer("\n" + XML.pretty(browseResult.getDidl()));
            log.finer("-------------------------------------------------------------------------------------");
        }
        */
        return true;
    }

    public abstract void received(ActionInvocation actionInvocation, DIDLContent didl);
    public abstract void updateStatus(Status status);

}
