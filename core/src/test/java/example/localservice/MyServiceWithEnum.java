package example.localservice;

import org.fourthline.cling.binding.annotations.*;

@UpnpService(                                                                           // DOC:INC1
        serviceId = @UpnpServiceId("MyService"),
        serviceType = @UpnpServiceType(namespace = "mydomain", value = "MyService"),
        stringConvertibleTypes = MyStringConvertible.class
)
public class MyServiceWithEnum {

    public enum Color {
        Red,
        Green,
        Blue
    }

    @UpnpStateVariable
    private Color color;

    @UpnpAction(out = @UpnpOutputArgument(name = "Out"))
    public Color getColor() {
        return color;
    }

    @UpnpAction
    public void setColor(@UpnpInputArgument(name = "In") String color) {
        this.color = Color.valueOf(color);
    }

} // DOC:INC1
