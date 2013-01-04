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

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.seamless.util.io.IO;
import org.seamless.util.MimeType;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.testng.Assert.assertEquals;

/**
 * Browsing a ContentDirectory
 * <p/>
 * <p>
 * A <em>ContentDirectory:1</em> service provides media resource metadata. The content format for
 * this metadata is XML and the schema is a mixture of DIDL, Dublic Core, and UPnP specific elements
 * and attributes. Usually you'd have to call the <code>Browse</code> action of the content directory
 * service to get this XML metadata and then parse it manually.
 * </p>
 * <p>
 * The <code>Browse</code> action callback in Cling Support handles all of this for you:
 * </p>
 * <a class="citation" href="javadoc://this#browseTracks()" style="read-title: false;"/>
 */
public class ContentDirectoryBrowseTest {

    protected DIDLParser parser = new DIDLParser();

    @Test
    public void browseRootMetadata() {

        final boolean[] assertions = new boolean[3];
        new Browse(createService(), "0", BrowseFlag.METADATA) {
            @Override
            public void received(ActionInvocation actionInvocation, DIDLContent didl) {
                assertEquals(didl.getContainers().size(), 1);
                assertEquals(didl.getContainers().get(0).getTitle(), "My multimedia stuff");
                assertions[0] = true;
            }

            @Override
            public void updateStatus(Status status) {
                if (!assertions[1] && status.equals(Status.LOADING)) {
                    assertions[1] = true;
                } else if (assertions[1] && !assertions[2] && status.equals(Status.OK)) {
                    assertions[2] = true;
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

            }
        }.run();

        for (boolean assertion : assertions) {
            assertEquals(assertion, true);
        }
    }

    @Test
    public void browseRootChildren() {

        final boolean[] assertions = new boolean[3];
        new Browse(
                createService(), "0", BrowseFlag.DIRECT_CHILDREN, "foo", 1, 10l,
                new SortCriterion(true, "dc:title"), new SortCriterion(false, "dc:creator")
        ) {
            public void received(ActionInvocation actionInvocation, DIDLContent didl) {
                assertEquals(didl.getContainers().size(), 3);
                assertEquals(didl.getContainers().get(0).getTitle(), "My Music");
                assertEquals(didl.getContainers().get(1).getTitle(), "My Photos");
                assertEquals(didl.getContainers().get(2).getTitle(), "Album Art");
                assertions[0] = true;
            }

            @Override
            public void updateStatus(Status status) {
                if (!assertions[1] && status.equals(Status.LOADING)) {
                    assertions[1] = true;
                } else if (assertions[1] && !assertions[2] && status.equals(Status.OK)) {
                    assertions[2] = true;
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        }.run();

        for (boolean assertion : assertions) {
            assertEquals(assertion, true);
        }
    }

    /**
     * <a class="citation" href="javacode://this" style="include: INC1; exclude: EXC1, EXC2;"/>
     * <p>
     * The first callback retrieves all the children of container <code>3</code> (container identifier).
     * </p>
     * <div class="note" id="browse_root_container">
     *     <div class="title">The root container identifier</div>
     *     You can not copy/paste the shown example code! It will most likely not return any items!
     *     You need to use a different container ID! The shown container ID '3' is just an example.
     *     Your server does not have a container with identifier '3'! If you want to browse the
     *     "root" container of the ContentDirectory, use the identifier '0':
     *     <code>Browse(service, "0", BrowseFlag.DIRECT_CHILDREN)</code>. Although not standardized
     *     many media servers consider the ID '0' to be the root container's identifier. If it's not,
     *     ask your media server vendor. By listing all the children of the root container you can
     *     get the identifiers of sub-containers and so on, recursively.
     * </div>
     * <p>
     * The <code>received()</code> method is called after the DIDL XML content has been validated and
     * parsed, so you can use a type-safe API to work with the metadata.
     * DIDL content is a composite structure of <code>Container</code> and <code>Item</code> elements,
     * here we are interested in the items of the container, ignoring any sub-containers it might or
     * might not have.
     * </p>
     * <p>
     * You can implement or ignore the <code>updateStatus()</code> method, it's convenient to be
     * notified before the metadata is loaded, and after it has been parsed. You can use this
     * event to update a status message/icon of your user interface, for example.
     * </p>
     * <p>
     * This more complex callback instantiation shows some of the available options:
     * </p>
     * <a class="citation" href="javacode://this" id="browse_tracks2" style="include: INC2; exclude: EXC3;"/>
     * <p>
     * The arguments declare filtering with a wildcard, limiting the result to 50 items starting at
     * item 100 (pagination), and some sort criteria. It's up to the content directory
     * provider to handle these options.
     * </p>
     */
    @Test
    public void browseTracks() {

        final boolean[] assertions = new boolean[3];

        Service service = createService();

        ActionCallback simpleBrowseAction =
                new Browse(service, "3", BrowseFlag.DIRECT_CHILDREN) {                      // DOC: INC1

                    @Override
                    public void received(ActionInvocation actionInvocation, DIDLContent didl) {

                        // Read the DIDL content either using generic Container and Item types...
                        assertEquals(didl.getItems().size(), 2);
                        Item item1 = didl.getItems().get(0);
                        assertEquals(
                                item1.getTitle(),
                                "All Secrets Known"
                        );
                        assertEquals(
                                item1.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM.class),
                                "Black Gives Way To Blue"
                        );
                        assertEquals(
                                item1.getFirstResource().getProtocolInfo().getContentFormatMimeType().toString(),
                                "audio/mpeg"
                        );
                        assertEquals(
                                item1.getFirstResource().getValue(),
                                "http://10.0.0.1/files/101.mp3"
                        );

                        // ... or cast it if you are sure about its type ...
                        assert MusicTrack.CLASS.equals(item1);
                        MusicTrack track1 = (MusicTrack) item1;
                        assertEquals(track1.getTitle(), "All Secrets Known");
                        assertEquals(track1.getAlbum(), "Black Gives Way To Blue");
                        assertEquals(track1.getFirstArtist().getName(), "Alice In Chains");
                        assertEquals(track1.getFirstArtist().getRole(), "Performer");

                        MusicTrack track2 = (MusicTrack) didl.getItems().get(1);
                        assertEquals(track2.getTitle(), "Check My Brain");

                        // ... which is much nicer for manual parsing, of course!

                        // DOC: EXC1
                        assertions[0] = true;
                        // DOC: EXC1
                    }

                    @Override
                    public void updateStatus(Status status) {
                        // Called before and after loading the DIDL content
                        // DOC: EXC2
                        if (!assertions[1] && status.equals(Status.LOADING)) {
                            assertions[1] = true;
                        } else if (assertions[1] && !assertions[2] && status.equals(Status.OK)) {
                            assertions[2] = true;
                        }
                        // DOC: EXC2
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        // Something wasn't right...
                    }
                };                                                                          // DOC: INC1


        ActionCallback complexBrowseAction =                                                // DOC: INC2
                new Browse(service, "3", BrowseFlag.DIRECT_CHILDREN,
                           "*",
                           100l, 50l,
                           new SortCriterion(true, "dc:title"),        // Ascending
                           new SortCriterion(false, "dc:creator")) {   // Descending

                    // Implementation...

                    // DOC: EXC3
                    @Override
                    public void received(ActionInvocation actionInvocation, DIDLContent didl) {

                    }

                    @Override
                    public void updateStatus(Status status) {

                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    }
                    // DOC: EXC3
                };                                                                          // DOC: INC2

        simpleBrowseAction.run();

        for (boolean assertion : assertions) {
            assertEquals(assertion, true);
        }
    }

    public Service createService() {
        LocalService<AbstractContentDirectoryService> service =
                new AnnotationLocalServiceBinder().read(AbstractContentDirectoryService.class);
        service.setManager(
                new DefaultServiceManager<AbstractContentDirectoryService>(service, null) {
                    @Override
                    protected AbstractContentDirectoryService createServiceInstance() throws Exception {
                        return new MP3ContentDirectory();
                    }
                }
        );
        return service;
    }

    /**
     * The ContentDirectory service
     * <p>
     * Let's switch perspective and consider the server-side of a <em>ContentDirectory</em>. Bundled in
     * Cling Support is a simple <em>ContentDirectory</em> abstract service class,
     * the only thing you have to do is implement the <code>browse()</code> method:
     * </p>
     * <a class="citation" href="javacode://this" style="exclude: EXC1, EXTRA"/>
     * <p>
     * You need a <code>DIDLContent</code> instance and a <code>DIDLParser</code> that will transform
     * the content into an XML string when the <code>BrowseResult</code> is returned. It's up to
     * you how you construct the DIDL content, typically you'd have a backend database you'd query
     * and then build the <code>Container</code> and <code>Item</code> graph dynamically. Cling provides
     * many convenience content model classes fore representing multimedia metadata, as defined
     * in the <em>ContentDirectory:1</em> specification (<code>MusicTrack</code>, <code>Movie</code>, etc.),
     * they can all be found in the package <code>org.fourthline.cling.support.model</code>.
     * </p>
     * <p>
     * The <code>DIDLParser</code> is <em>not</em> thread-safe, so don't share a single instance
     * between all threads of your server application!
     * </p>
     * <p>
     * The <code>AbstractContentDirectoryService</code> only implements the mandatory actions and
     * state variables as defined in <em>ContentDirectory:1</em> for browsing and searching
     * content. If you want to enable editing of metadata, you have to add additional action methods.
     * </p>
     */
    public class MP3ContentDirectory extends AbstractContentDirectoryService {

        @Override
        public BrowseResult browse(String objectID, BrowseFlag browseFlag,
                                   String filter,
                                   long firstResult, long maxResults,
                                   SortCriterion[] orderby) throws ContentDirectoryException {
            try {
                // DOC: EXC1
                if (objectID.equals("0") && browseFlag.equals(BrowseFlag.METADATA)) {

                    assertEquals(firstResult, 0);
                    assertEquals(maxResults, 999);

                    assertEquals(orderby.length, 0);

                    String result = readResource("org/fourthline/cling/test/support/contentdirectory/samples/browseRoot.xml");
                    return new BrowseResult(result, 1, 1);

                } else if (objectID.equals("0") && browseFlag.equals(BrowseFlag.DIRECT_CHILDREN)) {

                    assertEquals(filter, "foo");
                    assertEquals(firstResult, 1);
                    assertEquals(maxResults, 10l);

                    assertEquals(orderby.length, 2); // We don't sort, just test
                    assertEquals(orderby[0].isAscending(), true);
                    assertEquals(orderby[0].getPropertyName(), "dc:title");
                    assertEquals(orderby[1].isAscending(), false);
                    assertEquals(orderby[1].getPropertyName(), "dc:creator");

                    String result = readResource("org/fourthline/cling/test/support/contentdirectory/samples/browseRootChildren.xml");
                    return new BrowseResult(result, 3, 3);
                }
                // DOC: EXC1

                // This is just an example... you have to create the DIDL content dynamically!

                DIDLContent didl = new DIDLContent();

                String album = ("Black Gives Way To Blue");
                String creator = "Alice In Chains"; // Required
                PersonWithRole artist = new PersonWithRole(creator, "Performer");
                MimeType mimeType = new MimeType("audio", "mpeg");

                didl.addItem(new MusicTrack(
                        "101", "3", // 101 is the Item ID, 3 is the parent Container ID
                        "All Secrets Known",
                        creator, album, artist,
                        new Res(mimeType, 123456l, "00:03:25", 8192l, "http://10.0.0.1/files/101.mp3")
                ));

                didl.addItem(new MusicTrack(
                        "102", "3",
                        "Check My Brain",
                        creator, album, artist,
                        new Res(mimeType, 2222222l, "00:04:11", 8192l, "http://10.0.0.1/files/102.mp3")
                ));

                // Create more tracks...

                // Count and total matches is 2
                return new BrowseResult(new DIDLParser().generate(didl), 2, 2);

            } catch (Exception ex) {
                throw new ContentDirectoryException(
                        ContentDirectoryErrorCode.CANNOT_PROCESS,
                        ex.toString()
                );
            }
        }

        @Override
        public BrowseResult search(String containerId,
                                   String searchCriteria, String filter,
                                   long firstResult, long maxResults,
                                   SortCriterion[] orderBy) throws ContentDirectoryException {
            // You can override this method to implement searching!
            return super.search(containerId, searchCriteria, filter, firstResult, maxResults, orderBy);
        }
    }

    protected String readResource(String resource) {
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            return IO.readLines(is);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (is != null) is.close();
            } catch (Exception ex) {
                //
            }
        }
    }

}
