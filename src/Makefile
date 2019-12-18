# Copyright 2018, pEp Foundation
# This file is part of pEp JNI Adapter
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

include ../Makefile.conf

ifndef JAVA_HOME
    $(error JAVA_HOME is not set!)
endif

JAVA_BIN=$(JAVA_HOME)/bin

# Old versions of a Java distribution have a `javah` binary, new versions do not. This checks whether or not `javah` can be found in the Java distribution found in the directory `$JAVA_HOME`.
NOT_USED:=$(shell which $(JAVA_HOME)/bin/javah)
ifeq ($(.SHELLSTATUS),0)
    OLD_JAVA=placeholder
endif

DEST=out

AR_CXX=libpEpJNI.a
LIB_JAVA=pEp.jar

ifeq ($(BUILD_FOR),Linux)
    LIB_CXX=libpEpJNI.so
else ifeq ($(BUILD_FOR),Darwin)
    LIB_CXX=libpEpJNI.dylib
else
    $(error I dont know how to build for $(BUILD_FOR).)
endif

SRC_JAVA=$(shell find ./foundation/pEp/jniadapter/ -name '*.java')

SRC_CXX:=$(shell ls -1 | grep -E '.cc$|.hh$|.h$')
OBJS:=$(shell ls -1 | grep -E '.cc' | sed 's/\.cc/.o/g')


#JNI_GEN_H:=$(shell ls -1 |grep .cc | sed 's/\.cc/.h/g' | sed -e ':a' -e 'N' -e '$!ba' -e 's/\n/ /g')
JNI_GEN_H=foundation_pEp_jniadapter_AbstractEngine.h foundation_pEp_jniadapter_Engine.h foundation_pEp_jniadapter_Message.h

.PHONY: all status_list codegen jnigen compile_cxx compile_java clean

# BUILD ALL
all:
	$(MAKE) codegen
	$(MAKE) compile_java

# COMPILE JAVA
compile_java: compile_cxx $(LIB_JAVA)

$(LIB_JAVA): $(SRC_JAVA) $(SRC_CXX)
	$(JAVA_BIN)/javac ./foundation/pEp/jniadapter/*.java
	$(JAVA_BIN)/jar cf $@ ./foundation/pEp/jniadapter/*.class


# COMPILE C++
compile_cxx: jnigen $(LIB_CXX)

$(LIB_CXX): $(AR_CXX)
	$(CXX) *.o $(LDFLAGS) $(LDLIBS) -o $@

$(AR_CXX): $(OBJS)
	ar -r $@ *.o

%.o: %.cc %.h
	$(CXX) $(CXXFLAGS) -c $< -o $@


# GENERATE JNI HEADERS
jnigen: codegen $(JNI_GEN_H)

$(JNI_GEN_H): foundation_pEp_jniadapter_%.h: foundation/pEp/jniadapter/%.java
ifdef OLD_JAVA
	$(JAVA_BIN)/javah $(subst /,.,$(subst .java,,$<))
else
	$(JAVA_BIN)/javac -h . $<
endif



# CODEGEN
codegen: status_list foundation/pEp/jniadapter/pEpException.java foundation/pEp/jniadapter/Message.java foundation/pEp/jniadapter/Engine.java foundation_pEp_jniadapter_Message.cc foundation_pEp_jniadapter_Engine.cc throw_pEp_exception.cc throw_pEp_exception.hh

foundation/pEp/jniadapter/pEpException.java: pEp.yml2 gen_java_exceptions.ysl2 types_java.ysl2
	$(YML2_PROC) -y gen_java_exceptions.ysl2 $< -o $@

foundation/pEp/jniadapter/Message.java: pEp.yml2 gen_java_Message.ysl2 types_java.ysl2
	$(YML2_PROC) -y gen_java_Message.ysl2 $<

foundation/pEp/jniadapter/Engine.java: pEp.yml2 gen_java_Engine.ysl2 types_java.ysl2
	$(YML2_PROC) -y gen_java_Engine.ysl2 $<

foundation_pEp_jniadapter_Message.cc: pEp.yml2 gen_cpp_Message.ysl2 types_c.ysl2
	$(YML2_PROC) -y gen_cpp_Message.ysl2 $<

foundation_pEp_jniadapter_Engine.cc: pEp.yml2 gen_cpp_Engine.ysl2 types_c.ysl2
	$(YML2_PROC) -y gen_cpp_Engine.ysl2 $<

throw_pEp_exception.cc throw_pEp_exception.hh: pEp.yml2 gen_throw_pEp_exception.ysl2 textutils.ysl2
	$(YML2_PROC) -y gen_throw_pEp_exception.ysl2 $< -o throw_pEp_exception.cc


# GENERATE STATUS LIST
status_list: status_list.yml2

status_list.yml2: pEp.yml2 $(ENGINE_INC_PATH)/pEp/pEpEngine.h
	bash ../utils/extract_pEp_status_codes_from_engine.sh $(ENGINE_INC_PATH)/pEp/pEpEngine.h $@


clean:
	rm -f $(LIB_JAVA) $(AR_CXX) $(LIB_CXX)
	rm -f *.o
	rm -f *.class
	rm -f *.xml *.xsl
	rm -f foundation_pEp_jniadapter_*.h
	rm -f foundation/pEp/jniadapter/*.class
	rm -f foundation/pEp/jniadapter/pEp*.java
	rm -f foundation/pEp/jniadapter/Engine.java
	rm -f foundation/pEp/jniadapter/Message.java
	rm -f foundation/pEp/jniadapter/Color.java
	rm -f foundation/pEp/jniadapter/DecryptFlags.java
	rm -f foundation/pEp/jniadapter/IdentityFlags.java
	rm -f foundation/pEp/jniadapter/Rating.java
	rm -f foundation/pEp/jniadapter/Status.java
	rm -f foundation/pEp/jniadapter/SyncHandshakeResult.java
	rm -f foundation/pEp/jniadapter/SyncHandshakeSignal.java
	rm -f foundation/pEp/jniadapter/CipherSuite.java
	rm -f throw_pEp_exception.*
	rm -f foundation_pEp_jniadapter_Message.cc foundation_pEp_jniadapter_Engine.cc
	rm -f status_list.yml2
