# This makefile is defined to give you the following targets:
#
#    default: Same as check
#    check: For each file, F.in, use F.in as input to "java $(MAIN_CLASS)" and
#          compare the output to the contents of the file names F.out.
#          Report discrepencies.
#    clean: Remove all the .class files produced by java compilation, 
#          all Emacs backup files, and testing output files.
#

SHELL = /bin/bash

# Flags to Java interpreter: check assertions
JFLAGS = -ea

# Name of class containing main procedure 
MAIN = db61b.Main

TESTER_FLAGS =

TESTS := $(wildcard *.in)

.PHONY: default check clean

# First, and therefore default, target.
default:
	$(MAKE) -C .. 
	$(MAKE) check

# 'make check' will test run the tests in the testing directory.
# For each .in file, it will "pipe" that file through your program
# (that is, cause the .in file to be the standard input, rather as if it
# were typed into the terminal), and compare the result with the
# corresponding .out file.  It will report any failures.
check: $(CLASSES)
	@echo "Testing application $(MAIN)..."
	@CLASSPATH=..:$(CLASSPATH) python3 tester.py $(TESTER_FLAGS) $(TESTS)
	java -cp ../:$(CLASSPATH) db61b.BasicTests

# 'make clean' will clean up stuff you can reconstruct.
clean:
	$(RM) -r *~ OUTPUT* *.log *.diff __pycache__


