# Copyright 2018, pEp Foundation
# This file is part of pEp JNI Adapter
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

include ../Makefile.conf

# Names of the java and c++ libs to be built
LIB_JAVA=pEp.jar
LIB_CXX_NAME=libpEpJNI
LIB_CXX_STATIC=$(addsuffix .a,$(LIB_CXX_NAME))

ifeq ($(PLATFORM),linux)
    LIB_CXX_DYN=$(addsuffix .so,$(LIB_CXX_NAME))
else ifeq ($(PLATFORM),darwin)
    LIB_CXX_DYN=$(addsuffix .dylib,$(LIB_CXX_NAME))
else
    $(error I dont know how to build for $(PLATFORM).)
endif

$(info ------------------ DIST FILES ---------------------)
$(info LIB_JAVA:        $(LIB_JAVA))
$(info LIB_CXX_STATIC:  $(LIB_CXX_STATIC))
$(info LIB_CXX_DYN:     $(LIB_CXX_DYN))
$(info ------------------ BUILD INFO ---------------------)
$(info PLATFORM:        $(PLATFORM))
$(info DEBUG:           $(DEBUG))
$(info CXX:             $(CXX))
$(info JAVA_HOME:       $(JAVA_HOME))
$(info ENGINE_LIB_PATH: $(ENGINE_LIB_PATH))
$(info ENGINE_INC_PATH: $(ENGINE_INC_PATH))
$(info AD_LIB_PATH:     $(AD_LIB_PATH))
$(info AD_INC_PATH:     $(AD_INC_PATH))
$(info YML2_PATH:       $(YML2_PATH))
$(info CXXFLAGS:        $(CXXFLAGS))
$(info LDFLAGS:         $(LDFLAGS))
$(info LDLIBS:          $(LDLIBS))
$(info ---------------------------------------------------)

# Dirs
# relative to "src/"
JAVA_PKG_ROOT=foundation/pEp/jniadapter/
JAVA_BUILD_ROOT=../build/java/
# Separate dir for derived objects DOES NOT WORK YET :/ (has to be .)
OBJ_DIR=.
DIST_DIR=../dist/

PEP_HEADER:=$(shell $(CXX) $(CXXFLAGS) -E -M get_header.cc | grep -oe '[^[:space:]]*pEpEngine\.h' | head -1)

# Every ysl2 file that need to be "compiled" separately, needs to generate a "marker" file
# The marker serves as the make target.
# If the marker file is older than its corresponding ysl2 file, or not exsiting the ysl2 file will be "compiled"
# Naming:
# For a ysl2 file called "gen_example_stuff.ysl2", a marker file called "gen_example_stuff.marker" is expected.
YML2_MARKERS= \
	gen_java_Engine.marker \
	gen_java_Message.marker \
	gen_cpp_Engine.marker \
	gen_cpp_Message.marker \
	gen_throw_pEp_exception.marker

# All code genration will be done upon change of these files
YML2_INCLUDES= \
	textutils.ysl2 \
    types_c.ysl2 \
    types_java.ysl2

# for "make clean" only
GENERATED_JAVA=\
	$(JAVA_PKG_ROOT)/Engine.java \
	$(JAVA_PKG_ROOT)/Message.java \
	$(JAVA_PKG_ROOT)/CipherSuite.java \
	$(JAVA_PKG_ROOT)/Color.java \
	$(JAVA_PKG_ROOT)/DecryptFlags.java \
	$(JAVA_PKG_ROOT)/EngineInterface.java \
	$(JAVA_PKG_ROOT)/IdentityFlags.java \
 	$(JAVA_PKG_ROOT)/MessageInterface.java \
	$(JAVA_PKG_ROOT)/Rating.java \
	$(JAVA_PKG_ROOT)/SyncHandshakeResult.java \
	$(JAVA_PKG_ROOT)/SyncHandshakeSignal.java

# Used to determine files to compile which are being generated
GENERATED_CC=\
	foundation_pEp_jniadapter_Engine.cc \
	foundation_pEp_jniadapter_Message.cc \
	throw_pEp_exception.cc

