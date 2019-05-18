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

JAVA_SOURCES=pEp/jniadapter/AbstractEngine.java \
    pEp/jniadapter/Blob.java \
    pEp/jniadapter/CommType.java \
    pEp/jniadapter/Identity.java \
    pEp/jniadapter/Pair.java \
    pEp/jniadapter/Sync.java \
    pEp/jniadapter/_Blob.java \
    pEp/jniadapter/_Identity.java \
    pEp/jniadapter/pEpException.java \
    pEp/jniadapter/Message.java \
    pEp/jniadapter/Engine.java \

C_SOURCES=pEp_jniadapter_Engine.cc \
    pEp_jniadapter_Engine.h \
    pEp_jniadapter_Message.cc \
    pEp_jniadapter_Message.h \
    throw_pEp_exception.cc \
    throw_pEp_exception.hh \
    pEp_jniadapter_AbstractEngine.h


.PHONY: all
all: $(JAR) $(SHARED)

$(JAR): $(JAVA_SOURCES) $(C_SOURCES)
	$(JP)/javac pEp/jniadapter/*.java
	$(JP)/jar cf $@ pEp/jniadapter/*.class

BLUBB=pEp_jniadapter_AbstractEngine.h pEp_jniadapter_Engine.h pEp_jniadapter_Message.h
$(BLUBB): pEp_jniadapter_%.h: pEp/jniadapter/%.java
ifdef OLD_JAVA
	$(JP)/javah $(subst /,.,$(subst .java,,$<))
else
	$(JP)/javac -h . $<
endif

pEp_jniadapter_AbstractEngine.o: %.o: %.cc %.h throw_pEp_exception.hh jniutils.hh
	$(CXX) $(CXXFLAGS) -c $< -o $@

pEp_jniadapter_Engine.o pEp_jniadapter_Message.o: %.o: %.cc %.h
	$(CXX) $(CXXFLAGS) -c $< -o $@

$(LIBRARY): pEp_jniadapter_AbstractEngine.o pEp_jniadapter_Engine.o pEp_jniadapter_Message.o throw_pEp_exception.o jniutils.o basic_api.o
	ar -r $@ *.o

$(SHARED): $(LIBRARY)
	$(CXX) *.o $(LDFLAGS) $(LDLIBS) -o $@

pEp/jniadapter/pEpException.java: pEp.yml2 gen_java_exceptions.ysl2 pEp.yml2
	$(YML2_PROC) -y gen_java_exceptions.ysl2 $< -o $@

pEp/jniadapter/Message.java: pEp.yml2 gen_java_Message.ysl2 types_java.ysl2
	$(YML2_PROC) -y gen_java_Message.ysl2 $<

pEp/jniadapter/Engine.java: pEp.yml2 gen_java_Engine.ysl2 types_java.ysl2
	$(YML2_PROC) -y gen_java_Engine.ysl2 $<

pEp_jniadapter_Message.cc: pEp.yml2 gen_cpp_Message.ysl2 types_c.ysl2
	$(YML2_PROC) -y gen_cpp_Message.ysl2 $<

pEp_jniadapter_Engine.cc: pEp.yml2 gen_cpp_Engine.ysl2 types_c.ysl2
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
	rm -f pEp_jniadapter_*.h
	rm -f pEp/jniadapter/*.class
	rm -f pEp/jniadapter/pEp*.java
	rm -f pEp/jniadapter/Engine.java
	rm -f pEp/jniadapter/Message.java
	rm -f pEp/jniadapter/Color.java
	rm -f pEp/jniadapter/DecryptFlags.java
	rm -f pEp/jniadapter/IdentityFlags.java
	rm -f pEp/jniadapter/Rating.java
	rm -f pEp/jniadapter/Status.java
	rm -f pEp/jniadapter/SyncHandshakeResult.java
	rm -f pEp/jniadapter/SyncHandshakeSignal.java
	rm -f pEp/jniadapter/CipherSuite.java
	rm -f throw_pEp_exception.*
	rm -f pEp_jniadapter_Message.cc pEp_jniadapter_Engine.cc
