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

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DescMeta;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * A container in DIDL content.
 * <p>
 * Note that although this container can have sub-containers, the
 * {@link org.fourthline.cling.support.contentdirectory.DIDLParser}
 * will never read nor write this collection to and from XML.
 * Its only purpose is convenience when creating and manipulating a
 * recursive structure, that is, modelling the content tree as you
 * see fit. You can then pick a list of containers and/or a list of
 * items and hand them to the DIDL parser, which will render them
 * flat in XML. The only nested structure that can optionally be
 * rendered into and read from XML are the items of containers,
 * never their sub-containers.
 * </p>
 * <p>
 * Also see ContentDirectory 1.0 specification, section 2.8.3:
 * "Incremental navigation i.e. the full hierarchy is never returned
 * in one call since this is likely to flood the resources available to
 * the control point (memory, network bandwidth, etc.)."
 * </p>
 *
 * @author Christian Bauer
 */
public class Container extends DIDLObject {

    protected Integer childCount = null;
    protected boolean searchable; // Default or absent == false
    protected List<Class> createClasses = new ArrayList<>();
    protected List<Class> searchClasses = new ArrayList<>();
    protected List<Container> containers = new ArrayList<>();
    protected List<Item> items = new ArrayList<>();

    public Container() {
    }

    public Container(Container other) {
        super(other);
        setChildCount(other.getChildCount());
        setSearchable(other.isSearchable());
        setCreateClasses(other.getCreateClasses());
        setSearchClasses(other.getSearchClasses());
        setItems(other.getItems());
    }

    public Container(String id, String parentID, String title, String creator, boolean restricted, WriteStatus writeStatus, Class clazz, List<Res> resources, List<Property> properties, List<DescMeta> descMetadata) {
        super(id, parentID, title, creator, restricted, writeStatus, clazz, resources, properties, descMetadata);
    }

    public Container(String id, String parentID, String title, String creator, boolean restricted, WriteStatus writeStatus, Class clazz, List<Res> resources, List<Property> properties, List<DescMeta> descMetadata, Integer childCount, boolean searchable, List<Class> createClasses, List<Class> searchClasses, List<Item> items) {
        super(id, parentID, title, creator, restricted, writeStatus, clazz, resources, properties, descMetadata);
        this.childCount = childCount;
        this.searchable = searchable;
        this.createClasses = createClasses;
        this.searchClasses = searchClasses;
        this.items = items;
    }

    public Container(String id, Container parent, String title, String creator, DIDLObject.Class clazz, Integer childCount) {
        this(id, parent.getId(), title, creator, true, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), childCount, false, new ArrayList<Class>(), new ArrayList<Class>(), new ArrayList<Item>());
    }

    public Container(String id, String parentID, String title, String creator, DIDLObject.Class clazz, Integer childCount) {
        this(id, parentID, title, creator, true, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), childCount, false, new ArrayList<Class>(), new ArrayList<Class>(), new ArrayList<Item>());
    }

    public Container(String id, Container parent, String title, String creator, DIDLObject.Class clazz, Integer childCount, boolean searchable, List<Class> createClasses, List<Class> searchClasses, List<Item> items) {
        this(id, parent.getId(), title, creator, true, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), childCount, searchable, createClasses, searchClasses, items);
    }

    public Container(String id, String parentID, String title, String creator, DIDLObject.Class clazz, Integer childCount, boolean searchable, List<Class> createClasses, List<Class> searchClasses, List<Item> items) {
        this(id, parentID, title, creator, true, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), childCount, searchable, createClasses, searchClasses, items);
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public List<Class> getCreateClasses() {
        return createClasses;
    }

    public void setCreateClasses(List<Class> createClasses) {
        this.createClasses = createClasses;
    }

    public List<Class> getSearchClasses() {
        return searchClasses;
    }

    public void setSearchClasses(List<Class> searchClasses) {
        this.searchClasses = searchClasses;
    }

    public Container getFirstContainer() {
        return getContainers().get(0);
    }

    public Container addContainer(Container container) {
        getContainers().add(container);
        return this;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Container addItem(Item item) {
        getItems().add(item);
        return this;
    }
    
}
