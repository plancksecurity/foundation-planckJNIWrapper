include ../Makefile.conf

$(info If on Mac, make sure to disable SIP, or copy the libpEpEngine.dylib into here)
ifeq ($(BUILD_FOR),Linux)
  ifndef LD_LIBRARY_PATH
    export LD_LIBRARY_PATH=$(ENGINE_LIB_PATH)
  endif
else ifeq ($(BUILD_FOR),Darwin)
  ifndef DYLD_LIBRARY_PATH
    export DYLD_LIBRARY_PATH=$(ENGINE_LIB_PATH)
  endif
endif

CLASSPATH=.:../src
VM=java -Xcheck:jni -Djava.library.path=../src
#VM=lldb java -- -Xcheck:jni -Djava.library.path=../src

.PHONY: test clean

test: Testing.class SyncCallbacks.class
	HOME=$(PWD) CLASSPATH=$(CLASSPATH) time $(VM) Testing

%.class: %.java
	CLASSPATH=$(CLASSPATH) javac $<

clean:
	rm -f *.class
	rm -f *.log
	rm -f .pEp_*
	rm -Rf .gnupg
	rm -Rf .lldb
