include ../../../../../../../Makefile.conf
include ../Makefile.conf

TEST_UNIT_NAME=templateAliceBobCarol

JAVA_CLASSES+= \
 	TestAlice.class \
 	TestBob.class \
 	TestCarol.class

.PHONY: pitytest compile alice bob carol test clean

all: alice compile

pitytest:
	$(MAKE) -C $(PITYTEST_DIR)

alice: compile clean-pep-home-alice
	cd $(JAVA_CWD);pwd;HOME=$(JAVA_PEP_HOME_DIR_ALICE) $(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestAlice

bob: compile clean-pep-home-bob
	cd $(JAVA_CWD);pwd;HOME=$(JAVA_PEP_HOME_DIR_BOB) $(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestBob

carol: compile clean-pep-home-carol
	cd $(JAVA_CWD);pwd;HOME=$(JAVA_PEP_HOME_DIR_CAROL) $(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestCarol

compile: $(JAVA_CLASSES) pitytest

%.class: %.java
	cd $(JAVA_CWD);$(JAVAC_CMD) -cp $(CLASSPATH) $(JAVA_PKG_BASEPATH)/$(TEST_UNIT_NAME)/$<

clean:
	rm -f $(JAVA_CLASSES)
	rm -f *.class
	rm -f *.log
	rm -Rf .gnupg
	rm -Rf .lldb

clean-pep-home: clean-pep-home-alice clean-pep-home-bob clean-pep-home-carol

clean-pep-home-alice:
	rm -rf $(PEP_HOME_DIR_ALICE)/.pEp

clean-pep-home-bob:
	rm -rf $(PEP_HOME_DIR_BOB)/.pEp

clean-pep-home-carol:
	rm -rf $(PEP_HOME_DIR_CAROL)/.pEp