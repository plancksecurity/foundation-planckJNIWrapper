LIBRARY=libpEpJNI.a
JAR=jniadapter.jar

all: $(JAR) $(LIBRARY)

$(JAR): org/pEp/jniadapter/pEpException.java
	javac org/pEp/jniadapter/*.java
	jar cf $@ org/pEp/jniadapter/*.class

org_pEp_jniadapter_AbstractEngine.h: org/pEp/jniadapter/Engine.java
	javah org.pEp.jniadapter.AbstractEngine

org_pEp_jniadapter_AbstractEngine.o: org_pEp_jniadapter_AbstractEngine.h org_pEp_jniadapter_AbstractEngine.cc throw_pEp_exception.hh jniutils.hh

$(LIBRARY): org_pEp_jniadapter_AbstractEngine.o throw_pEp_exception.o jniutils.o
	ar -r $@ *.o

.PHONY: clean distclean

clean:
	rm -f $(JAR) $(LIBRARY)
	rm -f *.o
	rm -f *.class
	rm -f org_pEp_jniadapter_*.h
	rm -f org/pEp/jniadapter/*.class
	rm -f org/pEp/jniadapter/pEp*.java
	rm -f throw_pEp_exception.*

org/pEp/jniadapter/pEpException.java: pEp.yml2 gen_java_exceptions.ysl2
	yml2proc -y gen_java_exceptions.ysl2 $< -o $@

throw_pEp_exception.cc throw_pEp_exception.hh: pEp.yml2 gen_throw_pEp_exception.ysl2
	yml2proc -y gen_throw_pEp_exception.ysl2 $< -o throw_pEp_exception.cc

throw_pEp_exception.o: throw_pEp_exception.cc throw_pEp_exception.hh

