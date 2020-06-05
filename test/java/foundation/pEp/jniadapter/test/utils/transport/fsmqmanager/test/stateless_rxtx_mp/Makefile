include ../Makefile.conf

TEST_UNIT_NAME=stateless_rxtx_mp

JAVA_CLASSES = \
 	TestAlice.class \
 	TestBob.class

JAVA_CLASSES += $(JAVA_CLASSES_FSMSGQUEUE)
JAVA_CLASSES += $(JAVA_CLASSES_TESTSCOMMON)

.PHONY: all alice bob compile clean

all: alice compile

alice: compile
	cd $(JAVA_CWD);$(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestAlice

bob: compile
	cd $(JAVA_CWD);$(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestBob

compile: $(JAVA_CLASSES)

%.class: %.java
	cd $(JAVA_CWD);pwd;javac $(JAVA_PKG_BASEPATH)/$(TEST_UNIT_NAME)/$<

clean:
	rm -f $(JAVA_CLASSES)