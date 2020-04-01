.PHONY: all compile run clean

all: compile

run: compile
	$(MAKE) -C Basic run
	$(MAKE) -C JNI-85 run
	$(MAKE) -C JNI-88 run

compile:
	$(MAKE) -C Basic compile
	$(MAKE) -C JNI-85 compile
	$(MAKE) -C JNI-88 compile

clean:
	$(MAKE) -C Basic clean
	$(MAKE) -C JNI-85 clean
	$(MAKE) -C JNI-88 clean
