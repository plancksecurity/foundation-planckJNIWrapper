include ../../../../../../../Makefile.conf
include ../Makefile.conf

TEST_UNIT_NAME=jni153

JAVA_CLASSES+= \
 	TestAlice.class \
 	TestBob.class \
 	CTXMultiNode.class

.PHONY: pitytest compile alice bob test clean

all: both compile

pitytest:
	$(MAKE) -C $(PITYTEST_DIR)

both:
	$(MAKE) bob&
	$(MAKE) alice

alice: compile clean-pep-home-alice
	cd $(JAVA_CWD);pwd;HOME=$(JAVA_PEP_HOME_DIR_ALICE) $(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestAlice

bob: compile clean-pep-home-bob
	cd $(JAVA_CWD);pwd;HOME=$(JAVA_PEP_HOME_DIR_BOB) $(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestBob

compile: $(JAVA_CLASSES) pitytest

%.class: %.java
	cd $(JAVA_CWD);$(JAVAC_CMD) -cp $(CLASSPATH) $(JAVA_PKG_BASEPATH)/$(TEST_UNIT_NAME)/$<

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
