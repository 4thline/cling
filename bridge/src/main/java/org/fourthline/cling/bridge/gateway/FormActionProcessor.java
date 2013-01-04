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

package org.fourthline.cling.bridge.gateway;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.InvalidValueException;
import org.seamless.util.URIUtil;
import org.seamless.xhtml.XHTMLElement;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;

import static org.seamless.xhtml.XHTML.ATTR;
import static org.seamless.xhtml.XHTML.ELEMENT;

/**
 * @author Christian Bauer
 */
public class FormActionProcessor {

    public static final String NULL_OUTPUT_ARGUMENT_VALUE = "<<NULL>>";

    public ActionInvocation createInvocation(MultivaluedMap<String, String> form, Action action) throws InvalidValueException {
        ActionInvocation invocation = new ActionInvocation(action);
        if (action.hasInputArguments()) {
            for (ActionArgument arg : action.getInputArguments()) {
                // The first is OK, multiple keys we just ignore
                invocation.setInput(
                        new ActionArgumentValue(arg, form.getFirst(arg.getName()))
                );
            }
        }
        return invocation;
    }

    public void appendOutput(ActionInvocation invocation, MultivaluedMap<String, String> form) {
        for (ActionArgumentValue value : invocation.getOutput()) {
            String stringValue = value.toString();
            form.putSingle(
                    value.getArgument().getName(),
                    stringValue.length() > 0 ? stringValue : NULL_OUTPUT_ARGUMENT_VALUE
            );
        }
    }

    public void appendOutput(ActionInvocation invocation, XHTMLElement output) {
        output.createChild(ELEMENT.span).setContent("SUCCESS");
        if (invocation.getAction().hasOutputArguments()) {
            output.createChild("hr");
            XHTMLElement outputArgs = output.createChild(ELEMENT.dl).setAttribute(ATTR.id, "output-args");
            for (ActionArgumentValue value : invocation.getOutput()) {
                outputArgs.createChild(ELEMENT.dt).setContent(value.getArgument().getName());
                String stringValue = value.toString();
                outputArgs.createChild(ELEMENT.dd).setContent(
                        stringValue.length() > 0 ? stringValue : FormActionProcessor.NULL_OUTPUT_ARGUMENT_VALUE
                );
            }
        }
    }

    public void appendFailure(ActionInvocation invocation, MultivaluedMap<String, String> form) {
        final ActionException exception = invocation.getFailure();
        if (exception != null) {
            form.putSingle("error-code", String.valueOf(exception.getErrorCode()));
            form.putSingle("error-description", exception.getMessage());
        }
    }

    public void appendFailure(ActionInvocation invocation, XHTMLElement output) {
        output.createChild(ELEMENT.span).setContent("FAILURE");
        output.createChild(ELEMENT.span).setAttribute(ATTR.id, "error-code").setContent(
                String.valueOf(invocation.getFailure().getErrorCode())
        );
        output.createChild(ELEMENT.span).setAttribute(ATTR.id, "error-description").setContent(
                invocation.getFailure().getMessage()
        );
    }

    public void readOutput(MultivaluedMap<String, String> form, ActionInvocation invocation) throws InvalidValueException {
        for (ActionArgument argument : invocation.getAction().getOutputArguments()) {
            String v = form.getFirst(argument.getName());
            invocation.setOutput(new ActionArgumentValue(argument, v));
        }
    }

    public void readFailure(MultivaluedMap<String, String> form, ActionInvocation invocation) {
        String errorCode = URIUtil.percentDecode(form.getFirst("error-code"));
        String errorDescription = URIUtil.percentDecode(form.getFirst("error-description"));
        if (errorCode == null || errorCode.length() == 0) {
            errorCode = String.valueOf(ErrorCode.ACTION_FAILED.getCode());
            errorDescription = "No error description received";
        }
        invocation.setFailure(
                new ActionException(Integer.parseInt(errorCode), errorDescription)
        );
    }

    public MultivaluedMap<String, String> createForm(ActionInvocation invocation) {
        MultivaluedMap<String, String> form = new MultivaluedMapImpl();
        Action action = invocation.getAction();
        if (action.hasInputArguments()) {
            for (ActionArgumentValue argumentValue : invocation.getInput()) {
                form.putSingle(argumentValue.getArgument().getName(), argumentValue.toString());
            }
        }
        return form;
    }

    public String createFormString(ActionInvocation invocation) {
        return toString(createForm(invocation));
    }

    public String toString(MultivaluedMap<String, String> form) {
        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : form.entrySet()) {
            for (String value : entry.getValue()) {
                s.append(entry.getKey()).append("=").append(URIUtil.percentEncode(value));
                s.append("&");
            }
        }
        if (s.toString().endsWith("&"))
            s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    public MultivaluedMap<String, String> valueOf(String s) {
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl<String, String>();
        String[] params = s.split("&");
        for (String param : params) {
            if (param.indexOf('=') >= 0) {
                String[] nv = param.split("=");
                String val = nv.length > 1 ? nv[1] : "";
                formData.add(nv[0], URIUtil.percentDecode(val));
            } else {
                formData.add(param, "");
            }
        }
        return formData;
    }

}
