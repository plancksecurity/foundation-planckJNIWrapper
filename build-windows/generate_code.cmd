@ECHO OFF

SET current_directory=%~dp0
SET dist_directory=%current_directory:~0,-15%\dist
SET marker_dir=%current_directory:~0,-15%\build\marker
SET exceptions_directory=%current_directory:~0,-15%\src\java\foundation\pEp\jniadapter\exceptions
SET java_build_root=%current_directory:~0,-15%\build\java
SET java_pkg_basename=foundation\pEp\jniadapter

:: Create directories as necessary
MKDIR %marker_dir%
MKDIR %exceptions_directory%

:: Generate Status files
SH ..\utils\gen_status_codes.sh ..\..\pEp\pEpEngine.h
MV passphrase_status_list.yml2 ..\src\codegen\
MV status_list.yml2 ..\src\codegen\

:: Generate YML2 code
PUSHD ..
CD src
CD codegen

@ECHO ON
PY -m yml2.yml2proc -E utf-8 -y gen_java_Engine.ysl2 pEp.yml2
PY -m yml2.yml2proc -E utf-8 -y gen_java_Message.ysl2 pEp.yml2
PY -m yml2.yml2proc -E utf-8 -y gen_cpp_Engine.ysl2 pEp.yml2
PY -m yml2.yml2proc -E utf-8 -y gen_cpp_Message.ysl2 pEp.yml2
PY -m yml2.yml2proc -E utf-8 -y gen_throw_pEp_exception.ysl2 pEp.yml2
@ECHO OFF

:: Compile the Java part
CD ..
CD java

javac -encoding UTF-8 -d "%java_build_root%" -h ..\cxx %java_pkg_basename%\*.java
javac -encoding UTF-8 -d "%java_build_root%" %java_pkg_basename%\*.java
javac -encoding UTF-8 -d "%java_build_root%" %java_pkg_basename%\exceptions\*.java
javac -encoding UTF-8 -d "%java_build_root%" %java_pkg_basename%\interfaces\*.java

"C:\Program Files\Java\jdk-16\bin\jar" -cvf ..\build\java\pEp.jar -C "%java_build_root%" foundation

POPD
