The real README
=====================

https://github.com/4thline/cling/blob/master/distribution/src/dist/README.txt

Building Cling
---------------------

1. Install Maven 3.x.

2. Install the Android SDK and set the ANDROID_HOME environment variable to the SDK install directory.

3. Clone the Cling source:

  git clone https://github.com/4thline/cling.git

4. Change into the `cling/` directory.

5. Install everything into your local `.m2` Maven repository (this will take a few minutes if all dependencies have to be downloaded for the first time).

  mvn clean install

6. Use Cling in your pom.xml with:

  <dependencies>
    <dependency>
        <groupId>org.fourthline.cling</groupId>
        <artifactId>cling-core</artifactId>
        <version>2.0-SNAPSHOT</version>
    </dependency>
  </dependencies>
