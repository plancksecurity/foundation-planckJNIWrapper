include ../../../../../../../Makefile.conf
include ../Makefile.conf

TEST_UNIT_NAME=templateAliceBob

JAVA_CLASSES = \
 	TestAlice.class \
 	TestBob.class \
 	../utils/AdapterBaseTestContext.class \
 	../utils/AdapterTestUtils.class \
 	../utils/TestCallbacks.class

JAVA_CLASSES += $(JAVA_CLASSES_FRAMEWORK)

.PHONY: compile alice bob test clean

all: alice compile

alice: compile clean-pep-home-alice
	cd $(JAVA_CWD);pwd;HOME=$(JAVA_PEP_HOME_DIR_ALICE) $(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestAlice

bob: compile clean-pep-home-bob
	cd $(JAVA_CWD);pwd;HOME=$(JAVA_PEP_HOME_DIR_BOB) $(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestBob

compile: $(JAVA_CLASSES)

%.class: %.java
	cd $(JAVA_CWD);javac -cp $(CLASSPATH) $(JAVA_PKG_BASEPATH)/$(TEST_UNIT_NAME)/$<

clean:
	rm -f $(JAVA_CLASSES)
	rm -f *.class
	rm -f *.log
	rm -Rf .gnupg
	rm -Rf .lldb

clean-pep-home: clean-pep-home-alice clean-pep-home-bob

clean-pep-home-alice:
	rm -rf $(PEP_HOME_DIR_ALICE)/.pEp

clean-pep-home-bob:
	rm -rf $(PEP_HOME_DIR_BOB)/.pEp
