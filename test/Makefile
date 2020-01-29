.PHONY: all compile run clean

all: compile

run: compile
	$(MAKE) -C Basic run
	$(MAKE) -C JNI_85 run

compile:
	$(MAKE) -C Basic compile
	$(MAKE) -C JNI_85 compile

clean:
	$(MAKE) -C Basic clean
	$(MAKE) -C JNI_85 clean
