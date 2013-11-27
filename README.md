MEDrecord
=========
This is the implementation of MEDrecord and its tools. See http://medrecord.medvision360.org/ for more information.

Online demo
-----------
Visit

* http://medrecord.test.medvision360.org/medrecord/

for a live demo version of medrecord. Feel free to experiment with this installation: that's what it's for!


License
=======
This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the 
"License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

* http://creativecommons.org/licenses/by-nc-sa/4.0/


Getting started
===============
Clone the repository using:

    git clone --recursive git@github.com:ZorgGemak/medrecord.git


The java server
---------------
See [java/README.md](java/README.md) for the java server implementation code.


Required archetypes
-------------------
For its integration tests, the java server utilizes a jar containing archetypes from the OpenEHR CKM repository.
See [archetypes/README.md](archetypes/README.md) for how to build and install this jar yourself.
