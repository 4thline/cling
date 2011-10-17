package example.localservice;

import example.binarylight.SwitchPower;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

public class SwitchPowerModerated extends SwitchPower {

    @UpnpStateVariable(eventMaximumRateMilliseconds = 500)
    public String moderatedMaxRateVar = "one";

    @UpnpStateVariable(eventMinimumDelta = 3)
    public Integer moderatedMinDeltaVar = 1;

}
