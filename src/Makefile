LIBRARY=libpEpJNI.a
JAR=jniadapter.jar

all: $(JAR) $(LIBRARY)

JAVASOURCE=$(wildcard org/pEp/jniadapter/*.java) org/pEp/jniadapter/pEpException.java

$(JAR): $(JAVASOURCE)
	javac $<
	jar cf $@ org/pEp/jniadapter/*.class

org_pEp_jniadapter_Engine.h: org/pEp/jniadapter/Engine.java
	javah org.pEp.jniadapter.Engine

org_pEp_jniadapter_Engine.o: org_pEp_jniadapter_Engine.h org_pEp_jniadapter_Engine.cc throw_pEp_exception.hh

$(LIBRARY): org_pEp_jniadapter_Engine.o throw_pEp_exception.o
	ar -r $@ $<

.PHONY: clean generate distclean

clean:
	rm -f $(JAR) $(LIBRARY)
	rm -f org_pEp_jniadapter_Engine.o
	rm -f org_pEp_jniadapter_Engine.h
	rm -f *.class
	rm -f org/pEp/jniadapter/*.class
	rm -f org/pEp/jniadapter/pEp*.java
	rm -f throw_pEp_exception.*

org/pEp/jniadapter/pEpException.java: pEp.yml2 gen_java_exceptions.ysl2
	yml2proc -y gen_java_exceptions.ysl2 $< -o $@

throw_pEp_exception.cc throw_pEp_exception.hh: pEp.yml2 gen_throw_pEp_exception.ysl2
	yml2proc -y gen_throw_pEp_exception.ysl2 $< -o throw_pEp_exception.cc

throw_pEp_exception.o: throw_pEp_exception.cc throw_pEp_exception.hh

