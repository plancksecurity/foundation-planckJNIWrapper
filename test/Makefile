CLASSPATH=.:../src
VM=java -Djava.library.path=../src

test: Testing.class
	$(VM) Testing

%.class: %.java
	javac $<

.PHONY: clean

clean:
	rm -f *.class
