package example.messagebox;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDN;

/**
 * @author Christian Bauer
 */
public class MessageBoxSampleData {

    public static LocalService readService(Class<?> serviceClass) throws Exception {
        LocalService service = new AnnotationLocalServiceBinder().read(serviceClass);
        service.setManager(
                new DefaultServiceManager(service, serviceClass)
        );
        return service;
    }

    public static LocalDevice createDevice(Class<?> serviceClass) throws Exception {
        return new LocalDevice(
                new DeviceIdentity(new UDN("1111")),
                new DeviceType("samsung.com", "PersonalMessageReceiver"),
                new DeviceDetails("My TV"),
                readService(serviceClass)
        );
    }

    @UpnpService(
            serviceId = @UpnpServiceId(namespace = "samsung.com", value = "MessageBoxService"),
            serviceType = @UpnpServiceType(namespace = "samsung.com", value = "MessageBoxService")
    )
    @UpnpStateVariables({
            @UpnpStateVariable(name ="A_ARG_TYPE_MessageID", datatype = "string", sendEvents = false),
            @UpnpStateVariable(name ="A_ARG_TYPE_MessageType", datatype = "string", sendEvents = false, defaultValue = "text/xml; charset=\"utf-8\""),
            @UpnpStateVariable(name ="A_ARG_TYPE_Message", datatype = "string", sendEvents = false)
    })
    public static class MessageBoxService {

        @UpnpAction
        public void addMessage(@UpnpInputArgument(name = "MessageID") String id,
                               @UpnpInputArgument(name = "MessageType") String type,
                               @UpnpInputArgument(name = "Message") String messageText) {
            checkMessage(id, type, messageText);
        }

        protected void checkMessage(String id, String type, String messageText) {

        }

    }
}
