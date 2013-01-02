package org.fourthline.cling.transport.spi;

/**
 * Might be thrown by the constructor of {@link NetworkAddressFactory} and
 * {@link org.fourthline.cling.transport.Router} if no usable
 * network interfaces/addresses were discovered.
 *
 * @author Christian Bauer
 */
public class NoNetworkException extends InitializationException {

    public NoNetworkException(String s) {
        super(s);
    }
}
