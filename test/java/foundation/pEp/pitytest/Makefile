include Makefile.conf

PKGNAME=pitytest

JAVA_CLASSES = $(JAVA_CLASSES_PITYTEST)

.PHONY: all compile clean

all: compile

#run: compile clean-pep-home
#	cd $(JAVA_CWD);HOME=$(JAVA_PEP_HOME_DIR) $(JAVA) $(JAVA_PKG_BASENAME).$(PKGNAME).TestMain

compile: $(JAVA_CLASSES)

%.class: %.java
	cd $(JAVA_CWD);javac -cp $(CLASSPATH) $(JAVA_PKG_BASEPATH)/$(PKGNAME)/$<

clean:
	rm -f $(JAVA_CLASSES)
	rm -f *.class
	rm -f *.log
	rm -Rf .gnupg
	rm -Rf .lldb