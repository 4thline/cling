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
package example.mediaserver;

import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DIDLObject.Property;
import org.fourthline.cling.support.model.DescMeta;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.container.Album;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.container.MusicAlbum;
import org.fourthline.cling.support.model.container.PhotoAlbum;
import org.fourthline.cling.support.model.item.AudioBook;
import org.fourthline.cling.support.model.item.AudioBroadcast;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.Movie;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.MusicVideoClip;
import org.fourthline.cling.support.model.item.Photo;
import org.fourthline.cling.support.model.item.PlaylistItem;
import org.fourthline.cling.support.model.item.TextItem;
import org.fourthline.cling.support.model.item.VideoBroadcast;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

public class DIDLParserTest {

    protected DIDLParser parser = new DIDLParser();

    @Test
    public void readWriteEmpty() throws Exception {

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseEmpty.xml");
        assertSampleEmpty(didl);
        String xml = parser.generate(didl);
        didl = parser.parse(xml);
        assertSampleEmpty(didl);

        // Again with an empty DIDLContent instance
        didl = new DIDLContent();
        assertSampleEmpty(didl);
        xml = parser.generate(didl);
        //parser.debugXML(xml);
        didl = parser.parse(xml);
        assertSampleEmpty(didl);
    }

    @Test
    public void readWriteRoot() throws Exception {

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseRoot.xml");
        assertSampleRoot(didl);

        String xml = parser.generate(didl);

        //parser.debugXML(xml);

        didl = parser.parse(xml);
        assertSampleRoot(didl);
    }

    @Test
    public void readWriteRootChildren() throws Exception {

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseRootChildren.xml");
        assertSampleRootChildren(didl);

        String xml = parser.generate(didl);

        //parser.debugXML(xml);

        didl = parser.parse(xml);
        assertSampleRootChildren(didl);
    }

    @Test
    public void readWriteFolder() throws Exception {

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseFolder.xml");
        assertSampleFolder(didl);

        String xml = parser.generate(didl);

        //parser.debugXML(xml);

        didl = parser.parse(xml);
        assertSampleFolder(didl);
    }

    @Test
    public void readWriteItems() throws Exception {

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseItems.xml");
        assertSampleItems(didl);

        String xml = parser.generate(didl);

        parser.debugXML(xml);

        didl = parser.parse(xml);
        assertSampleItems(didl);
    }

    @Test
    public void readWriteItemsMinimal() throws Exception {

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseItemsMinimal.xml");
        assertEquals(didl.getContainers().size(), 0);

        List<Item> items = didl.getItems();
        assertEquals(items.size(), 1);
        Item itemOne = items.get(0);
        assertEquals(itemOne.getId(), "1");
        assertEquals(itemOne.getParentID(), "0");
        assertFalse(!itemOne.isRestricted());
        assertEquals(itemOne.getTitle(), "Chloe Dancer");
        assertNull(itemOne.getCreator());
        assertEquals(itemOne.getClazz().getValue(), "object.item.audioItem.musicTrack");
        Res resource = itemOne.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/x-ms-wma:*");
        assertNull(resource.getSize());
        assertEquals(resource.getValue(), "http://10.0.0.1/somecontent.wma");
        assertNull(resource.getDuration());
        assertNull(resource.getBitrate());
        assertNull(resource.getSampleFrequency());
        assertNull(resource.getBitsPerSample());
        assertNull(resource.getNrAudioChannels());
        assertEquals(resource.getResolutionX(), 0);
        assertEquals(resource.getResolutionY(), 0);
        assertNull(resource.getColorDepth());
        assertNull(resource.getProtection());
        assertNull(resource.getImportUri());

        String xml = parser.generate(didl);

        parser.debugXML(xml);

        didl = parser.parse(xml);

        items = didl.getItems();
        assertEquals(items.size(), 1);
        itemOne = items.get(0);
        assertEquals(itemOne.getId(), "1");
        assertEquals(itemOne.getParentID(), "0");
        assertFalse(!itemOne.isRestricted());
        assertEquals(itemOne.getTitle(), "Chloe Dancer");
        assertNull(itemOne.getCreator());
        assertEquals(itemOne.getClazz().getValue(), "object.item.audioItem.musicTrack");
        resource = itemOne.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/x-ms-wma:*");
        assertNull(resource.getSize());
        assertEquals(resource.getValue(), "http://10.0.0.1/somecontent.wma");
        assertNull(resource.getDuration());
        assertNull(resource.getBitrate());
        assertNull(resource.getSampleFrequency());
        assertNull(resource.getBitsPerSample());
        assertNull(resource.getNrAudioChannels());
        assertEquals(resource.getResolutionX(), 0);
        assertEquals(resource.getResolutionY(), 0);
        assertNull(resource.getColorDepth());
        assertNull(resource.getProtection());
        assertNull(resource.getImportUri());
    }

