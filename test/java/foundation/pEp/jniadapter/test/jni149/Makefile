include ../../../../../../../Makefile.conf
include ../Makefile.conf

TEST_UNIT_NAME=jni149

JAVA_CLASSES+= \
 	TestAlice.class

.PHONY: pitytest compile alice test clean

all: alice compile

pitytest:
	$(MAKE) -C $(PITYTEST_DIR)

alice: compile clean-pep-home-alice
	cd $(JAVA_CWD);pwd;HOME=$(JAVA_PEP_HOME_DIR_ALICE) $(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestAlice

compile: $(JAVA_CLASSES) pitytest

%.class: %.java
	cd $(JAVA_CWD);javac -cp $(CLASSPATH) $(JAVA_PKG_BASEPATH)/$(TEST_UNIT_NAME)/$<

clean:
	rm -f $(JAVA_CLASSES)
	rm -f *.class
	rm -f *.log
	rm -Rf .gnupg
	rm -Rf .lldb

clean-pep-home: clean-pep-home-alice

clean-pep-home-alice:
	rm -rf $(PEP_HOME_DIR_ALICE)/.pEp
