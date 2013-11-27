This is a large collection of archetypes jars built from MEDvision/archetypes.

It's primary use here is to provide a large, stable set of files to load into unit tests and the like.

Note that the code derived from the openehr reference implementation keeps its own local .adl files,
to make future syncing/merging of the upstream easier.

Note that our private version of this project holds many more archetypes, but for the published
version of medrecord we are constraining it to archetypes published at openehr.org.

Building and running
====================
These libraries are built with maven, and then picked up by the gradle build for the medrecord java
server.

Submodules
----------
If you cloned this repository like so:

    git clone https://github.com/MEDvision/medrecord.git --recursive

You probably know all about submodules. Skip this section.

Medrecord uses [git submodules](http://git-scm.com/book/en/Git-Tools-Submodules). Because the only way we distribute 
medrecord is by letting you clone the git repository, if you use medrecord, you need to use git submodules, too. Git
submodules are powerful, but, if you have not seem them before, they may require some study. They also have some
quirks in old versions of the git client.

By invoking the right git commands, git will recursively clone all the submodules. You can use

    git clone https://github.com/MEDvision/medrecord.git --recursive

or you can use

    git clone git@github.com:MEDvision/medrecord.git
    git submodule update --init --recursive

they mean the same thing. When you are pulling in new code from remote sources like github, you should use

    git pull
    git submodule update --recursive

to also get new code for the submodules.

Prerequisites
-------------
- install the latest stable version of
  [JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).

- install the latest stable version of [maven](http://maven.apache.org/). Make sure maven is using JDK 7.
  The easiest way to do that is to point JAVA_HOME to JDK 7, and add its /bin directory to your PATH.

Building with maven
-------------------
- build the archetype libraries and install them in your local repository

        maven install
  
  you don't need to do this every time you build medrecord; only if a 'git submodule update' indicated
  there's new archetypes available.
