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

package org.fourthline.cling.support.contentdirectory;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.model.DIDLAttribute;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DescMeta;
import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;
import org.seamless.util.io.IO;
import org.seamless.util.Exceptions;
import org.seamless.xml.SAXParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.fourthline.cling.model.XMLUtil.appendNewElement;
import static org.fourthline.cling.model.XMLUtil.appendNewElementIfNotNull;

/**
 * DIDL parser based on SAX for reading and DOM for writing.
 * <p>
 * This parser requires Android platform level 8 (2.2).
 * </p>
 * <p>
 * Override the {@link #createDescMetaHandler(org.fourthline.cling.support.model.DescMeta, org.seamless.xml.SAXParser.Handler)}
 * method to read vendor extension content of {@code <desc>} elements. You then should also override the
 * {@link #populateDescMetadata(org.w3c.dom.Element, org.fourthline.cling.support.model.DescMeta)} method for writing.
 * </p>
 * <p>
 * Override the {@link #createItemHandler(org.fourthline.cling.support.model.item.Item, org.seamless.xml.SAXParser.Handler)}
 * etc. methods to register custom handlers for vendor-specific elements and attributes within items, containers,
 * and so on.
 * </p>
 *
 * @author Christian Bauer
 * @author Mario Franco
 */
public class DIDLParser extends SAXParser {

    final private static Logger log = Logger.getLogger(DIDLParser.class.getName());

    public static final String UNKNOWN_TITLE = "Unknown Title";

