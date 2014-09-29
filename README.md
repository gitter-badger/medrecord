MEDrecord
=========
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/MEDvision/medrecord?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
This is the implementation of MEDrecord and its tools. 

Useful links
------------
* Website: https://www.medrecord.nl/en
* Online demo: http://medrecord.test.medvision360.org/medrecord/ (feel free to experiment here!)
* Group for discussing the implementation: https://groups.google.com/a/medrecord.nl/d/forum/dev
* Overview documentation: https://zorggemak.atlassian.net/wiki/display/DOC/MEDrecord
* Javadocs for the Java client: http://repo.medvision360.org/javadoc/medrecord-server-client-jee


License
=======
This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the 
"License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

* http://creativecommons.org/licenses/by-nc-sa/4.0/


Getting started
===============
Clone the repository using:

    git clone --recursive https://github.com:MEDvision/medrecord.git


The java server
---------------
See [java/README.md](java/README.md) for the java server implementation code.


Required archetypes
-------------------
For its integration tests, the java server utilizes a jar containing archetypes from the OpenEHR CKM repository.
See [archetypes/README.md](archetypes/README.md) for how to build and install this jar yourself.


The java client
---------------
The primary way for interacting with MEDrecord is through its `v2` REST API. That API is built with
[restlet](http://restlet.org/). It's designed to be easy to use from any client, including from javascript.

We have special support for java-based projects: thanks to some of our own nifty extensions built on top of restlet, 
we also auto-generate a restlet client library in java.

Because this client library shares API definition code with the server, it is always in sync with the server API. If 
you're talking to the MEDrecord API from java or android, we highly recommend using this client library rather than
creating your own.

You can see an example of this java client in use in the [cliclient](java/medrecord-tools/medrecord-tools-cliclient) 
module. For example, its
[RemoteArchetypeStore](java/medrecord-tools/medrecord-tools-cliclient/src/main/java/com/medvision360/medrecord/tools/cliclient/RemoteArchetypeStore.java#L47)
shows how easy it is to work with the
[/v2/archetype](http://medrecord.test.medvision360.org/medrecord/v2/apidocs/#!/com_medvision360_medrecord_api_archetype)
API using the java client.

Full javadocs are available at
* http://repo.medvision360.org/javadoc/medrecord-server-client-jee/

Prebuilt versions of the java client are available from our release maven repository at
http://repo.medvision360.org/release/ .

To use our repository with maven, add it to your `pom.xml`:

    <project xmlns="http://maven.apache.org/POM/4.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                          http://maven.apache.org/xsd/maven-4.0.0.xsd">
      ...
      <repositories>
        <repository>
          <releases>
            <enabled>false</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>interval:600</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
          </snapshots>
          <id>mvSnapshots</id>
          <name>MEDvision Snapshots</name>
          <url>http://repo.medvision360.org/snapshot/</url>
          <layout>default</layout>
        </repository>
        <repository>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
          </releases>
          <snapshots>
            <enabled>false</enabled>
            <updatePolicy>interval:600</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
          </snapshots>
          <id>mvReleases</id>
          <name>MEDvision Releases</name>
          <url>http://repo.medvision360.org/release/</url>
          <layout>default</layout>
        </repository>
      </repositories>
      ...
    </project>

After that, you can declare a dependency like so:

    <dependency>
      <groupId>com.medvision360.medrecord</groupId>
      <artifactId>medrecord-server-client-jee</artifactId>
      <version>2.0.0.20</version>
    </dependency>

(Make sure to bump the version to the latest available release).

Of course in gradle it looks similar, though we recommend specifying a version range of 2.+:

    ...
    repositories {
        maven {
            url "http://repo.medvision360.org/release/"
        }
        maven {
            url "http://repo.medvision360.org/snapshot/"
        }
        ...
    }
    ...
    dependencies {
        compile "com.medvision360.medrecord:medrecord-server-client-jee:2.+"
        ...
    }

The client library depends on restlet. To allow using it with either the jee or android versions of restlet, 
we do not specify that dependency for you, and you need to add it yourself. Restlet 2.2M1 or later is required.

Similarly, while the client code itself does not do any logging, restlet does. We prefer to configure restlet to log 
via slf4j to logback, see
[ArchetypeUploader](java/medrecord-tools/medrecord-tools-cliclient/src/main/java/com/medvision360/medrecord/tools/cliclient/ArchetypeUploader.java#L46)
for an example. We recommend you do the same, which means also adding dependencies on slf4j and logback.

Putting all this together, using gradle as an example, you should end up with something like

    repositories {
        maven {
            url "http://repo.medvision360.org/release/"
        }
        maven {
            url "http://repo.medvision360.org/snapshot/"
        }

        ...
        
        maven {
          url 'http://maven.restlet.org'
        }
    }
    
    dependencies {
        ...
        compile "com.medvision360.medrecord:medrecord-server-client-jee:${ext.version.medrecord}"
        
        compile "org.restlet.jee:org.restlet.ext.httpclient:${ext.version.restlet}"
        compile "org.restlet.jee:org.restlet.ext.slf4j:${ext.version.restlet}"
    
        compile "org.slf4j:slf4j-api:${ext.version.slf4j}"
        compile "ch.qos.logback:logback-classic:${ext.version.logback}"
        compile "org.slf4j:jcl-over-slf4j:${ext.version.slf4j}"
        compile "org.slf4j:log4j-over-slf4j:${ext.version.slf4j}"
        compile "org.slf4j:jul-to-slf4j:${ext.version.slf4j}"
    }
    
    ext {
        version = [
                medrecord           : '2.+',
                logback             : '1.0.13',
                restlet             : '2.2-M1',
                slf4j               : '1.7.5',
        ]
    }
