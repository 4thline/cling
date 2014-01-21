Read the README.txt
=====================

https://github.com/4thline/cling/blob/master/distribution/src/dist/README.txt

Building Cling
---------------------

* Install Maven 3.1.1 or newer.

* Install the Android SDK and set the ANDROID_HOME environment variable to the SDK install directory.

* Copy the Android SDK's Maven extras `$ANDROID_HOME/extras/android/m2repository` to your local Maven repository `~/.m2`.

* Clone the Cling source:

````
git clone https://github.com/4thline/cling.git
````

* Change into the `cling/` directory.

* Install everything into your local `~/.m2` Maven repository (this will take a few minutes if all dependencies have to be downloaded for the first time).

````
mvn clean install
````

* Use Cling in your pom.xml with:

````
<dependencies>
  <dependency>
    <groupId>org.fourthline.cling</groupId>
    <artifactId>cling-core</artifactId>
    <version>2.0-SNAPSHOT</version>
  </dependency>
</dependencies>
````
