# p≡p JNI Adapter

Please find the complete build instructions here:
https://dev.pep.foundation/Common%20Adapter%20Documentation/Adapter_Build_Instructions

## Build Result
The binary package resulting from the build will be located under `./dist`.
It merely contains:
* pEp.jar           - The java library
* libpEpJNI.dylib   - The dynamically linkable native library
* libpEpJNI.a       - The statically linkable native library

## Build Configuration

The build configuration file is called `local.conf`.
Use the file `local.conf.example` as a template.

```bash
cp local.conf.example local.conf
```

Then, tweak it to your needs.

## Make Targets

The default make target is `compile`.

### Build
* `make compile`   
  Builds the whole adapter under `./build`.
  The complete distribution ready adapter will be in `./dist`.

### Test
* `make test`   
  Builds the complete test-suite under `./test`.

### Clean
* `make clean`   
  Deletes all the derived objects of the adapter build in `./build` and `./dist`.
  Also invokes `make clean` which cleans the whole test-suite.
  Does not remove the generated API doc.

* `make clean-doc`   
  Removes all the generated API doc.

* `make clean-all`   
  Equals `make clean` and `make clean-doc`

### Generate API Documentation
In order to generate the API doc you need to have doxygen installed on your system.

* `make doc`   
  Generates Java and C++ API doc.

* `make doc-cxx`   
  Generates the API doc for the C++ part of the adapter.

* `make doc-java`   
  Generates the API doc for the Java part of the adapter.

### Install
* `make install`
  Installs the files under `./dist` under $PREFIX (local.conf)

* `make uninstall`
  Removes all the filenames under `./dist` from $PREFIX (local.conf)
