# Copyright 2018, pEp Foundation
# This file is part of pEp JNI Adapter
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

include ../Makefile.conf

# Names of the java and c++ libs to be built
LIB_JAVA_NAME=pEp.jar
LIB_CXX_NAME=libpEpJNI
DIST_DIR=../dist/


LIB_CXX_STATIC_NAME=$(addsuffix .a,$(LIB_CXX_NAME))
ifeq ($(PLATFORM),linux)
    LIB_CXX_DYN_NAME=$(addsuffix .so,$(LIB_CXX_NAME))
else ifeq ($(PLATFORM),darwin)
    LIB_CXX_DYN_NAME=$(addsuffix .dylib,$(LIB_CXX_NAME))
else
    $(error I dont know how to build for $(PLATFORM).)
endif

LIB_JAVA=$(DIST_DIR)/$(LIB_JAVA_NAME)
LIB_CXX_STATIC=$(DIST_DIR)/$(LIB_CXX_STATIC_NAME)
LIB_CXX_DYN=$(DIST_DIR)/$(LIB_CXX_DYN_NAME)

$(info ------------------ DIST FILES ---------------------)
$(info LIB_JAVA:        $(LIB_JAVA))
$(info LIB_CXX_STATIC:  $(LIB_CXX_STATIC))
$(info LIB_CXX_DYN:     $(LIB_CXX_DYN))
$(info ------------------ BUILD INFO ---------------------)
$(info PLATFORM:        $(PLATFORM))
$(info DEBUG:           $(DEBUG))
$(info CXX:             $(CXX))
$(info JAVA_HOME:       $(JAVA_HOME))
$(info USE_JAVAH:       $(USE_JAVAH))
$(info YML2_PATH:       $(YML2_PATH))
$(info CXXFLAGS:        $(CXXFLAGS))
$(info LDFLAGS:         $(LDFLAGS))
$(info LDLIBS:          $(LDLIBS))
$(info ---------------------------------------------------)

# Dirs
# relative to "src/"
BUILD_ROOT=../build/
CXX_DIR=cxx/
JAVA_DIR=java/
JAVA_PKG_BASENAME=foundation/pEp/jniadapter/

JAVA_PKG_ROOT=$(JAVA_DIR)$(JAVA_PKG_BASENAME)
JAVA_BUILD_ROOT=$(BUILD_ROOT)$(JAVA_DIR)
OBJ_DIR=$(BUILD_ROOT)$(CXX_DIR)


