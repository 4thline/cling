package example.mediarenderer;

import org.fourthline.cling.support.avtransport.impl.AVTransportStateMachine;
import org.seamless.statemachine.States;

@States({
        MyRendererNoMediaPresent.class,
        MyRendererStopped.class,
        MyRendererPlaying.class
})
interface MyRendererStateMachine extends AVTransportStateMachine {}
