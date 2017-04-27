# Cling - The UPnP stack for Java and Android

Cling is an effort to create a UPnP-compatible software stack in Java. The project's goals are strict specification compliance, complete, clean and extensive APIs, as well as rich SPIs for easy customization.

Cling is Free Software, distributed under the terms of the <a href="http://www.gnu.org/licenses/lgpl-2.1.html">GNU Lesser General Public License</a> <b>or at your option</b> the <a href="http://opensource.org/licenses/CDDL-1.0">Common Development and Distribution License</a>.

We recommend you start with the [README.txt](https://github.com/4thline/cling/blob/master/distribution/src/dist/README.txt).

Then [download the Cling distribution](https://github.com/4thline/cling/releases) or start with a Maven project in your `pom.xml`:

```
<repositories>
    <repository>
        <id>4thline-repo</id>
        <url>http://4thline.org/m2</url>
        <snapshots>
            <enabled>false</enabled> <!-- Or true, if you like to use unreleased code -->
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.fourthline.cling</groupId>
        <artifactId>cling-core</artifactId>
        <version>2.1.1</version>
    </dependency>
</dependencies>
```

Read <a href="http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#chapter.GettingStarted">the first chapter of the manual</a> for a simple Cling usage example. Have a look at the <a href="https://github.com/4thline/cling/tree/master/demo/android">Android application examples</a>.

**Please post Cling usage questions on [stackoverflow.com](https://stackoverflow.com/questions/tagged/cling+upnp) with the appropriate tags.**

The main Cling modules are:

### Cling Core

An embeddable Java library that implements the <a href="http://www.upnp.org/resources/documents.asp">UPnP Device Architecture 1.0</a>. Use Cling Core to expose services with a UPnP remoting interface, or to write control point applications that discover UPnP devices and utilize their services. You can also integrate <a href="http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#chapter.Android">Cling Core as an Android UPnP/DLNA library in your applications</a> (platform level 15/4.0 required).

- [User Manual](http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml)
- [API Javadoc](http://4thline.org/projects/cling/core/apidocs)
- [Source XRef](http://4thline.org/projects/cling/core/xref)
- [Test Source XRef](http://4thline.org/projects/cling/core/xref-test)

### Cling Support

Optional classes and useful infrastructure for developing and controlling UPnP services with Cling Core; extensions that simplify working with UPnP media servers and renderers, <a href="http://4thline.org/projects/cling/support/manual/cling-support-manual.xhtml#section.PortMapping">NAT port mapping on routers</a>, etc.

- [User Manual](http://4thline.org/projects/cling/support/manual/cling-support-manual.xhtml)
- [API Javadoc](http://4thline.org/projects/cling/support/apidocs)
- [Source XRef](http://4thline.org/projects/cling/support/xref)
- [Test Source XRef](http://4thline.org/projects/cling/support/xref-test)

### Cling Workbench

A desktop application for browsing UPnP devices and interacting with their services.

### Cling MediaRenderer

Standalone <a href="http://www.upnp.org/resources/documents.asp">UPnP MediaRenderer</a>, based on gstreamer.

Building Cling
---------------------

* Install Maven 3.2.3 or newer.

* Install the Android SDK and set the ANDROID_HOME environment variable to the SDK install directory.

* Clone the Cling source:

````
git clone https://github.com/4thline/cling.git
````

* Change into the `cling/` directory.

* Install everything into your local `~/.m2` Maven repository (this will take a few minutes if all dependencies have to be downloaded for the first time).

````
mvn clean install
````

If your build fails with Android/dex packaging errors, you forgot the clean.

* Use Cling in your pom.xml with:

````
<dependencies>
  <dependency>
    <groupId>org.fourthline.cling</groupId>
    <artifactId>cling-core</artifactId>
    <version>2.1.2-SNAPSHOT</version>
  </dependency>
</dependencies>
````

Building OS X Workbench DMG
---

    hdiutil create -srcfolder \
        workbench/target/cling-workbench-2.1.2-SNAPSHOT/Cling\ Workbench.app \
        workbench/target/cling-workbench-2.1.2-SNAPSHOT/Cling\ Workbench.dmg

Publishing a release
--------------------

Build release and tag on Github.

Update Maven repository:


````
mvn clean install
mvn deploy
````

## Projects and applications using Cling

If your project or product is using Cling and you'd like to add it to this page, open an issue and we'll add you to the list.

* <a href="https://market.android.com/details?id=com.bubblesoft.android.bubbleds">BubbleDS</a> - A UPnP control point for Android and <a href="http://linn.co.uk/digital_stream_players">LinnDS</a> streamer appliances.

* <a href="https://market.android.com/details?id=com.bubblesoft.android.bubbleupnp">BubbleUPnP</a> - A generic UPnP/DLNA media control point and renderer for Android.

* <a href="https://github.com/jinzora/">Jinzora UPnP</a> - The Jinzora music management and streaming server can be accessed through a UPnP MediaServer gateway written with Cling. The Android client also uses Cling to access the MediaServer.

* <a href="https://market.android.com/details?id=com.adree.moviebrowser.upnp">MovieBrowser UPnP</a> - A movie manager to use with your favorite player on your Android tablet. Manage your videos anywhere on your network (Samba &amp; UPnP/DLNA) and watch them with your favorite player or using the unique Play-To feature (Android airplay).

* <a href="https://market.android.com/details?id=com.abk.privatedancer">Private Dancer</a> - A  UPnP/DLNA Media Renderer for Android. It is designed to be used on a device attached to speakers and power. Unlike other UPnP Android applications, Private Dancer is designed for always-on (headless) use.

* <a href="https://play.google.com/store/apps/details?id=com.dbapp.android.mediahouse">MediaHouse</a> - Stream music, videos, movies and pictures from PC, NAS or any other device running UPnP/DLNA compliant media server to your Android phone/handset/tablet.

* <a href="https://play.google.com/store/apps/details?id=be.wyseur.photo">Digital Photo Frame Slideshow</a> - Turn your Android device in a digital photo viewer showing a slideshow of local files, photos from network shares (Samba/SMB) or pictures from a UPnP server.

* <a href="http://www.i-frame.net">I-Frame Home</a> - Full HD digital photo frame, picture-like design, displays files located in internal memory, LAN or Internet.

* <a href="https://play.google.com/store/apps/details?id=de.simatex.free.mediaconnect">MediaConnect</a> - Enables you to connect mediaservers with mediaplayers and also remote control these mediaplayers within a WLAN.

* <a href="https://play.google.com/store/apps/details?id=de.simatex.free.mediaconnect">MediaConnect</a> - MediaConnect enables you to connect mediaservers with mediaplayers and also remote control these mediaplayers within a WLAN.

* <a href="https://play.google.com/store/apps/details?id=de.mip.shufflebox.android">ShuffleBox</a> - Use your smartphone or tablet as a remote control and play music on your laptop, mobile phone or any other DNLA device.

* <a href="https://github.com/trishika/DroidUPnP">DroidUPnP</a> - A FREE SOFTWARE UPnP control point application for Android. DroidUPnP discover your home UPnP device, content provider and renderer. It allows you to browse your UPnP content directory, select the media you want to use and allows you to play it on your connected television or any UPnP renderer compatible device. It also allows you to use your Android device as a UPnP content provider.

* <a href="http://www.yaacc.de/">YAACC</a> -An Android UPnP controller, FREE SOFTWARE as GPL. It allows you to discover, use and control UPnP devices in your network in order to stream media files. Since it's a subset of UPnP it also is capable of communicating with DLNA devices.

## Noteworthy forks of Cling

* <a href="http://www.openhab.org/jupnp/">jUPnP</a> - Forked from pre-2.0, this code base still contains the OSGi feature, which has been removed in Cling 2.0.

* <a href="https://github.com/COLTRAM/cling">DIAL Support</a> - Based on alpha 2.0 code, this UPnP/DLNA library for Java and Android contains modifications to discover DIAL devices.

## FAQ

#### Which version of the UPnP specification does Cling implement?

Cling Core is compatible with the [UPnP Device Architecture 1.0](http://upnp.org/specs/arch/UPnP-arch-DeviceArchitecture-v1.0.pdf).


#### Can I use Cling to access a UPnP/DLNA MediaServer in Android?

Yes, you can write a control point application for Android with Cling Core as a UPnP library. You can find additional utilities for browsing and parsing a MediaServer content directory in the Cling Support module. 

#### Can I use Cling in my commercial application or device?

Cling is licensed under the LGPL, so there are no restrictions on the use of the unmodified Cling JAR files/binaries. You can use the unmodified JAR files/binaries in any application or device, for any purpose. The following distribution (for free or for pay) restrictions apply:
 
* If you distribute Cling with your application or device, you have to include a notice like &quot;contains LGPL software&quot; and a link to the Cling homepage, so your users also get the benefit of Free Software.

* You have to allow replacement of the Cling library in your distributed application. This means allowing replacement of the Cling JAR or JVM binary class file(s) in, for example, a WAR or EAR package.

* For Cling 1.x, the following <b>exception for static linking of an executable</b> (see <a href="https://www.gnu.org/licenses/lgpl.html">LGPLv3 clause 4</a> or LGPLv2 clause 6b) applies to Cling usage within <b>Android applications</b> and the DEX instead of the JVM binary format: Converting Cling's binary JVM class files to the DEX format, and distributing a combined work as an Android APK does not affect the licensing of other resources within that DEX or APK archive. You must however allow re-packaging/conversion of the DEX and APK with tools such as <a href="http://code.google.com/p/dex2jar/">dex2jar</a>. Anyone receiving your APK must be able to replace the Cling binary code with a compatible version. You can <i>not</i> lock your APK with any kind of obfuscation or DRM scheme, or otherwise prevent unpacking and reassembly of the DEX containing Cling binaries. Alternatively, consider <a href="http://android-developers.blogspot.ch/2011/07/custom-class-loading-in-dalvik.html"> dynamic loading of libraries on Android</a>.
                 
* For Cling 2.x, you may at your option license Cling under CDDL instead of the LGPL. You can convert to DEX and package Cling 2.x within an APK without affect on other files in that APK. You can obfuscate the source in the APK and lock it with digital restrictions. If you modify Cling source code, and you distribute a binary compiled from this modified source code, you have to distribute your changed source code as well under the LGPL or CDDL (upon request). Typically this means you contribute your changes back to the Cling project, to be included in an official Cling release.

<a href="mailto:license(at)4thline.com">Contact us</a> if you have questions about the licensing of Cling and/or require a proprietary license.

#### What are the dependencies of Cling Core?

Cling Core is distributed as a single JAR file. It only has one other dependency, the `seamless-*` libraries. All JAR files are typically packaged next to each other in the ZIP distribution. You have to add them to your classpath.

#### How can I access the services of a device?

First write a control point and a `RegistryListener` as explained <a href="http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#section.BinaryLightClient">in the manual</a>. Then call `device.getServices()` when a device has been discovered.

#### Cling doesn't work if I start my application on Tomcat/JBoss/Glassfish/etc?!

You'll get an error on startup, this error tells you that Cling couldn't use the Java JDK's `HTTPURLConnection` for HTTP client operations. This is an old and badly designed part of the JDK: Only &quot;one application&quot; in the whole JVM can configure it. You have to switch Cling to an alternative HTTP client, e.g. the other bundled implementation based on Apache HTTP Core. This is explained in more detail in <a href="http://4thline.org/projects/cling/core/manual/cling-core-manual.xhtml#section.BasicAPI.UpnpService.Configuration"> the user manual</a>.

#### Is IPv6 supported?

No, the default configuration of the UPnP stack in Cling Core will filter all network interfaces and IP addresses that are not IPv4. Some other parts of the Cling Core library might also assume that addresses are IPv4 and the whole library has not been tested in an IPv6 only environment. You are welcome to test Cling on IPv6 with a custom <tt>UpnpServiceConfiguration</tt> and <tt>NetworkAddressFactory</tt> and contribute back any necessary changes.

#### I don't see debug log messages on Android?

The `java.util.logging` implementation on Android is broken, it does not allow you to print debug-level messages easily. See [this discussion](https://stackoverflow.com/questions/4561345/how-to-configure-java-util-logging-on-android/9047282#9047282) for a simple solution.

#### Where can I find the source for Cling 1.x and teleal-common?

<a href="http://4thline.org/projects/download/archive/">Here.</a>

#### Wich version of Android (API) is supported by Cling?

Cling 1.0 supports Android 2.1. With Cling 2.0, we currently require platform level 15 (Android 4.0.3).

#### I get a lock acquisition timeout exception?

Your service receives a subscription, then this happens:

```
RuntimeException at org.teleal.cling.protocol.sync.ReceivingSubscribe.responseSent(ReceivingSubscribe.java:177)
```

Your service was already being used by something else and didn't give up the lock during the 500 millisecond default wait time. Increase the wait time by overriding DefaultServiceManager in LocalService. Or don't block the service action/methods for a long time.

