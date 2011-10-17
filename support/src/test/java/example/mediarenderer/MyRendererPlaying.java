package example.mediarenderer;

import org.fourthline.cling.support.avtransport.impl.state.AbstractState;
import org.fourthline.cling.support.avtransport.impl.state.Playing;
import org.fourthline.cling.support.model.AVTransport;
import org.fourthline.cling.support.model.SeekMode;

import java.net.URI;

/**
 * <p>
 * Usually you'd start playback when the <code>onEntry()</code> method of
 * the Playing state is called:
 * </p>
 * <a class="citation" href="javacode://this" style="include: INC1"/>
 */
public class MyRendererPlaying extends Playing { // DOC:INC1

    public MyRendererPlaying(AVTransport transport) {
        super(transport);
    }

    @Override
    public void onEntry() {
        super.onEntry();
        // Start playing now!
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        // Your choice of action here, and what the next state is going to be!
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> stop() {
        // Stop playing!
        return MyRendererStopped.class;
    } // DOC:INC1

    @Override
    public Class<? extends AbstractState> play(String speed) {
        return null;
    }

    @Override
    public Class<? extends AbstractState> pause() {
        return null;
    }

    @Override
    public Class<? extends AbstractState> next() {
        return null;
    }

    @Override
    public Class<? extends AbstractState> previous() {
        return null;
    }

    @Override
    public Class<? extends AbstractState> seek(SeekMode unit, String target) {
        return null;
    }
}
