LIBRARY=libpEpJNI.a
SHARED=libpEpJNI.so
JAR=jniadapter.jar
include Makefile.conf

all: $(JAR) $(SHARED)

JAVA_SOURCES=org/pEp/jniadapter/pEpException.java \
			 org/pEp/jniadapter/AbstractEngine.java \
			 org/pEp/jniadapter/Message.java \
			 org/pEp/jniadapter/Engine.java

C_SOURCES=org_pEp_jniadapter_Engine.cc \
		  org_pEp_jniadapter_Engine.h \
		  org_pEp_jniadapter_Message.cc \
		  org_pEp_jniadapter_Message.h \
		  throw_pEp_exception.cc \
		  throw_pEp_exception.hh \
		  org_pEp_jniadapter_AbstractEngine.h

gensource: $(JAVA_SOURCES) $(C_SOURCES)

$(JAR): $(JAVA_SOURCES)
	javac org/pEp/jniadapter/*.java
	jar cf $@ org/pEp/jniadapter/*.class

org_pEp_jniadapter_AbstractEngine.h: org/pEp/jniadapter/AbstractEngine.java
	javah $(subst /,.,$(subst .java,,$<))

org_pEp_jniadapter_Engine.h: org/pEp/jniadapter/Engine.java
	javah $(subst /,.,$(subst .java,,$<))

org_pEp_jniadapter_Message.h: org/pEp/jniadapter/Message.java
	javah $(subst /,.,$(subst .java,,$<))

org_pEp_jniadapter_AbstractEngine.o: %.o: %.cc %.h throw_pEp_exception.hh jniutils.hh

org_pEp_jniadapter_Engine.o org_pEp_jniadapter_Message.o: %.o: %.cc %.h

$(LIBRARY): org_pEp_jniadapter_AbstractEngine.o org_pEp_jniadapter_Engine.o org_pEp_jniadapter_Message.o throw_pEp_exception.o jniutils.o basic_api.o
	ar -r $@ *.o

$(SHARED): $(LIBRARY)
	g++ -shared -o $(SHARED) $(LDFLAGS) -lpEpEngine *.o

org/pEp/jniadapter/pEpException.java: pEp.yml2 gen_java_exceptions.ysl2
	yml2proc -y gen_java_exceptions.ysl2 $< -o $@

org/pEp/jniadapter/Message.java: pEp.yml2 gen_java_Message.ysl2 types_java.ysl2
	yml2proc -y gen_java_Message.ysl2 $<

org_pEp_jniadapter_Message.cc: pEp.yml2 gen_cpp_Message.ysl2 types_c.ysl2
	yml2proc -y gen_cpp_Message.ysl2 $<

org/pEp/jniadapter/Engine.java: pEp.yml2 gen_java_Engine.ysl2 types_java.ysl2
	yml2proc -y gen_java_Engine.ysl2 $<

org_pEp_jniadapter_Engine.cc: pEp.yml2 gen_cpp_Engine.ysl2 types_c.ysl2
	yml2proc -y gen_cpp_Engine.ysl2 $<

throw_pEp_exception.cc throw_pEp_exception.hh: pEp.yml2 gen_throw_pEp_exception.ysl2 textutils.ysl2
	yml2proc -y gen_throw_pEp_exception.ysl2 $< -o throw_pEp_exception.cc

throw_pEp_exception.o: throw_pEp_exception.cc throw_pEp_exception.hh

basic_api.o: basic_api.cc jniutils.hh throw_pEp_exception.hh

.PHONY: clean

clean:
	rm -f $(JAR) $(LIBRARY)
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

