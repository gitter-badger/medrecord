MEDrecord Java Code
===================

This is the implementation of MEDrecord and its tools.


Building and running
====================

Quickstart
----------

- install all the libs (TODO: pickup from cloudbees/medvision repo) :

  (make sure npm in in your PATH, this is used by web/swagger)

        cd ../../ZorgGemak-Common/libs
        for i in doclet gradle/buildlib java web/swagger
        do
            (cd $i && gradle install)
        done
        cd -

- build it:

        gradle vendor install

  (`vendor` is only required the first time or when some non-gradle vendor libraries are upgraded)


Run the server
--------------

    cd medrecord-server/medrecord-server-server
    gradle tomcatRunWar

Now point your browser to http://localhost:8100/medrecord/apidocs



Generate IDEA project files
---------------------------

- run: `gradle idea`
- remove them: `gradle cleanIdea`
