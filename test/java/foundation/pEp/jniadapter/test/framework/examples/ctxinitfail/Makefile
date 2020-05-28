include ../Makefile.conf

TEST_UNIT_NAME=ctxinitfail

JAVA_CLASSES = \
 	TestMain.class \

# Use the test framework
JAVA_CLASSES += $(JAVA_CLASSES_FRAMEWORK)

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
