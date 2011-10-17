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

package org.fourthline.cling.binding.xml;

import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Service;
import org.w3c.dom.Document;

/**
 * Reads and generates service descriptor XML metadata.
 *
 * @author Christian Bauer
 */
public interface ServiceDescriptorBinder {

    public <T extends Service> T describe(T undescribedService, String descriptorXml)
            throws DescriptorBindingException, ValidationException;

    public <T extends Service> T describe(T undescribedService, Document dom)
            throws DescriptorBindingException, ValidationException;

    public String generate(Service service) throws DescriptorBindingException;

    public Document buildDOM(Service service) throws DescriptorBindingException;
}