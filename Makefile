.PHONY: all src test clean

all: src test

src:
	$(MAKE) -C src

test: src
	$(MAKE) -C test

clean:
	$(MAKE) -C src clean
	$(MAKE) -C test clean
