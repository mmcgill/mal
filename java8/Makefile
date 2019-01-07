
TESTS =

PACKAGE = com/github/kanaka/mal

SOURCES =

all:
	mvn compile

dist: mal.jar mal

mal.jar: target/classes/$(PACKAGE)/stepA_mal.class
	mvn assembly:assembly
	cp target/mal-0.0.1.jar $@

SHELL := bash
mal: mal.jar
	cat <(echo -e '#!/bin/sh\nexec java -jar "$$0" "$$@"') mal.jar > $@
	chmod +x mal

src/main/java/$(PACKAGE)/%.java:
	mvn compile

target/classes/$(PACKAGE)/step%.class: src/main/java/$(PACKAGE)/step%.java ${SOURCES}
	mvn compile

clean:
	mvn clean
	rm -f mal.jar mal

#.PHONY: stats tests $(TESTS)
.PHONY: stats

stats: $(SOURCES)
	@wc $^
	@printf "%5s %5s %5s %s\n" `grep -E "^[[:space:]]*//|^[[:space:]]*$$" $^ | wc` "[comments/blanks]"
stats-lisp: $(SOURCES_LISP)
	@wc $^
	@printf "%5s %5s %5s %s\n" `grep -E "^[[:space:]]*//|^[[:space:]]*$$" $^ | wc` "[comments/blanks]"
