MEDrecord Java Code
===================

This is the implementation of MEDrecord and its tools.


Building and running
====================

Quickstart
----------

- install the buildlib (TODO: pickup buildlib from cloudbees repo) :

        cd ../../ZorgGemak-Common/libs/gradle/buildlib
        gradle install
        cd -

- build it:

        gradle vendor install

  (`vendor` is only required the first time or when some non-gradle vendor libraries are upgraded)


Generate IDEA project files
---------------------------

- run: `gradle idea`
- remove them: `gradle cleanIdea`
