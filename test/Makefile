CLASSPATH=.:../src
VM=java -Xcheck:jni -Djava.library.path=../src
#VM=lldb java -- -Xcheck:jni -Djava.library.path=../src

test: Testing.class SyncCallbacks.class
	HOME=$(PWD) CLASSPATH=$(CLASSPATH) time $(VM) Testing

%.class: %.java
	CLASSPATH=$(CLASSPATH) javac $<

.PHONY: clean

clean:
	rm -f *.class
	rm -f *.log
	rm -f .pEp_*
	rm -Rf .gnupg
	rm -Rf .lldb
