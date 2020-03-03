include ../../Makefile.conf
include ../Makefile.conf

JAVA_CLASSES = \
  JNI_88.class

.PHONY: compile run test clean

all: compile
	$(MAKE) run

run: compile
	HOME=$(PWD) CLASSPATH=$(CLASSPATH) time $(VM) JNI_88

compile: $(JAVA_CLASSES)

%.class: %.java
	CLASSPATH=$(CLASSPATH) javac $<

clean:
	rm -f *.class
	rm -f *.log
	rm -rf .pEp_*
	rm -Rf .pEp
	rm -Rf .gnupg
	rm -Rf .lldb
