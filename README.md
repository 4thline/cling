Read the README.txt
=====================

https://github.com/4thline/cling/blob/master/distribution/src/dist/README.txt

About this build
----------------

This cling branch will merge a various PRs intended for the central cling repository at https://github.com/4thline/cling.

Included:

* https://github.com/4thline/cling/pull/105
* https://github.com/4thline/cling/pull/106
* https://github.com/4thline/cling/pull/108
* https://github.com/4thline/cling/pull/109
* https://github.com/4thline/cling/pull/110
* https://github.com/4thline/cling/pull/114
* https://github.com/4thline/cling/pull/116

Also provided is a *temporary* maven repository. Sample inclusion for gradle:

````
repositories {
    mavenCentral()
    maven {
        url 'https://raw.github.com/ened/cling/mvn-repo'
    }
}

dependencies {
    compile('org.fourthline.cling:cling-core:2.0.1-SR-SNAPSHOT')
    compile('org.fourthline.cling:cling-support:2.0.1-SR-SNAPSHOT')
}
````

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
    <version>2.0.1</version>
  </dependency>
</dependencies>
````
