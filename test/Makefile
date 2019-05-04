CLASSPATH=.:../src
#VM=java -Xcheck:jni -Djava.library.path=../src
VM=java -Djava.library.path=../src

test: Testing.class SyncCallbacks.class
	HOME=$(PWD) CLASSPATH=$(CLASSPATH) $(VM) Testing

%.class: %.java
	CLASSPATH=$(CLASSPATH) javac $<

.PHONY: clean

clean:
	rm -f *.class
	rm -f *.log
	rm -f *.pEp_*
