LIBRARY=libpEpJNI.a
JAR=jniadapter.jar

all: $(JAR) $(LIBRARY)

JAVASOURCE=$(wildcard org/pEp/jniadapter/*.java)

$(JAR): $(JAVASOURCE)
	javac $<
	jar cf $@ org/pEp/jniadapter/*.class

org_pEp_jniadapter_Engine.h: org/pEp/jniadapter/Engine.java
	javah org.pEp.jniadapter.Engine

org_pEp_jniadapter_Engine.o: org_pEp_jniadapter_Engine.h org_pEp_jniadapter_Engine.cc

$(LIBRARY): org_pEp_jniadapter_Engine.o
	ar -r $@ $<

.PHONY: clean

clean:
	rm -f $(JAR) $(LIBRARY)
	rm -f org_pEp_jniadapter_Engine.o
	rm -f org_pEp_jniadapter_Engine.h
	rm -f org/pEp/jniadapter/*.class

