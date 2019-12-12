# p≡p JNI Adapter
More complete developer instructions can be found here: [https://dev.pep.foundation/JNI%20Adapter/Tutorial]().

## Prerequisites

### yml2
To set up yml2 properly, consult the documentation of pEp Engine (linked below). yml2 is a build dependency of pEp Engine.

### C and C++ compiler
Any gcc or clang distribution offered by your OS is fine.

### pEp Engine
Instructions for obtaining the pEp Engine can be found on [https://pep.foundation/dev/repos/pEpEngine/file/]().

### libpEpAdapter
Instructions for obtaining libpEpAdapter can be found on [https://pep.foundation/dev/repos/libpEpAdapter/file/]().

### Java 8 (or newer) JDK
Download Oracle Java from [https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html]().

OpenJDK can be installed from macports on macOS with one of the following commands:

~~~
sudo port install openjdk8
sudo port install openjdk11
~~~

## Building

Build configuration will be the result of including these files in the following order:
* `Makefile.conf` - Defaults
* `local.conf` - optional cfg (overwrites existing values)
* `src/local.conf`- optional cfg for src dir (overwrites existing values)

An example `local.conf` looks like this:

~~~
JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk8/Contents/Home

YML2_PATH=$(HOME)/code/yml2

ENGINE_INC_PATH=$(HOME)/code/engine/build/include
ENGINE_LIB_PATH=$(HOME)/code/engine/build/lib

AD_INC_PATH=$(HOME)/code/libad/build/include
AD_LIB_PATH=$(HOME)/code/libad/build/lib
~~~

The the foo_PATH variables will be turned into compiler directives (-I / -L), which can be directly set by just omitting "\_PATH" (e.g. ENGINE_INC). They  will take priority.

Depending on what is already set in your environment, or can be found in your default include/library paths, setting any of these variables may be optional on your platform.

Now, build the Adapter with

On Linux:

~~~
make src
~~~

On macOS:

~~~
make src
~~~

(The GNU Make distributed with macOS might be too old, in this case install GNU Make "gmake" from macPorts).
