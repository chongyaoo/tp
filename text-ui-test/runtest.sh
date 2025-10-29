#!/usr/bin/env bash

# change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar

cd text-ui-test

export TEST_TIME=2025-10-25T12:00:00

java -jar $(find ../build/libs/ -maxdepth 1 -name "*.jar" -type f -print -quit) < input.txt > ACTUAL.TXT

cp EXPECTED.TXT EXPECTED-UNIX.TXT
dos2unix EXPECTED-UNIX.TXT ACTUAL.TXT
diff EXPECTED-UNIX.TXT ACTUAL.TXT
if [ $? -eq 0 ]
then
    echo "Test 1 passed!"
else
    echo "Test 1 failed!"
    exit 1
fi

export TEST_TIME=2025-10-26T00:00:00

java -jar $(find ../build/libs/ -maxdepth 1 -name "*.jar" -type f -print -quit) < input2.txt > ACTUAL2.TXT

cp EXPECTED2.TXT EXPECTED2-UNIX.TXT
dos2unix EXPECTED2-UNIX.TXT ACTUAL2.TXT
diff EXPECTED2-UNIX.TXT ACTUAL2.TXT
if [ $? -eq 0 ]
then
    echo "Test 2 passed!"
else
    echo "Test 2 failed!"
    exit 1
fi

# Test 3: Simulate file corruption by copying badsave.txt to StudyMate.txt
mkdir -p ../build/libs/data
cp badsave.txt ../build/libs/data/StudyMate.txt

cd ../build/libs
java -jar $(find . -maxdepth 1 -name "*.jar" -type f -print -quit) < ../../text-ui-test/input3.txt > ../../text-ui-test/ACTUAL3.TXT
cd ../../text-ui-test

cp EXPECTED3.TXT EXPECTED3-UNIX.TXT
dos2unix EXPECTED3-UNIX.TXT ACTUAL3.TXT
diff EXPECTED3-UNIX.TXT ACTUAL3.TXT
if [ $? -eq 0 ]
then
    echo "Test 3 passed!"
else
    echo "Test 3 failed!"
    exit 1
fi

echo "All tests passed!"

rm -f ../data/StudyMate.txt
rm -rf data

exit 0
