/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.model.meta;


import org.fourthline.cling.model.Validatable;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.types.BinHexDatatype;
import org.seamless.util.io.IO;
import org.seamless.util.MimeType;
import org.seamless.util.URIUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The metadata of a device icon, might include the actual image data of a local icon.
 *
 * <p>
 * Note that validation of icons is lax on purpose, a valid <code>Icon</code> might still
 * return <code>null</code> from {@link #getMimeType()}, {@link #getWidth()},
 * {@link #getHeight()}, and {@link #getDepth()}. However, {@link #getUri()} will return
 * a valid URI for a valid <code>Icon</code>.
 * </p>
 *
 * @author Christian Bauer
 */
public class Icon implements Validatable {

    final private static Logger log = Logger.getLogger(StateVariable.class.getName());

    final private MimeType mimeType;
    final private int width;
    final private int height;
    final private int depth;
    final private URI uri;
    final private byte[] data;

    // Package mutable state
    private Device device;

    public Icon(String mimeType, int width, int height, int depth, String uri) throws IllegalArgumentException {
        this(mimeType, width, height, depth, URI.create(uri), "");
    }

    public Icon(String mimeType, int width, int height, int depth, URI uri) {
        this(mimeType, width, height, depth, uri, "");
    }

    /**
     * @param data The icon bytes encoded as BinHex.
     */
    public Icon(String mimeType, int width, int height, int depth, URI uri, String data) {
        this(
                mimeType, width, height, depth, uri,
                data != null && !data.equals("") ? new BinHexDatatype().valueOf(data) : null
        );
    }

    public Icon(String mimeType, int width, int height, int depth, URL url) throws IOException{
        this(mimeType, width, height, depth, new File(URIUtil.toURI(url)));
    }

    public Icon(String mimeType, int width, int height, int depth, URI uri, InputStream is) throws IOException {
        this(mimeType, width, height, depth, uri, IO.readBytes(is));
    }

    public Icon(String mimeType, int width, int height, int depth, File file) throws IOException {
        this(mimeType, width, height, depth, URI.create(file.getName()), IO.readBytes(file));
    }

    public Icon(String mimeType, int width, int height, int depth, URI uri, byte[] data) {
        this(mimeType != null && mimeType.length() > 0 ? MimeType.valueOf(mimeType) : null, width, height, depth, uri, data);
    }

    public Icon(MimeType mimeType, int width, int height, int depth, URI uri, byte[] data) {
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.uri = uri;
        this.data = data;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public URI getUri() {
        return uri;
    }

    public byte[] getData() {
        return data;
    }

    public Device getDevice() {
        return device;
    }

    void setDevice(Device device) {
        if (this.device != null)
            throw new IllegalStateException("Final value has been set already, model is immutable");
        this.device = device;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList();

        if (getMimeType() == null) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing mime type: " + this);
        }
        if (getWidth() == 0) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing width: " + this);
        }
        if (getHeight() == 0) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing height: " + this);
        }
        if (getDepth() == 0) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing bitmap depth: " + this);
        }

        if (getUri() == null) {
            errors.add(new ValidationError(
                    getClass(),
                    "uri",
                    "URL is required"
            ));
        } else {
        	try {
        		URL testURI = getUri().toURL();
        		if (testURI == null)
        			throw new MalformedURLException();
        	} catch (MalformedURLException ex) {
        		errors.add(new ValidationError(
        				getClass(),
        				"uri",
        				"URL must be valid: " + ex.getMessage())
        				);
        	} catch (IllegalArgumentException ex) {
        		// Relative URI is fine here!
        	}
        }

        return errors;
    }

    public Icon deepCopy() {
        return new Icon(
                getMimeType(),
                getWidth(),
                getHeight(),
                getDepth(),
                getUri(),
                getData()
        );
    }

    @Override
    public String toString() {
        return "Icon(" + getWidth() + "x" + getHeight() + ", MIME: " + getMimeType() + ") " + getUri();
    }
}