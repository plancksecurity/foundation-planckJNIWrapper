CLASSPATH=.:../src
VM=java -Djava.library.path=../src

test: Testing.class
	CLASSPATH=$(CLASSPATH) $(VM) Testing

%.class: %.java
	CLASSPATH=$(CLASSPATH) javac $<

.PHONY: clean

clean:
	rm -f *.class
	rm -f *.log
