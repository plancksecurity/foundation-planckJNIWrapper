SET current_directory=%~dp0
SET dist_directory=%current_directory:~0,-15%\dist
SET build_directory=%current_directory:~0,-15%\build
SET marker_dir=%build_directory%\marker
SET exceptions_directory=%current_directory:~0,-15%\src\java\foundation\pEp\jniadapter\exceptions
SET java_build_root=%build_directory%\java
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

PY -m yml2.yml2proc -E utf-8 -y gen_java_Engine.ysl2 pEp.yml2
IF %ERRORLEVEL% NEQ 0 GOTO end
PY -m yml2.yml2proc -E utf-8 -y gen_java_Message.ysl2 pEp.yml2
IF %ERRORLEVEL% NEQ 0 GOTO end
PY -m yml2.yml2proc -E utf-8 -y gen_cpp_Engine.ysl2 pEp.yml2
IF %ERRORLEVEL% NEQ 0 GOTO end
PY -m yml2.yml2proc -E utf-8 -y gen_cpp_Message.ysl2 pEp.yml2
IF %ERRORLEVEL% NEQ 0 GOTO end
PY -m yml2.yml2proc -E utf-8 -y gen_throw_pEp_exception.ysl2 pEp.yml2
IF %ERRORLEVEL% NEQ 0 GOTO end

:: Compile the Java part
CD ..
CD java

javac -encoding UTF-8 -d "%java_build_root%" -h ..\cxx %java_pkg_basename%\*.java
IF %ERRORLEVEL% NEQ 0 GOTO end
javac -encoding UTF-8 -d "%java_build_root%" %java_pkg_basename%\*.java
IF %ERRORLEVEL% NEQ 0 GOTO end
javac -encoding UTF-8 -d "%java_build_root%" %java_pkg_basename%\exceptions\*.java
IF %ERRORLEVEL% NEQ 0 GOTO end
javac -encoding UTF-8 -d "%java_build_root%" %java_pkg_basename%\interfaces\*.java
IF %ERRORLEVEL% NEQ 0 GOTO end

"C:\Program Files\Java\jdk-16\bin\jar" -cvf "%java_build_root%\pEp.jar" -C "%java_build_root%" foundation
IF %ERRORLEVEL% NEQ 0 GOTO end

:end
POPD
EXIT /B %ERRORLEVEL%
