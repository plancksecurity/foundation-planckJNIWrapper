include ../Makefile.conf

TEST_UNIT_NAME=regression

JAVA_CLASSES+= \
 	TestMain.class

JAVA_CLASSES += $(JAVA_CLASSES_FSMSGQUEUE)
JAVA_CLASSES += $(JAVA_CLASSES_TESTSCOMMON)

.PHONY: pitytest compile run test clean

all: compile
	$(MAKE) run

pitytest:
	$(MAKE) -C $(PITYTEST_DIR)

run: compile
	cd $(JAVA_CWD);$(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestMain

compile: $(JAVA_CLASSES) pitytest

%.class: %.java
	cd $(JAVA_CWD);pwd;$(JAVAC_CMD) $(JAVA_PKG_BASEPATH)/$(TEST_UNIT_NAME)/$<

clean:
	rm -f $(JAVA_CLASSES)
