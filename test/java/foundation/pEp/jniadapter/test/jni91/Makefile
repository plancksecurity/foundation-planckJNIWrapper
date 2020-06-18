include ../../../../../../../Makefile.conf
include ../Makefile.conf

TEST_UNIT_NAME=jni91

JAVA_CLASSES = \
 	TestMain.class

.PHONY: pitytest compile run test clean

all: compile
	$(MAKE) run

pitytest:
	$(MAKE) -C $(PITYTEST_DIR)

run: compile clean-pep-home
	cd $(JAVA_CWD);HOME=$(JAVA_PEP_HOME_DIR) $(JAVA) $(JAVA_PKG_BASENAME).$(TEST_UNIT_NAME).TestMain

compile: $(JAVA_CLASSES) pitytest

%.class: %.java
	cd $(JAVA_CWD);javac -cp $(CLASSPATH) $(JAVA_PKG_BASEPATH)/$(TEST_UNIT_NAME)/$<

clean:
	rm -f $(JAVA_CLASSES)
	rm -f *.class
	rm -f *.log
	rm -Rf .gnupg
	rm -Rf .lldb

clean-pep-home:
	rm -rf $(PEP_HOME_DIR)/.pEp