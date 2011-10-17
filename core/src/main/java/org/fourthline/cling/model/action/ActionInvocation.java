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

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The input, output, and failure values of an action invocation.
 *
 * @author Christian Bauer
 */
public class ActionInvocation<S extends Service> {

    final protected Action<S> action;

    // We don't necessarily have to preserve insertion order but it's nicer if the arrays returned
    // by the getters are reliable
    protected Map<String, ActionArgumentValue<S>> input = new LinkedHashMap();
    protected Map<String, ActionArgumentValue<S>> output = new LinkedHashMap();

    protected ActionException failure = null;

    public ActionInvocation(Action<S> action) {
        this(action, null, null);
    }

    public ActionInvocation(Action<S> action, ActionArgumentValue<S>[] input) {
        this(action, input, null);
    }

    public ActionInvocation(Action<S> action, ActionArgumentValue<S>[] input, ActionArgumentValue<S>[] output) {
        if (action == null) {
            throw new IllegalArgumentException("Action can not be null");
        }
        this.action = action;

        setInput(input);
        setOutput(output);
    }

    public ActionInvocation(ActionException failure) {
        this.action = null;
        this.input = null;
        this.output = null;
        this.failure = failure;
    }

    public Action<S> getAction() {
        return action;
    }

    public ActionArgumentValue<S>[] getInput() {
        return input.values().toArray(new ActionArgumentValue[input.size()]);
    }

    public ActionArgumentValue<S> getInput(String argumentName) {
        return getInput(getInputArgument(argumentName));
    }

    public ActionArgumentValue<S> getInput(ActionArgument<S> argument) {
        return input.get(argument.getName());
    }

    public Map<String, ActionArgumentValue<S>> getInputMap() {
        return Collections.unmodifiableMap(input);
    }

    public ActionArgumentValue<S>[] getOutput() {
        return output.values().toArray(new ActionArgumentValue[output.size()]);
    }

    public ActionArgumentValue<S> getOutput(String argumentName) {
        return getOutput(getOutputArgument(argumentName));
    }

    public Map<String, ActionArgumentValue<S>> getOutputMap() {
        return Collections.unmodifiableMap(output);
    }

    public ActionArgumentValue<S> getOutput(ActionArgument<S> argument) {
        return output.get(argument.getName());
    }

    public void setInput(String argumentName, Object value) throws InvalidValueException {
        setInput(new ActionArgumentValue(getInputArgument(argumentName), value));
    }

    public void setInput(ActionArgumentValue<S> value) {
        input.put(value.getArgument().getName(), value);
    }

    public void setInput(ActionArgumentValue<S>[] input) {
        if (input == null) return;
        for (ActionArgumentValue<S> argumentValue : input) {
            this.input.put(argumentValue.getArgument().getName(), argumentValue);
        }
    }

    public void setOutput(String argumentName, Object value) throws InvalidValueException {
        setOutput(new ActionArgumentValue(getOutputArgument(argumentName), value));
    }

    public void setOutput(ActionArgumentValue<S> value){
        output.put(value.getArgument().getName(), value);
    }

    public void setOutput(ActionArgumentValue<S>[] output) {
        if (output == null) return;
        for (ActionArgumentValue<S> argumentValue : output) {
            this.output.put(argumentValue.getArgument().getName(), argumentValue);
        }
    }

    protected ActionArgument<S> getInputArgument(String name) {
        ActionArgument<S> argument = getAction().getInputArgument(name);
        if (argument == null) throw new IllegalArgumentException("Argument not found: " + name);
        return argument;
    }

    protected ActionArgument<S> getOutputArgument(String name) {
        ActionArgument<S> argument = getAction().getOutputArgument(name);
        if (argument == null) throw new IllegalArgumentException("Argument not found: " + name);
        return argument;
    }


    /**
     * @return <code>null</code> if execution was successful, failure details otherwise.
     */
    public ActionException getFailure() {
        return failure;
    }

    public void setFailure(ActionException failure) {
        this.failure = failure;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") " + getAction();
    }
}