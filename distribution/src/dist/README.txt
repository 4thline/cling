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

Explore the Android demo applications in the demo/android/ folder.

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

For Cling on Android with Maven, please see the pom.xml example in the
demo/android/ folder.

==============================================================================
BUILDING CLING
==============================================================================

See https://github.com/4thline/cling

==============================================================================
DEPENDENCIES
==============================================================================

Required dependencies of Cling Core (included with this distribution):

    +- org.fourthline.cling:cling-core:jar:2.1.2-SNAPSHOT
       +- org.seamless:seamless-util:jar:1.1.1
       +- org.seamless:seamless-http:jar:1.1.1
       \- org.seamless:seamless-xml:jar:1.1.1

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

WARNING: Jetty JAR files each contain an 'about.html' file, you will get
an error when trying to package your application with APK. Use the Android
Maven plugin and set the "extractDuplicates" option or repackage the Jetty
JAR files and remove 'about.html'.

==============================================================================

Copyright 2017, 4th Line GmbH, Switzerland

For licensing questions, please contact: license(at)4thline.com

You may at your option receive a license to this program under EITHER
the terms of the GNU Lesser General Public License (LGPL) OR the
Common Development and Distribution License (CDDL), see LICENSE.txt.
