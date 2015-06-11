LIBRARY=libpEpJNI.a
JAR=jniadapter.jar

include Makefile.conf

all: $(JAR) $(LIBRARY)

$(JAR): org/pEp/jniadapter/pEpException.java org/pEp/jniadapter/AbstractEngine.java org/pEp/jniadapter/Engine.java
	javac org/pEp/jniadapter/*.java
	jar cf $@ org/pEp/jniadapter/*.class

org_pEp_jniadapter_AbstractEngine.h: org/pEp/jniadapter/AbstractEngine.java
	javah org.pEp.jniadapter.AbstractEngine

org_pEp_jniadapter_Engine.h: org/pEp/jniadapter/Engine.java
	javah org.pEp.jniadapter.Engine

org_pEp_jniadapter_AbstractEngine.o: org_pEp_jniadapter_AbstractEngine.h org_pEp_jniadapter_AbstractEngine.cc throw_pEp_exception.hh jniutils.hh

$(LIBRARY): org_pEp_jniadapter_AbstractEngine.o throw_pEp_exception.o jniutils.o
	ar -r $@ *.o

org/pEp/jniadapter/pEpException.java: pEp.yml2 gen_java_exceptions.ysl2
	yml2proc -y gen_java_exceptions.ysl2 $< -o $@

org/pEp/jniadapter/Engine.java : pEp.yml2 gen_java_Engine.ysl2 types_java.ysl2
	yml2proc -y gen_java_Engine.ysl2 $< -o $@

throw_pEp_exception.cc throw_pEp_exception.hh: pEp.yml2 gen_throw_pEp_exception.ysl2 textutils.ysl2
	yml2proc -y gen_throw_pEp_exception.ysl2 $< -o throw_pEp_exception.cc

throw_pEp_exception.o: throw_pEp_exception.cc throw_pEp_exception.hh

.PHONY: clean

clean:
	rm -f $(JAR) $(LIBRARY)
	rm -f *.o
	rm -f *.class
	rm -f org_pEp_jniadapter_*.h
	rm -f org/pEp/jniadapter/*.class
	rm -f org/pEp/jniadapter/pEp*.java
	rm -f org/pEp/jniadapter/Engine.java
	rm -f throw_pEp_exception.*

