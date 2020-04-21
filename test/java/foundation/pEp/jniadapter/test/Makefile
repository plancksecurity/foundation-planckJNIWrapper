.PHONY: all compile run clean

all: compile

run: compile
	$(MAKE) -C basic run
#BROKEN	$(MAKE) -C jni85 run
	$(MAKE) -C jni88 run
	$(MAKE) -C jni91 run
	$(MAKE) -C jni92 run
	$(MAKE) -C jni94 run

compile:
	$(MAKE) -C basic compile
#BROKEN	$(MAKE) -C jni85 compile
	$(MAKE) -C jni88 compile
	$(MAKE) -C jni91 compile
	$(MAKE) -C jni92 compile
	$(MAKE) -C jni94 compile

clean:
	$(MAKE) -C basic clean
	$(MAKE) -C jni85 clean
	$(MAKE) -C jni88 clean
	$(MAKE) -C jni91 clean
	$(MAKE) -C jni92 clean
	$(MAKE) -C jni94 clean

clean-pep-home:
	$(MAKE) -C basic clean-pep-home
