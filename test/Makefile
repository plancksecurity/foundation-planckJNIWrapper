include ../Makefile.conf

$(info If on Mac, make sure to disable SIP, or copy the libpEpEngine.dylib into here)

ifdef ENGINE_LIB_PATH
	ifeq ($(BUILD_FOR),Linux)
	  ifndef LD_LIBRARY_PATH
	    export LD_LIBRARY_PATH=$(ENGINE_LIB_PATH)
	  endif
	else ifeq ($(BUILD_FOR),Darwin)
	  ifndef DYLD_LIBRARY_PATH
	    export DYLD_LIBRARY_PATH=$(ENGINE_LIB_PATH)
	  endif
	endif
endif

CLASSPATH=.:../src
VM=java -Xcheck:jni -Djava.library.path=../src
#VM=lldb java -- -Xcheck:jni -Djava.library.path=../src

.PHONY: test clean

test: Testing.class SyncCallbacks.class
	HOME=$(PWD) CLASSPATH=$(CLASSPATH) time $(VM) Testing

test-JNI-85: 
	$(MAKE) -C JNI_85

%.class: %.java
	CLASSPATH=$(CLASSPATH) javac $<

clean:
	$(MAKE) -C JNI_85 clean
	rm -f *.class
	rm -f *.log
	rm -f .pEp_*
	rm -Rf .pEp
	rm -Rf .gnupg
	rm -Rf .lldb
