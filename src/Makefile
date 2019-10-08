# Copyright 2018, pEp Foundation
# This file is part of pEp JNI Adapter
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

include Makefile.conf

ifneq ($(wildcard local.conf),)
    $(info ================================================)
    $(info Overrides in `local.conf` are used.)
    $(info ================================================)
endif

ifdef BUILD_CONFIG
    $(info ================================================)
    $(info Overrides in `$(BUILD_CONFIG)` are used.)
    $(info ================================================)
endif

ifndef JAVA_HOME
    $(error JAVA_HOME is not set!)
endif

JP=$(JAVA_HOME)/bin

# Old versions of a Java distribution have a `javah` binary, new versions do not. This checks whether or not `javah` can be found in the Java distribution found in the directory `$JAVA_HOME`.
NOT_USED:=$(shell which $(JAVA_HOME)/bin/javah)
ifeq ($(.SHELLSTATUS),0)
    OLD_JAVA=placeholder
endif

LIBRARY=libpEpJNI.a
JAR=pEp.jar

ifeq ($(BUILD_FOR),Linux)
    SHARED=libpEpJNI.so
else ifeq ($(BUILD_FOR),Darwin)
    SHARED=libpEpJNI.dylib
else
    $(error I don't know how to build for $(BUILD_FOR).)
endif

JAVA_SOURCES=foundation/pEp/jniadapter/AbstractEngine.java \
    foundation/pEp/jniadapter/Blob.java \
    foundation/pEp/jniadapter/CommType.java \
    foundation/pEp/jniadapter/Identity.java \
    foundation/pEp/jniadapter/Pair.java \
    foundation/pEp/jniadapter/Sync.java \
    foundation/pEp/jniadapter/_Blob.java \
    foundation/pEp/jniadapter/_Identity.java \
    foundation/pEp/jniadapter/pEpException.java \
    foundation/pEp/jniadapter/Message.java \
    foundation/pEp/jniadapter/Engine.java \

C_SOURCES=foundation_pEp_jniadapter_Engine.cc \
    foundation_pEp_jniadapter_Engine.h \
    foundation_pEp_jniadapter_Message.cc \
    foundation_pEp_jniadapter_Message.h \
    throw_pEp_exception.cc \
    throw_pEp_exception.hh \
    foundation_pEp_jniadapter_AbstractEngine.h

PEP_HEADER:=$(shell $(CXX) -E -M get_header.cc | grep -oe '[^[:space:]]*pEpEngine\.h' | head -1)


.PHONY: all
all: $(JAR) $(SHARED)

$(JAR): status_list.yml2 $(JAVA_SOURCES) $(C_SOURCES)
	$(JP)/javac foundation/pEp/jniadapter/*.java
	$(JP)/jar cf $@ foundation/pEp/jniadapter/*.class

BLUBB=foundation_pEp_jniadapter_AbstractEngine.h foundation_pEp_jniadapter_Engine.h foundation_pEp_jniadapter_Message.h
$(BLUBB): foundation_pEp_jniadapter_%.h: foundation/pEp/jniadapter/%.java
ifdef OLD_JAVA
	$(JP)/javah $(subst /,.,$(subst .java,,$<))
else
	$(JP)/javac -h . $<
endif

foundation_pEp_jniadapter_AbstractEngine.o: %.o: %.cc %.h throw_pEp_exception.hh jniutils.hh
	$(CXX) $(CXXFLAGS) -c $< -o $@

foundation_pEp_jniadapter_Engine.o foundation_pEp_jniadapter_Message.o: %.o: %.cc %.h
	$(CXX) $(CXXFLAGS) -c $< -o $@

$(LIBRARY): foundation_pEp_jniadapter_AbstractEngine.o foundation_pEp_jniadapter_Engine.o foundation_pEp_jniadapter_Message.o throw_pEp_exception.o jniutils.o basic_api.o
	ar -r $@ *.o

$(SHARED): $(LIBRARY)
	$(CXX) *.o $(LDFLAGS) $(LDLIBS) -o $@

status_list.yml2: pEp.yml2
	bash ../utils/extract_pEp_status_codes_from_engine.sh "$(PEP_HEADER)" $@

foundation/pEp/jniadapter/pEpException.java: pEp.yml2 gen_java_exceptions.ysl2 pEp.yml2
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

throw_pEp_exception.o: throw_pEp_exception.cc throw_pEp_exception.hh

basic_api.o: basic_api.cc jniutils.hh throw_pEp_exception.hh

.PHONY: clean
clean:
	rm -f $(JAR) $(LIBRARY) $(SHARED)
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