    @Test
    public void readWriteMixed() throws Exception {

        final boolean[] tests = new boolean[1];

        // This is how you extend the parser for reading vendor-specific elements within root, container, or item scope
        DIDLParser parser = new DIDLParser() {
            @Override
            protected DIDLParser.ItemHandler createItemHandler(Item instance, Handler parent) {
                return new ItemHandler(instance, parent) {
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                        super.startElement(uri, localName, qName, attributes);
                        if ("urn:my-vendor-extension".equals(uri) && localName.equals("foo")) {
                            tests[0] = true;
                        }
                    }
                };
            }
        };

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseMixed.xml");
        assertSampleMixed(didl);
        assert tests[0];

        String xml = parser.generate(didl);

        //parser.debugXML(xml);

        didl = parser.parse(xml);
        assertSampleMixed(didl);
    }

    @Test
    public void readWriteNested() throws Exception {

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseNested.xml");
        assertSampleNested(didl);

        String xml = parser.generate(didl, true); // Special switch for nesting of items inside containers

        //parser.debugXML(xml);

        didl = parser.parse(xml);
        assertSampleNested(didl);
    }

    @Test
    public void readWriteItemsMixed() throws Exception {

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseItemsMixed.xml");
        assertSampleItemsMixed(didl);

        String xml = parser.generate(didl);

        //parser.debugXML(xml);

        didl = parser.parse(xml);
        assertSampleItemsMixed(didl);
    }

    @Test
    public void readWriteFoldersMixed() throws Exception {

        DIDLContent didl = parser.parseResource("org/fourthline/cling/test/support/contentdirectory/samples/browseFoldersMixed.xml");
        assertSampleFoldersMixed(didl);

        String xml = parser.generate(didl, true);

        // parser.debugXML(xml);

        didl = parser.parse(xml);
        assertSampleFoldersMixed(didl);
    }

    protected void assertSampleEmpty(DIDLContent didl) {
        assertEquals(didl.getContainers().size(), 0);
        assertEquals(didl.getDescMetadata().size(), 0);
        assertEquals(didl.getItems().size(), 0);
    }

    protected void assertSampleRoot(DIDLContent didl) {

        List<Container> containers = didl.getContainers();
        assertEquals(containers.size(), 1);

        Container rootContainer = didl.getFirstContainer();

        assertEquals(rootContainer.getId(), "0");
        assertEquals(rootContainer.getParentID(), "-1");
        assertEquals(rootContainer.isRestricted(), true);
        assertEquals(rootContainer.isSearchable(), true);
        assertEquals(rootContainer.getTitle(), "My multimedia stuff");

        assertEquals(rootContainer.getCreator(), "John Doe");

        String longDescription = rootContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.LONG_DESCRIPTION.class);
        assertEquals(longDescription, "This is a long description!");

        assertEquals(rootContainer.getClazz().getFriendlyName(), "Folder");
        assertEquals(rootContainer.getClazz().getValue(), "object.container.storageFolder");

        assertEquals(rootContainer.getSearchClasses().size(), 5);
        assertEquals(rootContainer.getSearchClasses().get(0).isIncludeDerived(), false);
        assertEquals(rootContainer.getSearchClasses().get(0).getValue(), "object.container.album.musicAlbum");
        assertEquals(rootContainer.getSearchClasses().get(4).getValue(), "object.item.imageItem.photo.vendorAlbumArt");
        assertEquals(rootContainer.getSearchClasses().get(4).getFriendlyName(), "Vendor Album Art");

        assertEquals(rootContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_USED.class), new Long(907000));

        assertEquals(rootContainer.getWriteStatus(), WriteStatus.WRITABLE);
    }

    protected void assertSampleRootChildren(DIDLContent didl) {

        List<Container> containers = didl.getContainers();
        assertEquals(containers.size(), 3);

        Container myMusicContainer = didl.getContainers().get(0);
        assertEquals(myMusicContainer.getId(), "1");
        assertEquals(myMusicContainer.getParentID(), "0");
        assertEquals(myMusicContainer.getChildCount(), new Integer(2));
        assertEquals(myMusicContainer.isRestricted(), false);
        assertEquals(myMusicContainer.isSearchable(), false);
        assertEquals(myMusicContainer.getTitle(), "My Music");
        assertEquals(myMusicContainer.getClazz().getValue(), "object.container.storageFolder");

        assertEquals(myMusicContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_USED.class), new Long(730000));
        assertEquals(myMusicContainer.getWriteStatus(), WriteStatus.WRITABLE);

        assertEquals(myMusicContainer.getSearchClasses().size(), 2);
        assertEquals(myMusicContainer.getSearchClasses().get(0).isIncludeDerived(), false);
        assertEquals(myMusicContainer.getSearchClasses().get(0).getValue(), "object.container.album.musicAlbum");
        assertEquals(myMusicContainer.getSearchClasses().get(1).isIncludeDerived(), false);
        assertEquals(myMusicContainer.getSearchClasses().get(1).getValue(), "object.item.audioItem.musicTrack");

        assertEquals(myMusicContainer.getCreateClasses().size(), 1);
        assertEquals(myMusicContainer.getCreateClasses().get(0).isIncludeDerived(), false);
        assertEquals(myMusicContainer.getCreateClasses().get(0).getValue(), "object.container.album.musicAlbum");

        Container myPhotosContainer = didl.getContainers().get(1);
        assertEquals(myPhotosContainer.getId(), "2");
        assertEquals(myPhotosContainer.getParentID(), "0");
        assertEquals(myPhotosContainer.getChildCount(), new Integer(2));
        assertEquals(myPhotosContainer.isRestricted(), false);
        assertEquals(myPhotosContainer.isSearchable(), false);
        assertEquals(myPhotosContainer.getTitle(), "My Photos");
        assertEquals(myPhotosContainer.getClazz().getValue(), "object.container.storageFolder");

        assertEquals(myPhotosContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_USED.class), new Long(177000));
        assertEquals(myPhotosContainer.getWriteStatus(), WriteStatus.WRITABLE);

        assertEquals(myPhotosContainer.getSearchClasses().size(), 2);
        assertEquals(myPhotosContainer.getSearchClasses().get(0).isIncludeDerived(), false);
        assertEquals(myPhotosContainer.getSearchClasses().get(0).getValue(), "object.container.album.photoAlbum");
        assertEquals(myPhotosContainer.getSearchClasses().get(1).isIncludeDerived(), false);
        assertEquals(myPhotosContainer.getSearchClasses().get(1).getValue(), "object.item.imageItem.photo");

        assertEquals(myPhotosContainer.getCreateClasses().size(), 1);
        assertEquals(myPhotosContainer.getCreateClasses().get(0).isIncludeDerived(), false);
        assertEquals(myPhotosContainer.getCreateClasses().get(0).getValue(), "object.container.album.photoAlbum");

        Container albumArtContainer = didl.getContainers().get(2);
        assertEquals(albumArtContainer.getId(), "3");
        assertEquals(albumArtContainer.getParentID(), "0");
        assertEquals(albumArtContainer.getChildCount(), new Integer(2));
        assertEquals(albumArtContainer.isRestricted(), false);
        assertEquals(albumArtContainer.isSearchable(), false);
        assertEquals(albumArtContainer.getTitle(), "Album Art");
        assertEquals(albumArtContainer.getClazz().getValue(), "object.container.storageFolder");

        assertEquals(albumArtContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_USED.class), new Long(40000));
        assertEquals(albumArtContainer.getWriteStatus(), WriteStatus.WRITABLE);

        assertEquals(albumArtContainer.getSearchClasses().size(), 1);
        assertEquals(albumArtContainer.getSearchClasses().get(0).isIncludeDerived(), true);
        assertEquals(albumArtContainer.getSearchClasses().get(0).getValue(), "object.item.imageItem.photo.vendorAlbumArt");

        assertEquals(albumArtContainer.getCreateClasses().size(), 1);
        assertEquals(albumArtContainer.getCreateClasses().get(0).isIncludeDerived(), true);
        assertEquals(albumArtContainer.getCreateClasses().get(0).getValue(), "object.item.imageItem.photo.vendorAlbumArt");
    }

    protected void assertSampleFolder(DIDLContent didl) {

        List<Container> containers = didl.getContainers();
        assertEquals(containers.size(), 2);

        Container brandNewDayContainer = didl.getContainers().get(0);

        assertEquals(brandNewDayContainer.getId(), "4");
        assertEquals(brandNewDayContainer.getParentID(), "1");
        assertEquals(brandNewDayContainer.getChildCount(), new Integer(3));
        assertEquals(brandNewDayContainer.isRestricted(), false);
        assertEquals(brandNewDayContainer.isSearchable(), false);
        assertEquals(brandNewDayContainer.getTitle(), "Brand New Day");
        assertEquals(brandNewDayContainer.getClazz().getValue(), "object.container.album.musicAlbum");

        assertEquals(brandNewDayContainer.getSearchClasses().size(), 1);
        assertEquals(brandNewDayContainer.getSearchClasses().get(0).isIncludeDerived(), false);
        assertEquals(brandNewDayContainer.getSearchClasses().get(0).getValue(), "object.item.audioItem.musicTrack");

        Container singlesSoundtrackContainer = didl.getContainers().get(1);
        assertEquals(singlesSoundtrackContainer.getId(), "5");
        assertEquals(singlesSoundtrackContainer.getParentID(), "1");
        assertEquals(singlesSoundtrackContainer.getChildCount(), new Integer(4));
        assertEquals(singlesSoundtrackContainer.isRestricted(), false);
        assertEquals(singlesSoundtrackContainer.isSearchable(), false);
        assertEquals(singlesSoundtrackContainer.getTitle(), "Singles Soundtrack");
        assertEquals(singlesSoundtrackContainer.getClazz().getValue(), "object.container.album.musicAlbum");

        assertEquals(singlesSoundtrackContainer.getSearchClasses().size(), 1);
        assertEquals(singlesSoundtrackContainer.getSearchClasses().get(0).isIncludeDerived(), false);
        assertEquals(singlesSoundtrackContainer.getSearchClasses().get(0).getValue(), "object.item.audioItem.musicTrack");

        assertEquals(singlesSoundtrackContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_USED.class), new Long(1234));
        assertEquals(singlesSoundtrackContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_FREE.class), new Long(12345));
        assertEquals(singlesSoundtrackContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_TOTAL.class), new Long(123456));
        assertEquals(singlesSoundtrackContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_MAX_PARTITION.class), new Long(1234567));
        assertEquals(singlesSoundtrackContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_MEDIUM.class), StorageMedium.UNKNOWN);

        assertEquals(singlesSoundtrackContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM_ART_URI.class).toString(), "http://some/album/art");
        assertEquals(singlesSoundtrackContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.ARTIST_DISCO_URI.class).toString(), "http://some/disco");
        assertEquals(singlesSoundtrackContainer.getFirstPropertyValue(DIDLObject.Property.UPNP.LYRICS_URI.class).toString(), "http://some/lyrics");
        
        Property<URI> albumArtURI = singlesSoundtrackContainer.getFirstProperty(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
        assertEquals(albumArtURI.getAttribute("profileID").getValue().getValue(), "JPEG_TN");
    }

    protected void assertSampleItems(DIDLContent didl) {

        assertEquals(didl.getContainers().size(), 0);

        List<Item> items = didl.getItems();
        assertEquals(items.size(), 4);

        Item itemOne = items.get(0);
        assertEquals(itemOne.getId(), "6");
        assertEquals(itemOne.getParentID(), "5");
        assert !itemOne.isRestricted();
        assertEquals(itemOne.getTitle(), "Chloe Dancer");
        assertEquals(itemOne.getCreator(), "Mother Love Bone");
        assertEquals(itemOne.getClazz().getValue(), "object.item.audioItem.musicTrack");
        Res resource = itemOne.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/x-ms-wma:*");
        assertEquals(resource.getSize(), new Long(200000));
        assertEquals(resource.getValue(), "http://10.0.0.1/getcontent.asp?id=6&foo=bar");
        assertEquals(resource.getDuration(), "00:03:25");
        assertEquals(resource.getBitrate(), new Long(8192));
        assertEquals(resource.getSampleFrequency(), new Long(44100));
        assertEquals(resource.getBitsPerSample(), new Long(16));
        assertEquals(resource.getNrAudioChannels(), new Long(2));
        assertEquals(resource.getResolutionX(), 120);
        assertEquals(resource.getResolutionY(), 130);
        assertEquals(resource.getColorDepth(), new Long(8));
        assertEquals(resource.getProtection(), "None");
        assertEquals(resource.getImportUri(), URI.create("http://10.0.0.1/import"));

        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.DC.DESCRIPTION.class), "Some Description");
        assertEquals(itemOne.getPropertyValues(DIDLObject.Property.DC.PUBLISHER.class).size(), 2);
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.DC.CONTRIBUTOR.class).getName(), "Some Contributor");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.DC.DATE.class), "2010-12-24");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.DC.LANGUAGE.class), "en-US");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.DC.RELATION.class).toString(), "http://some/related/resource");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.DC.RIGHTS.class), "CC SA-BY");

        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.GENRE.class), "Pop");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM.class), "Singles Soundtrack");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.ARTIST.class).getName(), "Mother Love Bone");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.ARTIST.class).getRole(), "performer");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.ACTOR.class).getName(), "Some Actor");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.ACTOR.class).getRole(), "myrole");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.AUTHOR.class).getName(), "Some Author");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.AUTHOR.class).getRole(), "anotherrole");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.PRODUCER.class).getName(), "Some Producer");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.DIRECTOR.class).getName(), "Some Director");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.PLAYLIST.class), "Some Playlist");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.LONG_DESCRIPTION.class), "Some Long Description");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.ICON.class).toString(), "http://some/icon");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.REGION.class), "US");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.RATING.class), "R");

        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.RADIO_CALL_SIGN.class), "KSJO");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.RADIO_STATION_ID.class), "107.7");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.RADIO_BAND.class), "FM");

        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.CHANNEL_NR.class), new Integer(123));
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.CHANNEL_NAME.class), "Some Channel");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.SCHEDULED_START_TIME.class), "2010-12-24T14:33:55");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.SCHEDULED_END_TIME.class), "2010-12-24T14:43:55");

        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.DVD_REGION_CODE.class), new Integer(2));
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.ORIGINAL_TRACK_NUMBER.class), new Integer(11));
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.TOC.class), "123abc");
        assertEquals(itemOne.getFirstPropertyValue(DIDLObject.Property.UPNP.USER_ANNOTATION.class), "Some User Annotation");

        Item itemTwo = items.get(1);
        assertEquals(itemTwo.getId(), "7");
        assertEquals(itemTwo.getParentID(), "5");
        assert !itemTwo.isRestricted();
        assertEquals(itemTwo.getTitle(), "Drown");
        assertEquals(itemTwo.getCreator(), "Smashing Pumpkins");
        assertEquals(itemTwo.getClazz().getValue(), "object.item.audioItem.musicTrack");
        resource = itemTwo.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/mpeg:*");
        assertEquals(resource.getSize(), new Long(140000));
        assertEquals(resource.getValue(), "http://10.0.0.1/getcontent.asp?id=7");

        Item itemThree = items.get(2);
        assertEquals(itemThree.getId(), "8");
        assertEquals(itemThree.getParentID(), "5");
        assert !itemThree.isRestricted();
        assertEquals(itemThree.getTitle(), "State Of Love And Trust");
        assertEquals(itemThree.getCreator(), null);
        assertEquals(itemThree.getClazz().getValue(), "object.item.audioItem.musicTrack");
        resource = itemThree.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/x-ms-wma:*");
        assertEquals(resource.getSize(), new Long(70000));
        assertEquals(resource.getValue(), "http://10.0.0.1/getcontent.asp?id=8");

        Item itemFour = items.get(3);
        assertEquals(itemFour.getId(), "9");
        assertEquals(itemFour.getParentID(), "5");
        assertEquals(itemFour.getRefID(), "8");
        assert !itemFour.isRestricted();
        assertEquals(itemFour.getTitle(), "State Of Love And Trust");
        assertEquals(itemFour.getCreator(), "Pearl Jam");

    }

    protected void assertSampleMixed(DIDLContent didl) throws Exception {

        DescMeta meta0 = didl.getDescMetadata().get(0);
        assertEquals(meta0.getId(), "a1");
        assertEquals(meta0.getType(), "Some Text");
        assertEquals(meta0.getNameSpace().toString(), "urn:my-vendor-extension");
        Document meta0Doc = (Document) meta0.getMetadata();
        assertEquals(documentToString(meta0Doc), "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>" +
                "<desc-wrapper xmlns=\"urn:fourthline-org:cling:support:content-directory-desc-1-0\">" +
                "<vendor:foo xmlns:vendor=\"urn:my-vendor-extension\">" +
                "<vendor:bar vendor:abc=\"123\">aaa</vendor:bar>" +
                "<vendor:baz>bbb</vendor:baz>" +
                "</vendor:foo>" +
                "</desc-wrapper>");

        List<Container> containers = didl.getContainers();
        assertEquals(containers.size(), 1);

        Container slideShowContainer = didl.getContainers().get(0);

        assertEquals(slideShowContainer.getId(), "10");
        assertEquals(slideShowContainer.getParentID(), "5");
        assertEquals(slideShowContainer.getChildCount(), new Integer(3));
        assertEquals(slideShowContainer.isRestricted(), true);
        assertEquals(slideShowContainer.isSearchable(), false);
        assertEquals(slideShowContainer.getTitle(), "Slideshow");
        assertEquals(slideShowContainer.getClazz().getValue(), "object.container.album.photoAlbum");

        DescMeta meta1 = slideShowContainer.getDescMetadata().get(0);
        assertEquals(meta1.getId(), "b1");
        assertEquals(meta1.getType(), "Some Text");
        assertEquals(meta1.getNameSpace().toString(), "urn:my-vendor-extension");
        Document meta1Doc = (Document) meta1.getMetadata();
        assertEquals(documentToString(meta1Doc), "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>" +
                "<desc-wrapper xmlns=\"urn:fourthline-org:cling:support:content-directory-desc-1-0\">" +
                "<vendor:foo xmlns:vendor=\"urn:my-vendor-extension\">bar</vendor:foo>" +
                "</desc-wrapper>");

        List<Item> items = didl.getItems();
        assertEquals(items.size(), 3);

        Item itemOne = items.get(0);
        assertEquals(itemOne.getId(), "6");
        assertEquals(itemOne.getParentID(), "5");
        assert itemOne.isRestricted();
        assertEquals(itemOne.getTitle(), "Chloe Dancer");
        assertEquals(itemOne.getCreator(), "Mother Love Bone");
        assertEquals(itemOne.getClazz().getValue(), "object.item.audioItem.musicTrack");
        Res resource = itemOne.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/x-ms-wma:*");
        assertEquals(resource.getSize(), new Long(200000));
        assertEquals(resource.getValue(), "http://10.0.0.1/getcontent.asp?id=6");

        DescMeta meta2 = itemOne.getDescMetadata().get(0);
        assertEquals(meta2.getId(), "c1");
        assertEquals(meta2.getType(), "Some Text");
        assertEquals(meta2.getNameSpace().toString(), "urn:my-vendor-extension");
        Document meta2Doc = (Document) meta2.getMetadata();
        assertEquals(documentToString(meta2Doc), "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>" +
                "<desc-wrapper xmlns=\"urn:fourthline-org:cling:support:content-directory-desc-1-0\">" +
                "<vendor:foo xmlns:vendor=\"urn:my-vendor-extension\">bar</vendor:foo>" +
                "</desc-wrapper>");

        DescMeta meta3 = itemOne.getDescMetadata().get(1);
        assertEquals(meta3.getId(), "c2");
        assertEquals(meta3.getType(), "More Text");
        assertEquals(meta3.getNameSpace().toString(), "urn:my-vendor-extension");
        Document meta3Doc = (Document) meta3.getMetadata();
        assertEquals(documentToString(meta3Doc), "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>" +
                "<desc-wrapper xmlns=\"urn:fourthline-org:cling:support:content-directory-desc-1-0\">" +
                "<vendor:foo xmlns:vendor=\"urn:my-vendor-extension\">baz</vendor:foo>" +
                "</desc-wrapper>");

        Item itemTwo = items.get(1);
        assertEquals(itemTwo.getId(), "7");
        assertEquals(itemTwo.getParentID(), "5");
        assert itemTwo.isRestricted();
        assertEquals(itemTwo.getTitle(), "Drown");
        assertEquals(itemTwo.getCreator(), "Smashing Pumpkins");
        assertEquals(itemTwo.getClazz().getValue(), "object.item.audioItem.musicTrack");
        resource = itemTwo.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/mpeg:*");
        assertEquals(resource.getSize(), new Long(140000));
        assertEquals(resource.getValue(), "http://10.0.0.1/getcontent.asp?id=7");

        Item itemThree = items.get(2);
        assertEquals(itemThree.getId(), "8");
        assertEquals(itemThree.getParentID(), "5");
        assert itemThree.isRestricted();
        assertEquals(itemThree.getTitle(), "State Of Love And Trust");
        assertEquals(itemThree.getCreator(), "Pearl Jam");
        assertEquals(itemThree.getClazz().getValue(), "object.item.audioItem.musicTrack");
        resource = itemThree.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/x-ms-wma:*");
        assertEquals(resource.getSize(), new Long(70000));
        assertEquals(resource.getValue(), "http://10.0.0.1/getcontent.asp?id=8");

    }

    protected void assertSampleNested(DIDLContent didl) {

        List<Container> containers = didl.getContainers();
        assertEquals(containers.size(), 1);

        Container container = didl.getContainers().get(0);

        assertEquals(container.getId(), "5");
        assertEquals(container.getParentID(), "1");
        assertEquals(container.getChildCount(), new Integer(4));
        assertEquals(container.isRestricted(), false);
        assertEquals(container.isSearchable(), false);
        assertEquals(container.getTitle(), "Singles Soundtrack");
        assertEquals(container.getClazz().getValue(), "object.container.album.musicAlbum");
        assertEquals(container.getSearchClasses().size(), 1);
        assertEquals(container.getSearchClasses().get(0).isIncludeDerived(), false);
        assertEquals(container.getSearchClasses().get(0).getValue(), "object.item.audioItem.musicTrack");

        List<Item> items = container.getItems();
        assertEquals(items.size(), 3);

        Item itemOne = items.get(0);
        assertEquals(itemOne.getId(), "6");
        assertEquals(itemOne.getParentID(), "5");
        assert itemOne.isRestricted();
        assertEquals(itemOne.getTitle(), "Chloe Dancer");
        assertEquals(itemOne.getCreator(), "Mother Love Bone");
        assertEquals(itemOne.getClazz().getValue(), "object.item.audioItem.musicTrack");
        Res resource = itemOne.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/x-ms-wma:*");
        assertEquals(resource.getSize(), new Long(200000));
        assertEquals(resource.getValue(), "http://10.0.0.1/getcontent.asp?id=6");

        Item itemTwo = items.get(1);
        assertEquals(itemTwo.getId(), "7");
        assertEquals(itemTwo.getParentID(), "5");
        assert itemTwo.isRestricted();
        assertEquals(itemTwo.getTitle(), "Drown");
        assertEquals(itemTwo.getCreator(), "Smashing Pumpkins");
        assertEquals(itemTwo.getClazz().getValue(), "object.item.audioItem.musicTrack");
        resource = itemTwo.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/mpeg:*");
        assertEquals(resource.getSize(), new Long(140000));
        assertEquals(resource.getValue(), "http://10.0.0.1/getcontent.asp?id=7");

        Item itemThree = items.get(2);
        assertEquals(itemThree.getId(), "8");
        assertEquals(itemThree.getParentID(), "5");
        assert itemThree.isRestricted();
        assertEquals(itemThree.getTitle(), "State Of Love And Trust");
        assertEquals(itemThree.getCreator(), "Pearl Jam");
        assertEquals(itemThree.getClazz().getValue(), "object.item.audioItem.musicTrack");
        resource = itemThree.getResources().get(0);
        assertEquals(resource.getProtocolInfo().toString(), "http-get:*:audio/x-ms-wma:*");
        assertEquals(resource.getSize(), new Long(70000));
        assertEquals(resource.getValue(), "http://10.0.0.1/getcontent.asp?id=8");

        // More specific types

        MusicAlbum album = (MusicAlbum)container;
        assertEquals(album.getMusicTracks()[0].getTitle(), "Chloe Dancer");
        assertEquals(album.getMusicTracks()[1].getTitle(), "Drown");
        assertEquals(album.getMusicTracks()[2].getTitle(), "State Of Love And Trust");

    }

    protected void assertSampleItemsMixed(DIDLContent didl) {

        assertEquals(didl.getContainers().size(), 0);

        List<Item> items = didl.getItems();

        MusicTrack item0 = (MusicTrack) items.get(0);
        assertEquals(item0.getFirstGenre(), "Pop");
        assertEquals(item0.getDescription(), "Some Description");
        assertEquals(item0.getLongDescription(), "Some Long Description");
        assertEquals(item0.getPublishers()[0].getName(), "Some Publisher");
        assertEquals(item0.getPublishers()[1].getName(), "Another Publisher");
        assertEquals(item0.getLanguage(), "en-US");
        assertEquals(item0.getFirstRelation().toString(), "http://some/related/resource");
        assertEquals(item0.getFirstRights(), "CC SA-BY");
        assertEquals(item0.getFirstArtist().getName(), "Some Artist");
        assertEquals(item0.getFirstArtist().getRole(), "Performer");
        assertEquals(item0.getAlbum(), "Some Album");
        assertEquals(item0.getOriginalTrackNumber(), new Integer(11));
        assertEquals(item0.getFirstPlaylist(), "Some Playlist");
        assertEquals(item0.getStorageMedium(), StorageMedium.NETWORK);
        assertEquals(item0.getFirstContributor().getName(), "Some Contributor");
        assertEquals(item0.getDate(), "2010-12-24");

        AudioBook item1 = (AudioBook) items.get(1);
        assertEquals(item1.getFirstGenre(), "Pop");
        assertEquals(item1.getDescription(), "Some Description");
        assertEquals(item1.getLongDescription(), "Some Long Description");
        assertEquals(item1.getPublishers()[0].getName(), "Some Publisher");
        assertEquals(item1.getPublishers()[1].getName(), "Another Publisher");
        assertEquals(item1.getLanguage(), "en-US");
        assertEquals(item1.getFirstRelation().toString(), "http://some/related/resource");
        assertEquals(item1.getFirstRights(), "CC SA-BY");
        assertEquals(item1.getFirstProducer().getName(), "Some Producer");
        assertEquals(item1.getFirstContributor().getName(), "Some Contributor");
        assertEquals(item1.getDate(), "2010-12-24");
        assertEquals(item1.getStorageMedium(), StorageMedium.NETWORK);

        AudioBroadcast item2 = (AudioBroadcast) items.get(2);
        assertEquals(item2.getFirstGenre(), "Pop");
        assertEquals(item2.getDescription(), "Some Description");
        assertEquals(item2.getLongDescription(), "Some Long Description");
        assertEquals(item2.getPublishers()[0].getName(), "Some Publisher");
        assertEquals(item2.getPublishers()[1].getName(), "Another Publisher");
        assertEquals(item2.getLanguage(), "en-US");
        assertEquals(item2.getFirstRelation().toString(), "http://some/related/resource");
        assertEquals(item2.getFirstRights(), "CC SA-BY");
        assertEquals(item2.getRegion(), "US");
        assertEquals(item2.getRadioCallSign(), "KSJO");
        assertEquals(item2.getRadioStationID(), "107.7");
        assertEquals(item2.getRadioBand(), "FM");
        assertEquals(item2.getChannelNr(), new Integer(123));

        Movie item3 = (Movie) items.get(3);
        assertEquals(item3.getFirstGenre(), "Pop");
        assertEquals(item3.getDescription(), "Some Description");
        assertEquals(item3.getLongDescription(), "Some Long Description");
        assertEquals(item3.getFirstDirector().getName(), "Some Director");
        assertEquals(item3.getFirstProducer().getName(), "Some Producer");
        assertEquals(item3.getPublishers()[0].getName(), "Some Publisher");
        assertEquals(item3.getPublishers()[1].getName(), "Another Publisher");
        assertEquals(item3.getLanguage(), "en-US");
        assertEquals(item3.getFirstRelation().toString(), "http://some/related/resource");
        assertEquals(item3.getRating(), "R");
        assertEquals(item3.getStorageMedium(), StorageMedium.NETWORK);
        assertEquals(item3.getDVDRegionCode(), new Integer(2));
        assertEquals(item3.getChannelName(), "Some Channel");
        assertEquals(item3.getFirstScheduledStartTime(), "2010-12-24T14:33:55");
        assertEquals(item3.getFirstScheduledEndTime(), "2010-12-24T14:43:55");

        VideoBroadcast item4 = (VideoBroadcast) items.get(4);
        assertEquals(item4.getFirstGenre(), "Pop");
        assertEquals(item4.getDescription(), "Some Description");
        assertEquals(item4.getLongDescription(), "Some Long Description");
        assertEquals(item4.getFirstDirector().getName(), "Some Director");
        assertEquals(item4.getFirstProducer().getName(), "Some Producer");
        assertEquals(item4.getPublishers()[0].getName(), "Some Publisher");
        assertEquals(item4.getPublishers()[1].getName(), "Another Publisher");
        assertEquals(item4.getLanguage(), "en-US");
        assertEquals(item4.getFirstRelation().toString(), "http://some/related/resource");
        assertEquals(item4.getRating(), "R");
        assertEquals(item4.getIcon().toString(), "http://some/icon");
        assertEquals(item4.getRegion(), "US");
        assertEquals(item4.getChannelNr(), new Integer(123));

        MusicVideoClip item5 = (MusicVideoClip) items.get(5);
        assertEquals(item5.getFirstGenre(), "Pop");
        assertEquals(item5.getDescription(), "Some Description");
        assertEquals(item5.getLongDescription(), "Some Long Description");
        assertEquals(item5.getFirstDirector().getName(), "Some Director");
        assertEquals(item5.getFirstProducer().getName(), "Some Producer");
        assertEquals(item5.getPublishers()[0].getName(), "Some Publisher");
        assertEquals(item5.getPublishers()[1].getName(), "Another Publisher");
        assertEquals(item5.getLanguage(), "en-US");
        assertEquals(item5.getFirstRelation().toString(), "http://some/related/resource");
        assertEquals(item5.getRating(), "R");
        assertEquals(item5.getFirstArtist().getName(), "Some Artist");
        assertEquals(item5.getFirstArtist().getRole(), "Performer");
        assertEquals(item5.getAlbum(), "Some Album");
        assertEquals(item5.getStorageMedium(), StorageMedium.NETWORK);
        assertEquals(item5.getFirstContributor().getName(), "Some Contributor");
        assertEquals(item5.getDate(), "2010-12-24");
        assertEquals(item5.getFirstScheduledStartTime(), "2010-12-24T14:33:55");
        assertEquals(item5.getFirstScheduledEndTime(), "2010-12-24T14:43:55");

        Photo item6 = (Photo) items.get(6);
        assertEquals(item6.getDescription(), "Some Description");
        assertEquals(item6.getLongDescription(), "Some Long Description");
        assertEquals(item6.getPublishers()[0].getName(), "Some Publisher");
        assertEquals(item6.getPublishers()[1].getName(), "Another Publisher");
        assertEquals(item6.getRating(), "R");
        assertEquals(item6.getDate(), "2010-12-24");
        assertEquals(item6.getStorageMedium(), StorageMedium.NETWORK);
        assertEquals(item6.getFirstRights(), "CC SA-BY");
        assertEquals(item6.getAlbum(), "Some Album");

        PlaylistItem item7 = (PlaylistItem) items.get(7);
        assertEquals(item7.getFirstGenre(), "Pop");
        assertEquals(item7.getDescription(), "Some Description");
        assertEquals(item7.getLongDescription(), "Some Long Description");
        assertEquals(item7.getLanguage(), "en-US");
        assertEquals(item7.getFirstArtist().getName(), "Some Artist");
        assertEquals(item7.getFirstArtist().getRole(), "Performer");
        assertEquals(item7.getStorageMedium(), StorageMedium.NETWORK);
        assertEquals(item7.getDate(), "2010-12-24");

        TextItem item8 = (TextItem) items.get(8);
        assertEquals(item8.getDescription(), "Some Description");
        assertEquals(item8.getLongDescription(), "Some Long Description");
        assertEquals(item8.getFirstAuthor().getName(), "Some Author");
        assertEquals(item8.getFirstAuthor().getRole(), "anotherrole");
        assertEquals(item8.getStorageMedium(), StorageMedium.NETWORK);
        assertEquals(item8.getRating(), "R");
        assertEquals(item8.getPublishers()[0].getName(), "Some Publisher");
        assertEquals(item8.getPublishers()[1].getName(), "Another Publisher");
        assertEquals(item5.getFirstContributor().getName(), "Some Contributor");
        assertEquals(item8.getLanguage(), "en-US");
        assertEquals(item8.getFirstRelation().toString(), "http://some/related/resource");
        assertEquals(item8.getDate(), "2010-12-24");
        assertEquals(item6.getFirstRights(), "CC SA-BY");
    }

    protected void assertSampleFoldersMixed(DIDLContent didl) {

        assertEquals(didl.getItems().size(), 0);

        List<Container> containers = didl.getContainers();

        Album c0 = (Album) containers.get(0);
        assertEquals(c0.getChildCount(), new Integer(3));
        assertEquals(c0.isRestricted(), true);
        assertEquals(c0.getDescription(), "Some Description");
        assertEquals(c0.getLongDescription(), "Some Long Description");
        assertEquals(c0.getPublishers()[0].getName(), "Some Publisher");
        assertEquals(c0.getPublishers()[1].getName(), "Another Publisher");
        assertEquals(c0.getFirstRelation().toString(), "http://some/related/resource");
        assertEquals(c0.getFirstRights(), "CC SA-BY");
        assertEquals(c0.getStorageMedium(), StorageMedium.NETWORK);
        assertEquals(c0.getFirstContributor().getName(), "Some Contributor");
        assertEquals(c0.getDate(), "2010-12-24");

        PhotoAlbum c1 = (PhotoAlbum)containers.get(1);
        assertEquals(c1.getChildCount(), new Integer(2));
        assertEquals(c1.getTitle(), "Some Photos");
        assertEquals(c1.getPhotos()[0].getTitle(), "Photo 2010");
        assertEquals(c1.getPhotos()[0].getAlbum(), "Some Photos");
        assertEquals(c1.getPhotos()[1].getTitle(), "Photo 2011");
        assertEquals(c1.getPhotos()[1].getAlbum(), "Some Photos");
    }

    public String documentToString(Document document) throws Exception {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        StringWriter out = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(out));
        return out.toString();
    }
}

