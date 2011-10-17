package example.binarylight;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.*;
import org.fourthline.cling.model.action.*;
import org.fourthline.cling.model.message.*;
import org.fourthline.cling.model.message.header.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.registry.*;

public class BinaryLightClient implements Runnable {

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread clientThread = new Thread(new BinaryLightClient());
        clientThread.setDaemon(false);
        clientThread.start();

    }

    public void run() {
        try {

            UpnpService upnpService = new UpnpServiceImpl();

            // Add a listener for device registration events
            upnpService.getRegistry().addListener(
                    createRegistryListener(upnpService)
            );

            // Broadcast a search message for all devices
            upnpService.getControlPoint().search(
                    new STAllHeader()
            );

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            System.exit(1);
        }
    }

    // DOC: REGISTRYLISTENER
    RegistryListener createRegistryListener(final UpnpService upnpService) {
        return new DefaultRegistryListener() {

            ServiceId serviceId = new UDAServiceId("SwitchPower");

            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

                Service switchPower;
                if ((switchPower = device.findService(serviceId)) != null) {

                    System.out.println("Service discovered: " + switchPower);
                    executeAction(upnpService, switchPower);

                }

            }

            @Override
            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                Service switchPower;
                if ((switchPower = device.findService(serviceId)) != null) {
                    System.out.println("Service disappeared: " + switchPower);
                }
            }

        };
    }
    // DOC: REGISTRYLISTENER
    // DOC: EXECUTEACTION
    void executeAction(UpnpService upnpService, Service switchPowerService) {

            ActionInvocation setTargetInvocation =
                    new SetTargetActionInvocation(switchPowerService);

            // Executes asynchronous in the background
            upnpService.getControlPoint().execute(
                    new ActionCallback(setTargetInvocation) {

                        @Override
                        public void success(ActionInvocation invocation) {
                            assert invocation.getOutput().length == 0;
                            System.out.println("Successfully called action!");
                        }

                        @Override
                        public void failure(ActionInvocation invocation,
                                            UpnpResponse operation,
                                            String defaultMsg) {
                            System.err.println(defaultMsg);
                        }
                    }
            );

    }

    class SetTargetActionInvocation extends ActionInvocation {

        SetTargetActionInvocation(Service service) {
            super(service.getAction("SetTarget"));
            try {

                // Throws InvalidValueException if the value is of wrong type
                setInput("NewTargetValue", true);

            } catch (InvalidValueException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }
    // DOC: EXECUTEACTION
}
