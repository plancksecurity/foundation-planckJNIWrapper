.PHONY: all compile run clean

all: compile

run: compile
	$(MAKE) -C templateAlice run
	$(MAKE) -C templateAliceBob run
	$(MAKE) -C basic run
	$(MAKE) -C regression run
	$(MAKE) -C jni88 run
	$(MAKE) -C jni91 run
	$(MAKE) -C jni92 run
	$(MAKE) -C jni94 run
	$(MAKE) -C jni96 run
	$(MAKE) -C jni98 run
	$(MAKE) -C jni100 run
	$(MAKE) -C jni111 run
	$(MAKE) -C jni114 run
	$(MAKE) -C jni115 run

compile:
	$(MAKE) -C templateAlice compile
	$(MAKE) -C templateAliceBob compile
	$(MAKE) -C basic compile
	$(MAKE) -C regression compile
	$(MAKE) -C jni88 compile
	$(MAKE) -C jni91 compile
	$(MAKE) -C jni92 compile
	$(MAKE) -C jni94 compile
	$(MAKE) -C jni96 compile
	$(MAKE) -C jni98 compile
	$(MAKE) -C jni100 compile
	$(MAKE) -C jni111 compile
	$(MAKE) -C jni114 compile
	$(MAKE) -C jni115 compile

clean:
	$(MAKE) -C templateAlice clean
	$(MAKE) -C templateAliceBob clean
	$(MAKE) -C basic clean
	$(MAKE) -C regression clean
	$(MAKE) -C jni88 clean
	$(MAKE) -C jni91 clean
	$(MAKE) -C jni92 clean
	$(MAKE) -C jni94 clean
	$(MAKE) -C jni96 clean
	$(MAKE) -C jni98 clean
	$(MAKE) -C jni100 clean
	$(MAKE) -C jni111 clean
	$(MAKE) -C jni114 clean
	$(MAKE) -C jni115 clean

clean-pep-home:
	$(MAKE) -C basic clean-pep-home
