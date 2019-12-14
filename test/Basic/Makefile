include ../../Makefile.conf
include ../Makefile.conf

JAVA_CLASSES = \
  Testing.class \
	SyncCallbacks.class

.PHONY: compile run test clean

all: compile
	$(MAKE) run

run: compile
	HOME=$(PWD) CLASSPATH=$(CLASSPATH) time $(VM) Testing

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
