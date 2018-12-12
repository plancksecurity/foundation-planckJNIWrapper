# Copyright 2018, pEp Foundation
# This file is part of pEp JNI Adapter
# This file may be used under the terms of the GNU General Public License version 3
# see LICENSE.txt

include Makefile.conf

ifneq ($(wildcard local.conf),)
    $(info ================================================)
    $(info Overrides in \`local.conf\` are used.)
    $(info ================================================)
endif

ifdef BUILD_CONFIG
    $(info ================================================)
    $(info Overrides in \`$(BUILD_CONFIG)\` are used.)
    $(info ================================================)
endif

ifndef JAVA_HOME
    $(error JAVA_HOME is not set!)
endif

JP=$(JAVA_HOME)/bin

# Old versions of a Java distribution have a `javah` binary, new versions do not. This checks whether or not `javah` can be found in the Java distribution found in the directory `$JAVA_HOME`.
NOT_USED:=$(shell type $(JAVA_HOME)/bin/javah)
ifeq ($(.SHELLSTATUS),0)
    OLD_JAVA=placeholder
endif

LIBRARY=libpEpJNI.a
JAR=jniadapter.jar

ifeq ($(BUILD_FOR),Linux)
    SHARED=libpEpJNI.so
else ifeq ($(BUILD_FOR),Darwin)
    SHARED=libpEpJNI.dylib
else
    $(error I don't know how to build for $(BUILD_FOR).)
endif

JAVA_SOURCES=org/pEp/jniadapter/pEpException.java \
    org/pEp/jniadapter/AbstractEngine.java \
    org/pEp/jniadapter/Message.java \
    org/pEp/jniadapter/Engine.java \
    org/pEp/jniadapter/Identity.java \
    org/pEp/jniadapter/_Identity.java \
    org/pEp/jniadapter/Blob.java \
    org/pEp/jniadapter/_Blob.java \
    org/pEp/jniadapter/CommType.java

C_SOURCES=org_pEp_jniadapter_Engine.cc \
    org_pEp_jniadapter_Engine.h \
    org_pEp_jniadapter_Message.cc \
    org_pEp_jniadapter_Message.h \
    throw_pEp_exception.cc \
    throw_pEp_exception.hh \
    org_pEp_jniadapter_AbstractEngine.h


.PHONY: all
all: $(JAR) $(SHARED)

.PHONY: gensource
gensource: $(JAVA_SOURCES) $(C_SOURCES)

$(JAR): $(JAVA_SOURCES)
	$(JP)/javac org/pEp/jniadapter/*.java
	$(JP)/jar cf $@ org/pEp/jniadapter/*.class

BLUBB = org_pEp_jniadapter_AbstractEngine.h org_pEp_jniadapter_Engine.h org_pEp_jniadapter_Message.h
$(BLUBB): org_pEp_jniadapter_%.h: org/pEp/jniadapter/%.java
ifdef OLD_JAVA
	$(JP)/javah $(subst /,.,$(subst .java,,$<))
else
	$(JP)/javac -h . $<
endif

org_pEp_jniadapter_AbstractEngine.o: %.o: %.cc %.h throw_pEp_exception.hh jniutils.hh
	$(CXX) $(CXXFLAGS) -c $< -o $@

org_pEp_jniadapter_Engine.o org_pEp_jniadapter_Message.o: %.o: %.cc %.h
	$(CXX) $(CXXFLAGS) -c $< -o $@

$(LIBRARY): org_pEp_jniadapter_AbstractEngine.o org_pEp_jniadapter_Engine.o org_pEp_jniadapter_Message.o throw_pEp_exception.o jniutils.o basic_api.o
	ar -r $@ *.o

$(SHARED): $(LIBRARY)
	$(CXX) *.o $(LDFLAGS) $(LDLIBS) -o $@

org/pEp/jniadapter/pEpException.java: pEp.yml2 gen_java_exceptions.ysl2
	$(YML2_PROC) -y gen_java_exceptions.ysl2 $< -o $@

org/pEp/jniadapter/Message.java: pEp.yml2 gen_java_Message.ysl2 types_java.ysl2
	$(YML2_PROC) -y gen_java_Message.ysl2 $<

org_pEp_jniadapter_Message.cc: pEp.yml2 gen_cpp_Message.ysl2 types_c.ysl2
	$(YML2_PROC) -y gen_cpp_Message.ysl2 $<

org/pEp/jniadapter/Engine.java: pEp.yml2 gen_java_Engine.ysl2 types_java.ysl2
	$(YML2_PROC) -y gen_java_Engine.ysl2 $<

org_pEp_jniadapter_Engine.cc: pEp.yml2 gen_cpp_Engine.ysl2 types_c.ysl2
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
	rm -f org_pEp_jniadapter_*.h
	rm -f org/pEp/jniadapter/*.class
	rm -f org/pEp/jniadapter/pEp*.java
	rm -f org/pEp/jniadapter/Engine.java
	rm -f org/pEp/jniadapter/Message.java
	rm -f org/pEp/jniadapter/Color.java
	rm -f throw_pEp_exception.*
	rm -f org_pEp_jniadapter_Message.cc org_pEp_jniadapter_Engine.cc
