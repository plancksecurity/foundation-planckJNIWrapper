# Copyright 2018, pEp Foundation
# This file is part of pEp JNI Adapter
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

include ../../Makefile.conf

MARKER_DIR=../../build/marker/

PEP_HEADER:=$(shell $(CXX) $(CXXFLAGS) -E -M ../cxx/get_header.cc | grep -oe '[^[:space:]]*pEpEngine\.h' | head -1)

# Every ysl2 file that needs to be "compiled" separately, needs to generate a "marker" file
# The marker serves as the make target.
# If the marker file is older than its corresponding ysl2 file, or not exsiting, the ysl2 file will be "compiled"
# Naming:
# For a ysl2 file called "gen_example_stuff.ysl2", a marker file called "gen_example_stuff.marker" is expected.
YML2_MARKERS= \
	$(MARKER_DIR)/gen_java_Engine.marker \
	$(MARKER_DIR)/gen_java_Message.marker \
	$(MARKER_DIR)/gen_cpp_Engine.marker \
	$(MARKER_DIR)/gen_cpp_Message.marker \
	$(MARKER_DIR)/gen_throw_pEp_exception.marker

# All code genration will be done upon change of these files
YML2_INCLUDES= \
	textutils.ysl2 \
    types_c.ysl2 \
    types_java.ysl2


.PHONY: all codegen gen-status-codes create-dirs remove-dirs clean

all: codegen

# ------------- YML2 CodeGen --------------
codegen: create-dirs gen-status-codes $(YML2_MARKERS)

$(YML2_MARKERS): $(MARKER_DIR)/%.marker : %.ysl2 pEp.yml2 $(YML2_INCLUDES)
	$(YML2_PROC) -y $< pEp.yml2

gen-status-codes: status_list.yml2

status_list.yml2: pEp.yml2
	bash ../../utils/extract_pEp_status_codes_from_engine.sh "$(PEP_HEADER)" $@


# ------------- Housekeeping ---------------
create-dirs:
	mkdir -p $(MARKER_DIR)

#rm -rf is too dangerous for vars
remove-dirs:
	rm -rf ../../build/marker

clean: remove-dirs
	rm -f status_list.yml2

