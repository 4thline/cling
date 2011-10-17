package example.binarylight;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.test.data.SampleData;

/**
 * @author Christian Bauer
 */
public class BinaryLightSampleData {

    public static LocalDevice createDevice(Class<?> serviceClass) throws Exception {
        return createDevice(
                SampleData.readService(
                        new AnnotationLocalServiceBinder(),
                        serviceClass
                )
        );
    }

    public static LocalDevice createDevice(LocalService service) throws Exception {
        return new LocalDevice(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("BinaryLight", 1),
                new DeviceDetails("Example Binary Light"),
                service
        );
    }

}
