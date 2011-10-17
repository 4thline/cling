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

import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.types.ErrorCode;

/**
 * Special executor for one action, the deprecated "query the value of the any state variable" action.
 * 
 * @author Christian Bauer
 */
public class QueryStateVariableExecutor extends AbstractActionExecutor {
    
    @Override
    protected void execute(ActionInvocation<LocalService> actionInvocation, Object serviceImpl) throws Exception {

        // Querying a state variable doesn't mean an actual "action" method on this instance gets invoked
        if (actionInvocation.getAction() instanceof QueryStateVariableAction) {
            if (!actionInvocation.getAction().getService().isSupportsQueryStateVariables()) {
                actionInvocation.setFailure(
                        new ActionException(ErrorCode.INVALID_ACTION, "This service does not support querying state variables")
                );
            } else {
                executeQueryStateVariable(actionInvocation, serviceImpl);
            }
        } else {
            throw new IllegalStateException(
                    "This class can only execute QueryStateVariableAction's, not: " + actionInvocation.getAction()
            );
        }
    }

    protected void executeQueryStateVariable(ActionInvocation<LocalService> actionInvocation, Object serviceImpl) throws Exception {

        LocalService service = actionInvocation.getAction().getService();

        String stateVariableName = actionInvocation.getInput("varName").toString();
        StateVariable stateVariable = service.getStateVariable(stateVariableName);

        if (stateVariable == null) {
            throw new ActionException(
                    ErrorCode.ARGUMENT_VALUE_INVALID, "No state variable found: " + stateVariableName
            );
        }

        StateVariableAccessor accessor;
        if ((accessor = service.getAccessor(stateVariable.getName())) == null) {
            throw new ActionException(
                    ErrorCode.ARGUMENT_VALUE_INVALID, "No accessor for state variable, can't read state: " + stateVariableName
            );
        }

        try {
            setOutputArgumentValue(
                    actionInvocation,
                    actionInvocation.getAction().getOutputArgument("return"),
                    accessor.read(stateVariable, serviceImpl).toString()
            );
        } catch (Exception ex) {
            throw new ActionException(ErrorCode.ACTION_FAILED, ex.getMessage());
        }
    }

}
