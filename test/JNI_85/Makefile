include ../../Makefile.conf
include ../Makefile.conf

#TODO: Remove when successufuly reproduce the error, and then use it to avoid regression
$(info This is to be run using pEpJNIAdapter 626)

.PHONY: test clean

test: SyncCallbacks.class Step1.class Step2.class
	-HOME=$(PWD) CLASSPATH=$(CLASSPATH) time $(VM) Step1
	HOME=$(PWD) CLASSPATH=$(CLASSPATH) time $(VM) Step2

%.class: %.java
	CLASSPATH=$(CLASSPATH) javac $<

clean:
	rm -f *.class
	rm -f *.log
	rm -f .pEp_*
	rm -Rf .pEp
	rm -Rf .gnupg
	rm -Rf .lldb
