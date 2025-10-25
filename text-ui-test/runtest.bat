@echo off
setlocal enableextensions
pushd %~dp0

cd ..
call gradlew clean shadowJar

cd build\libs
for /f "tokens=*" %%a in (
    'dir /b *.jar'
) do (
    set jarloc=%%a
)

set TEST_TIME=2025-10-25T12:00:00

java -jar %jarloc% < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT

cd ..\..\text-ui-test

FC ACTUAL.TXT EXPECTED.TXT >NUL && ECHO Test 1 passed! || (
    Echo Test 1 failed!
    FC ACTUAL.TXT EXPECTED.TXT
    exit /b 1
)

cd ..\build\libs
java -jar %jarloc% < ..\..\text-ui-test\input2.txt > ..\..\text-ui-test\ACTUAL2.TXT

cd ..\..\text-ui-test

FC ACTUAL2.TXT EXPECTED2.TXT >NUL && ECHO Test 2 passed! || (
    Echo Test 2 failed!
    FC ACTUAL2.TXT EXPECTED2.TXT
    exit /b 1
)

echo All tests passed!

if exist "..\data\StudyMate.txt" del "..\data\StudyMate.txt"
