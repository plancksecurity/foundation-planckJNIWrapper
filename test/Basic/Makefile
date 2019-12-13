include ../../Makefile.conf
include ../Makefile.conf

.PHONY: test clean

test: Testing.class SyncCallbacks.class
	HOME=$(PWD) CLASSPATH=$(CLASSPATH) time $(VM) Testing

%.class: %.java
	CLASSPATH=$(CLASSPATH) javac $<

clean:
	rm -f *.class
	rm -f *.log
	rm -f .pEp_*
	rm -Rf .pEp
	rm -Rf .gnupg
	rm -Rf .lldb