    /**
     * Uses the current thread's context classloader to read and unmarshall the given resource.
     *
     * @param resource The resource on the classpath.
     * @return The unmarshalled DIDL content model.
     * @throws Exception
     */
    public DIDLContent parseResource(String resource) throws Exception {
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            return parse(IO.readLines(is));
        } finally {
            if (is != null) is.close();
        }
    }

    /**
     * Reads and unmarshalls an XML representation into a DIDL content model.
     *
     * @param xml The XML representation.
     * @return A DIDL content model.
     * @throws Exception
     */
    public DIDLContent parse(String xml) throws Exception {

        if (xml == null || xml.length() == 0) {
            throw new RuntimeException("Null or empty XML");
        }

        DIDLContent content = new DIDLContent();
        createRootHandler(content, this);

        log.fine("Parsing DIDL XML content");
        parse(new InputSource(new StringReader(xml)));
        return content;
    }

    protected RootHandler createRootHandler(DIDLContent instance, SAXParser parser) {
        return new RootHandler(instance, parser);
    }

    protected ContainerHandler createContainerHandler(Container instance, Handler parent) {
        return new ContainerHandler(instance, parent);
    }

    protected ItemHandler createItemHandler(Item instance, Handler parent) {
        return new ItemHandler(instance, parent);
    }

    protected ResHandler createResHandler(Res instance, Handler parent) {
        return new ResHandler(instance, parent);
    }

    protected DescMetaHandler createDescMetaHandler(DescMeta instance, Handler parent) {
        return new DescMetaHandler(instance, parent);
    }


    protected Container createContainer(Attributes attributes) {
        Container container = new Container();

        container.setId(attributes.getValue("id"));
        container.setParentID(attributes.getValue("parentID"));

        if ((attributes.getValue("childCount") != null))
            container.setChildCount(Integer.valueOf(attributes.getValue("childCount")));

        try {
            Boolean value = (Boolean) Datatype.Builtin.BOOLEAN.getDatatype().valueOf(
                attributes.getValue("restricted")
            );
            if (value != null)
                container.setRestricted(value);

            value = (Boolean) Datatype.Builtin.BOOLEAN.getDatatype().valueOf(
                attributes.getValue("searchable")
            );
            if (value != null)
                container.setSearchable(value);
        } catch (Exception ex) {
            // Ignore
        }

        return container;
    }

    protected Item createItem(Attributes attributes) {
        Item item = new Item();

        item.setId(attributes.getValue("id"));
        item.setParentID(attributes.getValue("parentID"));

        try {
            Boolean value = (Boolean)Datatype.Builtin.BOOLEAN.getDatatype().valueOf(
                    attributes.getValue("restricted")
            );
            if (value != null)
                item.setRestricted(value);

        } catch (Exception ex) {
            // Ignore
        }

        if ((attributes.getValue("refID") != null))
            item.setRefID(attributes.getValue("refID"));

        return item;
    }

    protected Res createResource(Attributes attributes) {
        Res res = new Res();

        if (attributes.getValue("importUri") != null)
            res.setImportUri(URI.create(attributes.getValue("importUri")));

        try {
            res.setProtocolInfo(
                    new ProtocolInfo(attributes.getValue("protocolInfo"))
            );
        } catch (InvalidValueException ex) {
            log.warning("In DIDL content, invalid resource protocol info: " + Exceptions.unwrap(ex));
            return null;
        }

        if (attributes.getValue("size") != null)
            res.setSize(toLongOrNull(attributes.getValue("size")));

        if (attributes.getValue("duration") != null)
            res.setDuration(attributes.getValue("duration"));

        if (attributes.getValue("bitrate") != null)
            res.setBitrate(toLongOrNull(attributes.getValue("bitrate")));

        if (attributes.getValue("sampleFrequency") != null)
            res.setSampleFrequency(toLongOrNull(attributes.getValue("sampleFrequency")));

        if (attributes.getValue("bitsPerSample") != null)
            res.setBitsPerSample(toLongOrNull(attributes.getValue("bitsPerSample")));

        if (attributes.getValue("nrAudioChannels") != null)
            res.setNrAudioChannels(toLongOrNull(attributes.getValue("nrAudioChannels")));

        if (attributes.getValue("colorDepth") != null)
            res.setColorDepth(toLongOrNull(attributes.getValue("colorDepth")));

        if (attributes.getValue("protection") != null)
            res.setProtection(attributes.getValue("protection"));

        if (attributes.getValue("resolution") != null)
            res.setResolution(attributes.getValue("resolution"));

        return res;
    }

    private Long toLongOrNull(String value) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException x) {
            return null;
        }
    }

    protected DescMeta createDescMeta(Attributes attributes) {
        DescMeta desc = new DescMeta();

        desc.setId(attributes.getValue("id"));

        if ((attributes.getValue("type") != null))
            desc.setType(attributes.getValue("type"));

        if ((attributes.getValue("nameSpace") != null))
            desc.setNameSpace(URI.create(attributes.getValue("nameSpace")));

        return desc;
    }


    /* ############################################################################################# */


    /**
     * Generates a XML representation of the content model.
     * <p>
     * Items inside a container will <em>not</em> be represented in the XML, the containers
     * will be rendered flat without children.
     * </p>
     *
     * @param content The content model.
     * @return An XML representation.
     * @throws Exception
     */
    public String generate(DIDLContent content) throws Exception {
        return generate(content, false);
    }

    /**
     * Generates an XML representation of the content model.
     * <p>
     * Optionally, items inside a container will be represented in the XML,
     * the container elements then have nested item elements. Although this
     * parser can read such a structure, it is unclear whether other DIDL
     * parsers should and actually do support this XML.
     * </p>
     *
     * @param content     The content model.
     * @param nestedItems <code>true</code> if nested item elements should be rendered for containers.
     * @return An XML representation.
     * @throws Exception
     */
    public String generate(DIDLContent content, boolean nestedItems) throws Exception {
        return documentToString(buildDOM(content, nestedItems), true);
    }

    // TODO: Yes, this only runs on Android 2.2

    protected String documentToString(Document document, boolean omitProlog) throws Exception {
        TransformerFactory transFactory = TransformerFactory.newInstance();

        // Indentation not supported on Android 2.2
        //transFactory.setAttribute("indent-number", 4);

        Transformer transformer = transFactory.newTransformer();

        if (omitProlog) {
            // TODO: UPNP VIOLATION: Terratec Noxon Webradio fails when DIDL content has a prolog
            // No XML prolog! This is allowed because it is UTF-8 encoded and required
            // because broken devices will stumble on SOAP messages that contain (even
            // encoded) XML prologs within a message body.
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }

        // Again, Android 2.2 fails hard if you try this.
        //transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter out = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(out));
        return out.toString();
    }

    protected Document buildDOM(DIDLContent content, boolean nestedItems) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document d = factory.newDocumentBuilder().newDocument();

        generateRoot(content, d, nestedItems);

        return d;
    }

    protected void generateRoot(DIDLContent content, Document descriptor, boolean nestedItems) {
        Element rootElement = descriptor.createElementNS(DIDLContent.NAMESPACE_URI, "DIDL-Lite");
        descriptor.appendChild(rootElement);

        // rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:didl", DIDLContent.NAMESPACE_URI);
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:upnp", DIDLObject.Property.UPNP.NAMESPACE.URI);
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dc", DIDLObject.Property.DC.NAMESPACE.URI);
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:sec", DIDLObject.Property.SEC.NAMESPACE.URI);

        for (Container container : content.getContainers()) {
            if (container == null) continue;
            generateContainer(container, descriptor, rootElement, nestedItems);
        }

        for (Item item : content.getItems()) {
            if (item == null) continue;
            generateItem(item, descriptor, rootElement);
        }

        for (DescMeta descMeta : content.getDescMetadata()) {
            if (descMeta == null) continue;
            generateDescMetadata(descMeta, descriptor, rootElement);
        }
    }

    protected void generateContainer(Container container, Document descriptor, Element parent, boolean nestedItems) {

        if (container.getClazz() == null) {
            throw new RuntimeException("Missing 'upnp:class' element for container: " + container.getId());
        }

        Element containerElement = appendNewElement(descriptor, parent, "container");

        if (container.getId() == null)
            throw new NullPointerException("Missing id on container: " + container);
        containerElement.setAttribute("id", container.getId());

        if (container.getParentID() == null)
            throw new NullPointerException("Missing parent id on container: " + container);
        containerElement.setAttribute("parentID", container.getParentID());

        if (container.getChildCount() != null) {
            containerElement.setAttribute("childCount", Integer.toString(container.getChildCount()));
        }

        containerElement.setAttribute("restricted", booleanToInt(container.isRestricted()));
        containerElement.setAttribute("searchable", booleanToInt(container.isSearchable()));

        String title = container.getTitle();
        if (title == null) {
            log.warning("Missing 'dc:title' element for container: " + container.getId());
            title = UNKNOWN_TITLE;
        }

        appendNewElementIfNotNull(
            descriptor,
            containerElement,
            "dc:title",
            title,
            DIDLObject.Property.DC.NAMESPACE.URI
        );

        appendNewElementIfNotNull(
            descriptor,
            containerElement,
            "dc:creator",
            container.getCreator(),
            DIDLObject.Property.DC.NAMESPACE.URI
        );

        appendNewElementIfNotNull(
            descriptor,
            containerElement,
            "upnp:writeStatus",
            container.getWriteStatus(),
            DIDLObject.Property.UPNP.NAMESPACE.URI
        );

        appendClass(descriptor, containerElement, container.getClazz(), "upnp:class", false);

        for (DIDLObject.Class searchClass : container.getSearchClasses()) {
            appendClass(descriptor, containerElement, searchClass, "upnp:searchClass", true);
        }

        for (DIDLObject.Class createClass : container.getCreateClasses()) {
            appendClass(descriptor, containerElement, createClass, "upnp:createClass", true);
        }

        appendProperties(descriptor, containerElement, container, "upnp", DIDLObject.Property.UPNP.NAMESPACE.class, DIDLObject.Property.UPNP.NAMESPACE.URI);
        appendProperties(descriptor, containerElement, container, "dc", DIDLObject.Property.DC.NAMESPACE.class, DIDLObject.Property.DC.NAMESPACE.URI);

        if (nestedItems) {
            for (Item item : container.getItems()) {
                if (item == null) continue;
                generateItem(item, descriptor, containerElement);
            }
        }

        for (Res resource : container.getResources()) {
            if (resource == null) continue;
            generateResource(resource, descriptor, containerElement);
        }

        for (DescMeta descMeta : container.getDescMetadata()) {
            if (descMeta == null) continue;
            generateDescMetadata(descMeta, descriptor, containerElement);
        }
    }

    protected void generateItem(Item item, Document descriptor, Element parent) {

        if (item.getClazz() == null) {
            throw new RuntimeException("Missing 'upnp:class' element for item: " + item.getId());
        }

        Element itemElement = appendNewElement(descriptor, parent, "item");

        if (item.getId() == null)
            throw new NullPointerException("Missing id on item: " + item);
        itemElement.setAttribute("id", item.getId());

        if (item.getParentID() == null)
            throw new NullPointerException("Missing parent id on item: " + item);
        itemElement.setAttribute("parentID", item.getParentID());

        if (item.getRefID() != null)
            itemElement.setAttribute("refID", item.getRefID());
        itemElement.setAttribute("restricted", booleanToInt(item.isRestricted()));

        String title = item.getTitle();
        if (title == null) {
            log.warning("Missing 'dc:title' element for item: " + item.getId());
            title = UNKNOWN_TITLE;
        }

        appendNewElementIfNotNull(
            descriptor,
            itemElement,
            "dc:title",
            title,
            DIDLObject.Property.DC.NAMESPACE.URI
        );

        appendNewElementIfNotNull(
            descriptor,
            itemElement,
            "dc:creator",
            item.getCreator(),
            DIDLObject.Property.DC.NAMESPACE.URI
        );

        appendNewElementIfNotNull(
            descriptor,
            itemElement,
            "upnp:writeStatus",
            item.getWriteStatus(),
            DIDLObject.Property.UPNP.NAMESPACE.URI
        );

        appendClass(descriptor, itemElement, item.getClazz(), "upnp:class", false);

        appendProperties(descriptor, itemElement, item, "upnp", DIDLObject.Property.UPNP.NAMESPACE.class, DIDLObject.Property.UPNP.NAMESPACE.URI);
        appendProperties(descriptor, itemElement, item, "dc", DIDLObject.Property.DC.NAMESPACE.class, DIDLObject.Property.DC.NAMESPACE.URI);
        appendProperties(descriptor, itemElement, item, "sec", DIDLObject.Property.SEC.NAMESPACE.class, DIDLObject.Property.SEC.NAMESPACE.URI);

        for (Res resource : item.getResources()) {
            if (resource == null) continue;
            generateResource(resource, descriptor, itemElement);
        }

        for (DescMeta descMeta : item.getDescMetadata()) {
            if (descMeta == null) continue;
            generateDescMetadata(descMeta, descriptor, itemElement);
        }
    }

    protected void generateResource(Res resource, Document descriptor, Element parent) {

        if (resource.getValue() == null) {
            throw new RuntimeException("Missing resource URI value" + resource);
        }
        if (resource.getProtocolInfo() == null) {
            throw new RuntimeException("Missing resource protocol info: " + resource);
        }

        Element resourceElement = appendNewElement(descriptor, parent, "res", resource.getValue());
        resourceElement.setAttribute("protocolInfo", resource.getProtocolInfo().toString());
        if (resource.getImportUri() != null)
            resourceElement.setAttribute("importUri", resource.getImportUri().toString());
        if (resource.getSize() != null)
            resourceElement.setAttribute("size", resource.getSize().toString());
        if (resource.getDuration() != null)
            resourceElement.setAttribute("duration", resource.getDuration());
        if (resource.getBitrate() != null)
            resourceElement.setAttribute("bitrate", resource.getBitrate().toString());
        if (resource.getSampleFrequency() != null)
            resourceElement.setAttribute("sampleFrequency", resource.getSampleFrequency().toString());
        if (resource.getBitsPerSample() != null)
            resourceElement.setAttribute("bitsPerSample", resource.getBitsPerSample().toString());
        if (resource.getNrAudioChannels() != null)
            resourceElement.setAttribute("nrAudioChannels", resource.getNrAudioChannels().toString());
        if (resource.getColorDepth() != null)
            resourceElement.setAttribute("colorDepth", resource.getColorDepth().toString());
        if (resource.getProtection() != null)
            resourceElement.setAttribute("protection", resource.getProtection());
        if (resource.getResolution() != null)
            resourceElement.setAttribute("resolution", resource.getResolution());
    }

    protected void generateDescMetadata(DescMeta descMeta, Document descriptor, Element parent) {

        if (descMeta.getId() == null) {
            throw new RuntimeException("Missing id of description metadata: " + descMeta);
        }
        if (descMeta.getNameSpace() == null) {
            throw new RuntimeException("Missing namespace of description metadata: " + descMeta);
        }

        Element descElement = appendNewElement(descriptor, parent, "desc");
        descElement.setAttribute("id", descMeta.getId());
        descElement.setAttribute("nameSpace", descMeta.getNameSpace().toString());
        if (descMeta.getType() != null)
            descElement.setAttribute("type", descMeta.getType());
        populateDescMetadata(descElement, descMeta);
    }

    /**
     * Expects an <code>org.w3c.Document</code> as metadata, copies nodes of the document into the DIDL content.
     * <p>
     * This method will ignore the content and log a warning if it's of the wrong type. If you override
     * {@link #createDescMetaHandler(org.fourthline.cling.support.model.DescMeta, org.seamless.xml.SAXParser.Handler)},
     * you most likely also want to override this method.
     * </p>
     *
     * @param descElement The DIDL content {@code <desc>} element wrapping the final metadata.
     * @param descMeta    The metadata with a <code>org.w3c.Document</code> payload.
     */
    protected void populateDescMetadata(Element descElement, DescMeta descMeta) {
        if (descMeta.getMetadata() instanceof Document) {
            Document doc = (Document) descMeta.getMetadata();

            NodeList nl = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Node clone = descElement.getOwnerDocument().importNode(n, true);
                descElement.appendChild(clone);
            }

        } else {
            log.warning("Unknown desc metadata content, please override populateDescMetadata(): " + descMeta.getMetadata());
        }
    }

    protected void appendProperties(Document descriptor, Element parent, DIDLObject object, String prefix,
                                    Class<? extends DIDLObject.Property.NAMESPACE> namespace,
                                    String namespaceURI) {
        for (DIDLObject.Property<Object> property : object.getPropertiesByNamespace(namespace)) {
            Element el = descriptor.createElementNS(namespaceURI, prefix + ":" + property.getDescriptorName());
            parent.appendChild(el);
            property.setOnElement(el);
        }
    }

    protected void appendClass(Document descriptor, Element parent, DIDLObject.Class clazz, String element, boolean appendDerivation) {
        Element classElement = appendNewElementIfNotNull(
            descriptor,
            parent,
            element,
            clazz.getValue(),
            DIDLObject.Property.UPNP.NAMESPACE.URI
        );
        if (clazz.getFriendlyName() != null && clazz.getFriendlyName().length() > 0)
            classElement.setAttribute("name", clazz.getFriendlyName());
        if (appendDerivation)
            classElement.setAttribute("includeDerived", Boolean.toString(clazz.isIncludeDerived()));
    }

    protected String booleanToInt(boolean b) {
        return b ? "1" : "0";
    }

    /**
     * Sends the given string to the log with <code>Level.FINE</code>, if that log level is enabled.
     *
     * @param s The string to send to the log.
     */
    public void debugXML(String s) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("-------------------------------------------------------------------------------------");
            log.fine("\n" + s);
            log.fine("-------------------------------------------------------------------------------------");
        }
    }


    /* ############################################################################################# */


    public abstract class DIDLObjectHandler<I extends DIDLObject> extends Handler<I> {

        protected DIDLObjectHandler(I instance, Handler parent) {
            super(instance, parent);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if (DIDLObject.Property.DC.NAMESPACE.URI.equals(uri)) {

                if ("title".equals(localName)) {
                    getInstance().setTitle(getCharacters());
                } else if ("creator".equals(localName)) {
                    getInstance().setCreator(getCharacters());
                } else if ("description".equals(localName)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.DESCRIPTION(getCharacters()));
                } else if ("publisher".equals(localName)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.PUBLISHER(new Person(getCharacters())));
                } else if ("contributor".equals(localName)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.CONTRIBUTOR(new Person(getCharacters())));
                } else if ("date".equals(localName)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.DATE(getCharacters()));
                } else if ("language".equals(localName)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.LANGUAGE(getCharacters()));
                } else if ("rights".equals(localName)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.RIGHTS(getCharacters()));
                } else if ("relation".equals(localName)) {
                    getInstance().addProperty(new DIDLObject.Property.DC.RELATION(URI.create(getCharacters())));
                }

            } else if (DIDLObject.Property.UPNP.NAMESPACE.URI.equals(uri)) {

                if ("writeStatus".equals(localName)) {
                    try {
                        getInstance().setWriteStatus(
                            WriteStatus.valueOf(getCharacters())
                        );
                    } catch (Exception ex) {
                        log.info("Ignoring invalid writeStatus value: " + getCharacters());
                    }
                } else if ("class".equals(localName)) {
                    getInstance().setClazz(
                        new DIDLObject.Class(
                            getCharacters(),
                            getAttributes().getValue("name")
                        )
                    );
                } else if ("artist".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.ARTIST(
                            new PersonWithRole(getCharacters(), getAttributes().getValue("role"))
                        )
                    );
                } else if ("actor".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.ACTOR(
                            new PersonWithRole(getCharacters(), getAttributes().getValue("role"))
                        )
                    );
                } else if ("author".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.AUTHOR(
                            new PersonWithRole(getCharacters(), getAttributes().getValue("role"))
                        )
                    );
                } else if ("producer".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.PRODUCER(new Person(getCharacters()))
                    );
                } else if ("director".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.DIRECTOR(new Person(getCharacters()))
                    );
                } else if ("longDescription".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.LONG_DESCRIPTION(getCharacters())
                    );
                } else if ("storageUsed".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.STORAGE_USED(Long.valueOf(getCharacters()))
                    );
                } else if ("storageTotal".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.STORAGE_TOTAL(Long.valueOf(getCharacters()))
                    );
                } else if ("storageFree".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.STORAGE_FREE(Long.valueOf(getCharacters()))
                    );
                } else if ("storageMaxPartition".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.STORAGE_MAX_PARTITION(Long.valueOf(getCharacters()))
                    );
                } else if ("storageMedium".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.STORAGE_MEDIUM(StorageMedium.valueOrVendorSpecificOf(getCharacters()))
                    );
                } else if ("genre".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.GENRE(getCharacters())
                    );
                } else if ("album".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.ALBUM(getCharacters())
                    );
                } else if ("playlist".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.PLAYLIST(getCharacters())
                    );
                } else if ("region".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.REGION(getCharacters())
                    );
                } else if ("rating".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.RATING(getCharacters())
                    );
                } else if ("toc".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.TOC(getCharacters())
                    );
                } else if ("albumArtURI".equals(localName)) {
                    DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(URI.create(getCharacters()));

                    Attributes albumArtURIAttributes = getAttributes();
                    for (int i = 0; i < albumArtURIAttributes.getLength(); i++) {
                        if ("profileID".equals(albumArtURIAttributes.getLocalName(i))) {
                            albumArtURI.addAttribute(
                                new DIDLObject.Property.DLNA.PROFILE_ID(
                                    new DIDLAttribute(
                                        DIDLObject.Property.DLNA.NAMESPACE.URI,
                                        "dlna",
                                        albumArtURIAttributes.getValue(i))
                                ));
                        }
                    }

                    getInstance().addProperty(albumArtURI);
                } else if ("artistDiscographyURI".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.ARTIST_DISCO_URI(URI.create(getCharacters()))
                    );
                } else if ("lyricsURI".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.LYRICS_URI(URI.create(getCharacters()))
                    );
                } else if ("icon".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.ICON(URI.create(getCharacters()))
                    );
                } else if ("radioCallSign".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.RADIO_CALL_SIGN(getCharacters())
                    );
                } else if ("radioStationID".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.RADIO_STATION_ID(getCharacters())
                    );
                } else if ("radioBand".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.RADIO_BAND(getCharacters())
                    );
                } else if ("channelNr".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.CHANNEL_NR(Integer.valueOf(getCharacters()))
                    );
                } else if ("channelName".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.CHANNEL_NAME(getCharacters())
                    );
                } else if ("scheduledStartTime".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.SCHEDULED_START_TIME(getCharacters())
                    );
                } else if ("scheduledEndTime".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.SCHEDULED_END_TIME(getCharacters())
                    );
                } else if ("DVDRegionCode".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.DVD_REGION_CODE(Integer.valueOf(getCharacters()))
                    );
                } else if ("originalTrackNumber".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.ORIGINAL_TRACK_NUMBER(Integer.valueOf(getCharacters()))
                    );
                } else if ("userAnnotation".equals(localName)) {
                    getInstance().addProperty(
                        new DIDLObject.Property.UPNP.USER_ANNOTATION(getCharacters())
                    );
                }
            }
        }
    }

    public class RootHandler extends Handler<DIDLContent> {

        RootHandler(DIDLContent instance, SAXParser parser) {
            super(instance, parser);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if (!DIDLContent.NAMESPACE_URI.equals(uri)) return;

            if (localName.equals("container")) {

                Container container = createContainer(attributes);
                getInstance().addContainer(container);
                createContainerHandler(container, this);

            } else if (localName.equals("item")) {

                Item item = createItem(attributes);
                getInstance().addItem(item);
                createItemHandler(item, this);

            } else if (localName.equals("desc")) {

                DescMeta desc = createDescMeta(attributes);
                getInstance().addDescMetadata(desc);
                createDescMetaHandler(desc, this);

            }
        }

        @Override
        protected boolean isLastElement(String uri, String localName, String qName) {
            if (DIDLContent.NAMESPACE_URI.equals(uri) && "DIDL-Lite".equals(localName)) {

                // Now transform all the generically typed Container and Item instances into
                // more specific Album, MusicTrack, etc. instances
                getInstance().replaceGenericContainerAndItems();

                return true;
            }
            return false;
        }
    }

    public class ContainerHandler extends DIDLObjectHandler<Container> {
        public ContainerHandler(Container instance, Handler parent) {
            super(instance, parent);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if (!DIDLContent.NAMESPACE_URI.equals(uri)) return;

            if (localName.equals("item")) {

                Item item = createItem(attributes);
                getInstance().addItem(item);
                createItemHandler(item, this);

            } else if (localName.equals("desc")) {

                DescMeta desc = createDescMeta(attributes);
                getInstance().addDescMetadata(desc);
                createDescMetaHandler(desc, this);

            } else if (localName.equals("res")) {

                Res res = createResource(attributes);
                if (res != null) {
                    getInstance().addResource(res);
                    createResHandler(res, this);
                }

            }

            // We do NOT support recursive container embedded in container! The schema allows it
            // but the spec doesn't:
            //
            // Section 2.8.3: Incremental navigation i.e. the full hierarchy is never returned
            // in one call since this is likely to flood the resources available to the control
            // point (memory, network bandwidth, etc.).
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if (DIDLObject.Property.UPNP.NAMESPACE.URI.equals(uri)) {

                if ("searchClass".equals(localName)) {
                    getInstance().getSearchClasses().add(
                        new DIDLObject.Class(
                            getCharacters(),
                            getAttributes().getValue("name"),
                            "true".equals(getAttributes().getValue("includeDerived"))
                        )
                    );
                } else if ("createClass".equals(localName)) {
                    getInstance().getCreateClasses().add(
                        new DIDLObject.Class(
                            getCharacters(),
                            getAttributes().getValue("name"),
                            "true".equals(getAttributes().getValue("includeDerived"))
                        )
                    );
                }
            }
        }

        @Override
        protected boolean isLastElement(String uri, String localName, String qName) {
            if (DIDLContent.NAMESPACE_URI.equals(uri) && "container".equals(localName)) {
                if (getInstance().getTitle() == null) {
                    log.warning("In DIDL content, missing 'dc:title' element for container: " + getInstance().getId());
                }
                if (getInstance().getClazz() == null) {
                    log.warning("In DIDL content, missing 'upnp:class' element for container: " + getInstance().getId());
                }
                return true;
            }
            return false;
        }
    }

    public class ItemHandler extends DIDLObjectHandler<Item> {
        public ItemHandler(Item instance, Handler parent) {
            super(instance, parent);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if (!DIDLContent.NAMESPACE_URI.equals(uri)) return;

            if (localName.equals("res")) {

                Res res = createResource(attributes);
                if (res != null) {
                    getInstance().addResource(res);
                    createResHandler(res, this);
                }

            } else if (localName.equals("desc")) {

                DescMeta desc = createDescMeta(attributes);
                getInstance().addDescMetadata(desc);
                createDescMetaHandler(desc, this);

            }
        }

        @Override
        protected boolean isLastElement(String uri, String localName, String qName) {
            if (DIDLContent.NAMESPACE_URI.equals(uri) && "item".equals(localName)) {
                if (getInstance().getTitle() == null) {
                    log.warning("In DIDL content, missing 'dc:title' element for item: " + getInstance().getId());
                }
                if (getInstance().getClazz() == null) {
                    log.warning("In DIDL content, missing 'upnp:class' element for item: " + getInstance().getId());
                }
                return true;
            }
            return false;
        }
    }

    protected class ResHandler extends Handler<Res> {
        public ResHandler(Res instance, Handler parent) {
            super(instance, parent);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            getInstance().setValue(getCharacters());
        }

        @Override
        protected boolean isLastElement(String uri, String localName, String qName) {
            return DIDLContent.NAMESPACE_URI.equals(uri) && "res".equals(localName);
        }
    }

    /**
     * Extracts an <code>org.w3c.Document</code> from the nested elements in the {@code <desc>} element.
     * <p>
     * The root element of this document is a wrapper in the namespace
     * {@link org.fourthline.cling.support.model.DIDLContent#DESC_WRAPPER_NAMESPACE_URI}.
     * </p>
     */
    public class DescMetaHandler extends Handler<DescMeta> {

        protected Element current;

        public DescMetaHandler(DescMeta instance, Handler parent) {
            super(instance, parent);
            instance.setMetadata(instance.createMetadataDocument());
            current = getInstance().getMetadata().getDocumentElement();
        }

        @Override
        public DescMeta<Document> getInstance() {
            return super.getInstance();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            Element newEl = getInstance().getMetadata().createElementNS(uri, qName);
            for (int i = 0; i < attributes.getLength(); i++) {
                newEl.setAttributeNS(
                    attributes.getURI(i),
                    attributes.getQName(i),
                    attributes.getValue(i)
                );
            }
            current.appendChild(newEl);
            current = newEl;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (isLastElement(uri, localName, qName)) return;

            // Ignore whitespace
            if (getCharacters().length() > 0 && !getCharacters().matches("[\\t\\n\\x0B\\f\\r\\s]+"))
                current.appendChild(getInstance().getMetadata().createTextNode(getCharacters()));

            current = (Element) current.getParentNode();

            // Reset this so we can continue parsing child nodes with this handler
            characters = new StringBuilder();
            attributes = null;
        }

        @Override
        protected boolean isLastElement(String uri, String localName, String qName) {
            return DIDLContent.NAMESPACE_URI.equals(uri) && "desc".equals(localName);
        }
    }
}
