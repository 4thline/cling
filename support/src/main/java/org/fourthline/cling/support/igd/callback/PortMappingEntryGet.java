package org.fourthline.cling.support.igd.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.model.PortMapping;

import java.util.Map;

public abstract class PortMappingEntryGet extends ActionCallback {

    public PortMappingEntryGet(Service service, long index) {
        this(service, null, index);
    }

    protected PortMappingEntryGet(Service service, ControlPoint controlPoint, long index) {
        super(new ActionInvocation(service.getAction("GetGenericPortMappingEntry")), controlPoint);

        getActionInvocation().setInput("NewPortMappingIndex", new UnsignedIntegerTwoBytes(index));
    }

    @Override
    public void success(ActionInvocation invocation) {

        Map<String, ActionArgumentValue<Service>> outputMap = invocation.getOutputMap();
        success(new PortMapping(outputMap));
    }

    protected abstract void success(PortMapping portMapping);
}