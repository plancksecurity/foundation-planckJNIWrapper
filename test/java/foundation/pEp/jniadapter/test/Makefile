.PHONY: all compile run clean

all: compile

run: compile
	$(MAKE) -C template run
	$(MAKE) -C basic run
	$(MAKE) -C regression run
	$(MAKE) -C jni88 run
	$(MAKE) -C jni91 run
	$(MAKE) -C jni92 run
	$(MAKE) -C jni94 run
	$(MAKE) -C jni98 run

compile:
	$(MAKE) -C template compile
	$(MAKE) -C basic compile
	$(MAKE) -C regression compile
	$(MAKE) -C jni88 compile
	$(MAKE) -C jni91 compile
	$(MAKE) -C jni92 compile
	$(MAKE) -C jni94 compile
	$(MAKE) -C jni98 compile

clean:
	$(MAKE) -C template clean
	$(MAKE) -C basic clean
	$(MAKE) -C regression clean
	$(MAKE) -C jni88 clean
	$(MAKE) -C jni91 clean
	$(MAKE) -C jni92 clean
	$(MAKE) -C jni94 clean
	$(MAKE) -C jni98 clean

clean-pep-home:
	$(MAKE) -C basic clean-pep-home