# for "make clean" only
GENERATED_HH=\
	throw_pEp_exception.hh

# Generated JNI headers (javac -h)
JNI_GENERATED_HH=\
	foundation_pEp_jniadapter_AbstractEngine.h \
    foundation_pEp_jniadapter__Blob.h \
    foundation_pEp_jniadapter_Engine.h \
    foundation_pEp_jniadapter_Identity.h \
    foundation_pEp_jniadapter_Message.h

# Derive Java sources that will cause generated headers (for incremental build)
helper=$(subst _,/,$(JNI_GENERATED_HH))
JNI_GENERATING_JAVA=$(subst .h,.java,$(helper))

# Auto dependencies using gcc/clang
CXXFLAGS+= -MMD -MP

SOURCES=$(wildcard *.cc)
SOURCES+=$(GENERATED_CC)
OBJECTS=$(SOURCES:.cc=.o)
DEPENDS=$(OBJECTS:.o=.d)

.PHONY: all lib-java lib-cxx compile-java compile-cxx gen-jni-headers codegen gen-status-codes clean create-dirs remove-dirs

all: lib-java lib-cxx

# Build only java lib
lib-java: $(LIB_JAVA)

# Build only c++ static/shared lib
lib-cxx: $(LIB_CXX_DYN) $(LIB_CXX_STATIC)

# ---------------- Link -----------------
$(LIB_JAVA): compile-java
	$(JAVA_BIN_DIR)/jar cf $(DIST_DIR)/$@ -C $(JAVA_BUILD_ROOT) foundation

$(LIB_CXX_DYN): compile-cxx
	$(CXX) $(OBJ_DIR)/*.o $(LDFLAGS) $(LDLIBS) -o $(DIST_DIR)/$@

$(LIB_CXX_STATIC): compile-cxx
	$(AR) -r $(DIST_DIR)/$@ $(OBJ_DIR)/*.o


# -------------- Compile -----------------
compile-java: create-dirs codegen
	$(JAVA_BIN_DIR)/javac -d $(JAVA_BUILD_ROOT) $(JAVA_PKG_ROOT)/*.java
	$(JAVA_BIN_DIR)/javac -d $(JAVA_BUILD_ROOT) $(JAVA_PKG_ROOT)/exceptions/*.java


# Compile C++ using implicit rules
-include $(DEPENDS)


# --------- Generate JNI headers ----------
gen-jni-headers: codegen $(JNI_GENERATED_HH)

$(JNI_GENERATED_HH): $(JNI_GENERATING_JAVA)
ifdef OLD_JAVA
	$(JAVA_BIN_DIR)/javah $(subst /,.,$(subst .java,,$<))
else
	$(JAVA_BIN_DIR)/javac -h . $(JAVA_PKG_ROOT)/*.java
endif
	mv $(JAVA_PKG_ROOT)/*.class $(JAVA_BUILD_ROOT)/$(JAVA_PKG_ROOT)/


# ------------- YML2 CodeGen --------------
codegen: gen-status-codes $(YML2_MARKERS)

$(YML2_MARKERS): %.marker : %.ysl2 pEp.yml2 $(YML2_INCLUDES)
	$(YML2_PROC) -y $< pEp.yml2

gen-status-codes: status_list.yml2
status_list.yml2: pEp.yml2
	bash ../utils/extract_pEp_status_codes_from_engine.sh "$(PEP_HEADER)" $@


# ------------- Housekeeping ---------------
create-dirs:
	mkdir -p $(JAVA_BUILD_ROOT)/$(JAVA_PKG_ROOT)
	mkdir -p $(JAVA_BUILD_ROOT)/$(JAVA_PKG_ROOT)/exceptions
	mkdir -p $(OBJ_DIR)
	mkdir -p $(DIST_DIR)

remove-dirs:
	rm -rf ../build
	rm -rf ../dist

clean: remove-dirs
	rm -f $(GENERATED_JAVA)
	rm -f $(GENERATED_CC)
	rm -f $(GENERATED_HH)
	rm -f $(JNI_GENERATED_HH)
	rm -f $(YML2_MARKERS)
	rm -f status_list.yml2

