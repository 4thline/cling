package example.binarylight;

import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Christian Bauer
 */
public class BinaryLightTest {

    @Test
    public void testServer() throws Exception {
        LocalDevice binaryLight = new BinaryLightServer().createDevice();
        assertEquals(binaryLight.getServices()[0].getAction("SetTarget").getName(), "SetTarget");
    }

    @Test
    public void testClient() throws Exception {
        // Well we can't really test the listener easily, but the action invocation should work on a local device

        MockUpnpService upnpService = new MockUpnpService();

        BinaryLightClient client = new BinaryLightClient();
        LocalDevice binaryLight = new BinaryLightServer().createDevice();

        LocalService<SwitchPower> service = binaryLight.getServices()[0];
        client.executeAction(upnpService, binaryLight.getServices()[0]);
        Thread.sleep(100);
        assertEquals(service.getManager().getImplementation().getStatus(), true);
    }

}
