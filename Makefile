# Copyright 2017, pEp Foundation
# This file is part of pEp JNI Adapter
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

.PHONY: all compile test clean doc doc-cxx doc-java clean-doc install uninstall

all: compile

compile:
	$(MAKE) -C src

test: compile
	$(MAKE) -C test/java/foundation/pEp/jniadapter/test/ compile


clean-all: clean clean-doc

clean:
	$(MAKE) -C src clean
	$(MAKE) -C test/java/foundation/pEp/jniadapter/test/ clean clean-pep-home

clean-doc:
	rm -rf doc/doxygen/cxx
	rm -rf doc/doxygen/java

 # Generate API Documentation
doc: doc-cxx doc-java

dox-cxx: compile
	cd doc/doxygen; doxygen doxyfile-cxx

doc-java: compile
	cd doc/doxygen; doxygen doxyfile-java


install:
	$(MAKE) -C src install

uninstall:
	$(MAKE) -C src uninstall

