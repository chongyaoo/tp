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

set TEST_TIME=2025-10-26T00:00:00

cd ..\build\libs
java -jar %jarloc% < ..\..\text-ui-test\input2.txt > ..\..\text-ui-test\ACTUAL2.TXT

cd ..\..\text-ui-test

FC ACTUAL2.TXT EXPECTED2.TXT >NUL && ECHO Test 2 passed! || (
    Echo Test 2 failed!
    FC ACTUAL2.TXT EXPECTED2.TXT
    exit /b 1
)

REM Test 3: Simulate file corruption by copying badsave.txt to StudyMate.txt
if not exist "..\build\libs\data\" mkdir "..\build\libs\data\"
copy badsave.txt ..\build\libs\data\StudyMate.txt >NUL

cd ..\build\libs
java -jar %jarloc% < ..\..\text-ui-test\input3.txt > ..\..\text-ui-test\ACTUAL3.TXT

cd ..\..\text-ui-test

FC ACTUAL3.TXT EXPECTED3.TXT >NUL && ECHO Test 3 passed! || (
    Echo Test 3 failed!
    FC ACTUAL3.TXT EXPECTED3.TXT
    exit /b 1
)

echo All tests passed!

if exist "..\data\StudyMate.txt" del "..\data\StudyMate.txt"

