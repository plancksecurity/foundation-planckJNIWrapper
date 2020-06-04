include ../Makefile.conf

TEST_UNIT_NAME=stateless_rxtx

JAVA_CLASSES = \
 	TestMain.class \

JAVA_CLASSES += $(JAVA_CLASSES_FSMSGQUEUE)

.PHONY: compile run test clean

all: compile
	$(MAKE) run

run: compile
	cd $(JAVA_CWD);$(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestMain

compile: $(JAVA_CLASSES)

%.class: %.java
	cd $(JAVA_CWD);pwd;javac $(JAVA_PKG_BASEPATH)/$(TEST_UNIT_NAME)/$<

clean:
	rm -f $(JAVA_CLASSES)
