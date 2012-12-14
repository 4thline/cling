/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
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

package org.fourthline.cling.binding.xml;

import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import org.seamless.xml.ParserException;
import org.xml.sax.SAXParseException;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Pujos
 */
public class RecoveringUDA10DeviceDescriptorBinderImpl extends UDA10DeviceDescriptorBinderImpl {

    private static Logger log = Logger.getLogger(RecoveringUDA10DeviceDescriptorBinderImpl.class.getName());

    @Override
    public <D extends Device> D describe(D undescribedDevice, String descriptorXml) throws DescriptorBindingException, ValidationException {
        D device = null;
        DescriptorBindingException firstException = null;
        try {
            // Try to fix "up to five" missing namespace declarations
            for (int retryCount = 0; retryCount < 5; retryCount++) {
                try {
                    device = super.describe(undescribedDevice, descriptorXml);
                    return device;
                } catch (DescriptorBindingException ex) {

                    // Windows: org.fourthline.cling.binding.xml.DescriptorBindingException: Could not parse device descriptor: org.seamless.xml.ParserException: org.xml.sax.SAXParseException: The prefix "dlna" for element "dlna:X_DLNADOC" is not bound.
                    // Android: org.xmlpull.v1.XmlPullParserException: undefined prefix: dlna (position:START_TAG <{null}dlna:X_DLNADOC>@19:17 in java.io.StringReader@406dff48)

                    if (firstException == null)
                        firstException = ex;

                    // We can only handle certain exceptions, depending on their type and message
                    Throwable cause = ex.getCause();
                    if (!((cause instanceof SAXParseException) || (cause instanceof ParserException)))
                        throw ex;
                    String message = cause.getMessage();
                    if (message == null)
                        throw ex;

                    Pattern pattern = Pattern.compile("The prefix \"(.*)\" for element"); // Windows
                    Matcher matcher = pattern.matcher(message);
                    if (!matcher.find() || matcher.groupCount() != 1) {
                        pattern = Pattern.compile("undefined prefix: ([^ ]*)"); // Android
                        matcher = pattern.matcher(message);
                        if (!matcher.find() || matcher.groupCount() != 1)
                            throw ex;
                    }

                    String missingNS = matcher.group(1);
                    log.warning("Detected missing namespace declaration: " + missingNS);

                    // Extract <root> attrbiutes
                    pattern = Pattern.compile("<root([^>]*)");
                    matcher = pattern.matcher(descriptorXml);
                    if (!matcher.find() || matcher.groupCount() != 1)
                        throw ex;

                    String rootAttributes = matcher.group(1);

                    // Extract <root> body
                    pattern = Pattern.compile("<root[^>]*>(.*)</root>", Pattern.DOTALL);
                    matcher = pattern.matcher(descriptorXml);
                    if (!matcher.find() || matcher.groupCount() != 1)
                        throw ex;

                    String rootBody = matcher.group(1);

                    // Add missing namespace, it only matters that it is defined, not that it is correct
                    descriptorXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                        + "<root "
                        + String.format("xmlns:%s=\"urn:schemas-dlna-org:device-1-0\"", missingNS) + rootAttributes + ">"
                        + rootBody
                        + "</root>";

                    // TODO: Should we match different undeclared prefixes with their correct namespace?
                    // So if it's "dlna" we use "urn:schemas-dlna-org:device-1-0" etc.
                }
            }
           throw firstException;
        } catch (DescriptorBindingException ex) {
            handleInvalidDescriptor(descriptorXml, firstException);
        } catch (ValidationException ex) {
            device = handleInvalidDevice(descriptorXml, device, ex);
            if (device != null)
                return device;
        }
        throw new IllegalStateException("No device produced, did you swallow exceptions in your subclass?");
    }

    /**
     * Handle processing errors while reading XML descriptors.
     *
     * <p>
     * Typically you want to log this problem or create an error report, and in any
     * case, throw a {@link DescriptorBindingException} to notify the caller of the
     * binder of this failure. The default implementation simply rethrows the
     * given exception.
     * </p>
     *
     * @param xml The original XML causing the parsing failure.
     * @param exception The original exception while parsing the XML.
     */
    protected void handleInvalidDescriptor(String xml, DescriptorBindingException exception)
        throws DescriptorBindingException{
        throw exception;
    }

    /**
     * Handle processing errors while binding XML descriptors.
     *
     * <p>
     * Typically you want to log this problem or create an error report. You
     * should throw a {@link ValidationException} to notify the caller of the
     * binder of failure. The default implementation simply rethrows the
     * given exception.
     * </p>
     * <p>
     * This method gives you a final chance to fix the problem, instead of
     * throwing an exception, you could try to create valid {@link Device}
     * model and return it.
     * </p>
     * @param xml The original XML causing the binding failure.
     * @param device The unfinished {@link Device} that failed validation
     * @param exception The errors found when validating the {@link Device} model.
     * @return Device A "fixed" {@link Device} model, instead of throwing an exception.
     */
    protected <D extends Device> D handleInvalidDevice(String xml, D device, ValidationException exception)
        throws ValidationException {
        throw exception;
    }
}
