==============================================================================
Cling - a UPnP stack for Java
==============================================================================

Version: ${project.version}

==============================================================================
GETTING STARTED
==============================================================================

First read core/manual/cling-core-manual.xhtml!

Start the Cling Workbench by double-clicking the JAR file, or with:
    'java -jar <cling-workbench-standalone.jar>'.

Start the Cling MediaRenderer with:
    'java -Djna.library.path=<path to gstreamer> \
          -jar <cling-mediarenderer-standalone.jar>'

==============================================================================
MAVEN USAGE
==============================================================================

If you want to depend on a released version of Cling, add this repository to
your pom.xml:

   <repository>
       <id>4thline-repo</id>
       <url>http://4thline.org/m2</url>
       <snapshots>
           <enabled>false</enabled>
       </snapshots>
   </repository>

Add this dependency to your pom.xml:

    <dependency>
        <groupId>org.fourthline.cling</groupId>
        <artifactId>cling-core</artifactId>
        <version>${project.version}</version>
    </dependency>

The current unstable version is 2.0-SNAPSHOT.

For Cling on Android with Maven, please see the pom.xml example in the
demo/android/ folder.

==============================================================================
BUILDING CLING
==============================================================================

To build the source of 2.0-SNAPSHOT, clone it with:

    git clone https://github.com/4thline/cling.git'

Run "mvn install" to build the JAR files and store them in your local repo.

==============================================================================
DEPENDENCIES
==============================================================================

Required dependencies of Cling Core (included with this distribution):

    +- org.fourthline.cling:cling-core:jar:2.0-SNAPSHOT
       +- org.seamless:seamless-util:jar:1.0-alpha2
       +- org.seamless:seamless-http:jar:1.0-alpha2
       \- org.seamless:seamless-xml:jar:1.0-alpha2

Additional dependencies of Cling Core on Android (not included):

    +- org.eclipse.jetty:jetty-server:jar:8.1.8.v20121106
    |  +- org.eclipse.jetty.orbit:javax.servlet:jar:3.0.0.v201112011016
    |  +- org.eclipse.jetty:jetty-continuation:jar:8.1.8.v20121106
    |  \- org.eclipse.jetty:jetty-http:jar:8.1.8.v20121106
    |     \- org.eclipse.jetty:jetty-io:jar:8.1.8.v20121106
    |        \- org.eclipse.jetty:jetty-util:jar:8.1.8.v20121106
    +- org.eclipse.jetty:jetty-servlet:jar:8.1.8.v20121106
    |  \- org.eclipse.jetty:jetty-security:jar:8.1.8.v20121106
    +- org.eclipse.jetty:jetty-client:jar:8.1.8.v20121106
    +- org.slf4j:slf4j-jdk14:jar:1.6.1  (or any other SLF4J implementation)
       \- org.slf4j:slf4j-api:jar:1.6.1

If you need the fixed Android java.util.logging Handler:

    +- org.seamless:seamless-android:jar:1.0-alpha2
       \- android.support:compatibility-v13:jar:10

WARNING: Jetty JAR files each contain an 'about.html' file, you will get
an error when trying to package your application with APK. Use the Android
Maven plugin and set the "extractDuplicates" option or repackage the Jetty
JAR files and remove 'about.html'.

==============================================================================

Feedback, bug reports: http://4thline.org/projects/mailinglists.html

Copyright 2013, 4th Line GmbH, Switzerland, http://4thline.com/

You may at your option receive a license to this program under EITHER
the terms of the GNU Lesser General Public License (LGPL) OR the
Common Development and Distribution License (CDDL), see LICENSE.txt.
