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
DEPENDENCIES
==============================================================================

Required dependencies of Cling Core (included with this distribution):

- seamless-http.jar
- seamless-util.jar
- seamless-xml.jar

Additional dependencies of Cling Core on Android (not included):

- jetty-server.jar
- jetty-servlet.jar
- jetty-client.jar
- slf4j-api.jar
- slf4j-jdk14.jar (or any other SLF4J implementation)
- seamless-android.jar (only for fixed java.util.logging Handler)

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
