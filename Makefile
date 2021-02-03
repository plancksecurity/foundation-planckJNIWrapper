# Copyright 2017, pEp Foundation
# This file is part of pEp JNI Adapter
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt


.PHONY: all src test clean doxy-all doxy-cxx doxy-java doxy-clean

all: src

src:
	$(MAKE) -C src

test: src
	$(MAKE) -C test/java/foundation/pEp/jniadapter/test/ compile

clean:
	$(MAKE) -C src clean
	$(MAKE) -C test/java/foundation/pEp/jniadapter/test/ clean clean-pep-home

# Generate API Documentation
doxy-all: doxy-cxx doxy-java

doxy-cxx: src
	cd doc/doxygen; doxygen doxyfile-cxx

doxy-java: src
	cd doc/doxygen; doxygen doxyfile-java

doxy-clean:
	rm -rf doc/doxygen/cxx
	rm -rf doc/doxygen/java
