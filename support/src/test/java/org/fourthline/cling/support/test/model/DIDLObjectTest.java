package org.fourthline.cling.support.test.model;


import org.fourthline.cling.support.model.DIDLObject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class DIDLObjectTest {

    @Test
    public void testHasPropertyWithNormalClass() {
        DIDLObject didlObject = new DIDLObject() {

        };
        DIDLObject.Property property = new DIDLObject.Property.UPNP.ACTOR();
        didlObject.addProperty(property);
        assertTrue(didlObject.hasProperty(DIDLObject.Property.UPNP.ACTOR.class));
    }

    @Test
    public void testHasPropertyWithAnonymousClass() {
        DIDLObject didlObject = new DIDLObject() {

        };
        DIDLObject.Property property = new DIDLObject.Property.UPNP.ACTOR() {

        };
        didlObject.addProperty(property);
        assertTrue(didlObject.hasProperty(DIDLObject.Property.UPNP.ACTOR.class));
    }

}
