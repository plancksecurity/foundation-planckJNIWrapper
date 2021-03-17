.PHONY: all compile run clean

all: compile

run: compile
	$(MAKE) -C basic
	$(MAKE) -C regression
	#$(MAKE) -C jni88
	#$(MAKE) -C jni91
	#$(MAKE) -C jni92
	$(MAKE) -C jni94
	$(MAKE) -C jni96
	$(MAKE) -C jni98
	$(MAKE) -C jni100
	$(MAKE) -C jni111
	$(MAKE) -C jni114
	$(MAKE) -C jni115
	$(MAKE) -C jni118
	$(MAKE) -C jni125
	$(MAKE) -C jni126
	$(MAKE) -C jni132
	$(MAKE) -C jni134
	#$(MAKE) -C jni135
	$(MAKE) -C jni143
	$(MAKE) -C jni147
	$(MAKE) -C jni148


compile:
	$(MAKE) -C templateAlice compile
	$(MAKE) -C templateAliceBob compile
	$(MAKE) -C basic compile
	$(MAKE) -C regression compile
	$(MAKE) -C speedtest compile
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
	$(MAKE) -C jni118 compile
	$(MAKE) -C jni119 compile
	$(MAKE) -C jni125 compile
	$(MAKE) -C jni126 compile
	$(MAKE) -C jni132 compile
	$(MAKE) -C jni134 compile
	$(MAKE) -C jni135 compile
	$(MAKE) -C jni143 compile
	$(MAKE) -C jni147 compile
	$(MAKE) -C jni148 compile

clean:
	$(MAKE) -C templateAlice clean
	$(MAKE) -C templateAliceBob clean
	$(MAKE) -C basic clean
	$(MAKE) -C regression clean
	$(MAKE) -C speedtest clean
	$(MAKE) -C jni88 clean
	$(MAKE) -C jni91 clean
	$(MAKE) -C jni92 clean
	$(MAKE) -C jni94 clean
	$(MAKE) -C jni96 clean
	$(MAKE) -C jni98 clean
	$(MAKE) -C jni100 clean
	$(MAKE) -C jni111 clean
	$(MAKE) -C jni114 clean
	$(MAKE) -C jni118 clean
	$(MAKE) -C jni119 clean
	$(MAKE) -C jni125 clean
	$(MAKE) -C jni126 clean
	$(MAKE) -C jni132 clean
	$(MAKE) -C jni134 clean
	$(MAKE) -C jni135 clean
	$(MAKE) -C jni143 clean
	$(MAKE) -C jni147 clean
	$(MAKE) -C jni148 clean

clean-pep-home:
	$(MAKE) -C basic clean-pep-home
