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
Customization of the build can be done in `src/Makefile.conf`, or in a file `src/local.conf` which is not part of the source code distribution.

An example `src/local.conf` looks like this:

~~~
JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk8/Contents/Home

YML2_PATH=$(HOME)/code/yml2

ENGINE_INC=-I$(HOME)/code/engine/build/include
ENGINE_LIB=-L$(HOME)/code/engine/build/lib

AD_INC=-L$(HOME)/code/libad/build/include
AD_LIB=-L$(HOME)/code/libad/build/lib
~~~

Depending on what is already set in your environment, or can be found in your default include/library paths, setting any of these variables may be optional on your platform.

Now, build the Adapter with

On Linux:

~~~
cd src
make
~~~

On macOS:

~~~
cd src
gmake
~~~

(The GNU Make distributed with macOS is too old, install GNU Make "gmake" from macPorts).
