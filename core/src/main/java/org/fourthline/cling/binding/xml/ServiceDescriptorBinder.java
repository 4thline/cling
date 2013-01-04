/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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