# for "make clean" only
GENERATED_JAVA=\
	$(JAVA_PKG_ROOT)/Engine.java \
	$(JAVA_PKG_ROOT)/Message.java \
	$(JAVA_PKG_ROOT)/CipherSuite.java \
	$(JAVA_PKG_ROOT)/Color.java \
	$(JAVA_PKG_ROOT)/DecryptFlags.java \
	$(JAVA_PKG_ROOT)/IdentityFlags.java \
	$(JAVA_PKG_ROOT)/Rating.java \
	$(JAVA_PKG_ROOT)/SyncHandshakeResult.java \
	$(JAVA_PKG_ROOT)/SyncHandshakeSignal.java \
	$(JAVA_PKG_ROOT)/PassphraseType.java \
	$(JAVA_PKG_ROOT)/interfaces/EngineInterface.java \
 	$(JAVA_PKG_ROOT)/interfaces/MessageInterface.java \
 	$(JAVA_PKG_ROOT)/exceptions/*.java

# files to compile which will be be generated
GENERATED_CC=\
	$(CXX_DIR)/foundation_pEp_jniadapter_Engine.cc \
	$(CXX_DIR)/foundation_pEp_jniadapter_Message.cc \
	$(CXX_DIR)/throw_pEp_exception.cc

# for "make clean" only
GENERATED_HH=\
	$(CXX_DIR)/throw_pEp_exception.hh

# Generated JNI headers
JNI_GENERATED_HH=\
	$(CXX_DIR)/foundation_pEp_jniadapter_AbstractEngine.h \
    $(CXX_DIR)/foundation_pEp_jniadapter__Blob.h \
    $(CXX_DIR)/foundation_pEp_jniadapter_Engine.h \
    $(CXX_DIR)/foundation_pEp_jniadapter_Identity.h \
    $(CXX_DIR)/foundation_pEp_jniadapter_Message.h

# Auto dependencies using gcc/clang
CXXFLAGS+= -MMD -MP

SOURCES=$(wildcard $(CXX_DIR)/*.cc)
SOURCES+=$(GENERATED_CC)
tmp=$(SOURCES:%.cc=%.o)
OBJECTS=$(addprefix $(BUILD_ROOT), $(tmp))
DEPENDS=$(OBJECTS:%.o=%.d)

-include $(DEPENDS)
VPATH = $(CXX_DIR)/

.PHONY: all lib-java lib-cxx compile-java compile-cxx gen-jni-headers codegen clean create-dirs remove-dirs install uninstall

$(info SOURCES: $(SOURCES))
$(info OBJECTS: $(OBJECTS))
$(info DEPENDS: $(DEPENDS))

.DEFAULT_GOAL := all
all: lib-java lib-cxx

# Build only java lib
lib-java: $(LIB_JAVA)

# Build only c++ static/shared lib
lib-cxx: $(LIB_CXX_DYN) $(LIB_CXX_STATIC)

# ---------------- Link -----------------
$(LIB_JAVA): compile-java
	$(JAVA_BIN_DIR)/jar cf $@ -C $(JAVA_BUILD_ROOT) foundation

$(LIB_CXX_DYN): compile-cxx
	$(CXX) $(OBJ_DIR)*.o $(LDFLAGS) $(LDLIBS) -o $@

$(LIB_CXX_STATIC): compile-cxx
	$(AR) -r $@ $(OBJ_DIR)*.o

# -------------- Compile -----------------
compile-java: create-dirs codegen gen-jni-headers
	cd $(JAVA_DIR);$(JAVA_BIN_DIR)/$(JAVAC_CMD) -d ../$(JAVA_BUILD_ROOT) $(JAVA_PKG_BASENAME)/*.java
	cd $(JAVA_DIR);$(JAVA_BIN_DIR)/$(JAVAC_CMD) -d ../$(JAVA_BUILD_ROOT) $(JAVA_PKG_BASENAME)/exceptions/*.java
	cd $(JAVA_DIR);$(JAVA_BIN_DIR)/$(JAVAC_CMD) -d ../$(JAVA_BUILD_ROOT) $(JAVA_PKG_BASENAME)/interfaces/*.java

compile-cxx: create-dirs gen-jni-headers $(OBJECTS)

$(OBJECTS) : $(BUILD_ROOT)%.o : %.cc
	$(CXX) $(CXXFLAGS) -c $< -o $@

# --------- Generate JNI headers ----------
gen-jni-headers: codegen $(JNI_GENERATED_HH)

# "new" javac -h accepts list of java files
# While "old" (Java 8) javah needs a list of java classes (e.g. foundation.pEp.jniadapter.Message)
$(JNI_GENERATED_HH):
ifeq ($(USE_JAVAH),1)
	cd $(JAVA_DIR)/;$(JAVA_BIN_DIR)/$(JAVAH_CMD) -d ../$(CXX_DIR) $(subst ${ },${space}\${\n},$(subst /,.,$(patsubst %.java,%, $(subst $(JAVA_DIR),,$(sort $(wildcard $(JAVA_DIR)$(JAVA_PKG_BASENAME)*.java))))))
else
	cd $(JAVA_DIR);$(JAVA_BIN_DIR)/$(JAVAC_CMD) -d ../$(JAVA_BUILD_ROOT) -h ../$(CXX_DIR)/ $(JAVA_PKG_BASENAME)/*.java
endif


# ------------- YML2 CodeGen --------------
codegen:
	$(MAKE) -C codegen

# ------------- Housekeeping ---------------
create-dirs:
	$(MAKE) -C codegen create-dirs
	mkdir -p $(JAVA_BUILD_ROOT)/$(JAVA_PKG_BASENAME)
	mkdir -p $(JAVA_BUILD_ROOT)/$(JAVA_PKG_BASENAME)/exceptions
	mkdir -p $(OBJ_DIR)
	mkdir -p $(DIST_DIR)
	mkdir -p $(JAVA_PKG_ROOT)/exceptions

#rm -rf is too dangerous for vars
remove-dirs:
	$(MAKE) -C codegen remove-dirs
	rm -rf ../build
	rm -rf ../dist

clean: remove-dirs
	$(MAKE) -C codegen clean
	rm -f $(GENERATED_JAVA)
	rm -f $(GENERATED_CC)
	rm -f $(GENERATED_HH)
	rm -f $(JNI_GENERATED_HH)
	rm -f $(DEPENDS)
	rm -f $(OBJECTS)

install: $(LIB_JAVA) $(LIB_CXX_STATIC) $(LIB_CXX_DYN)
	mkdir -p $(PREFIX)/lib
	cp -v $(LIB_JAVA) 		$(PREFIX)/lib/
	cp -v $(LIB_CXX_STATIC) $(PREFIX)/lib/
	cp -v $(LIB_CXX_DYN) 	$(PREFIX)/lib/

uninstall:
	cd $(PREFIX)/lib && rm -vf $(LIB_JAVA_NAME)
	cd $(PREFIX)/lib && rm -vf $(LIB_CXX_STATIC_NAME)
	cd $(PREFIX)/lib && rm -vf $(LIB_CXX_DYN_NAME)

