rm -r classes/*
javac -classpath $($HADOOP_HOME/bin/hadoop classpath) -d classes ./src/PasswordCracker/CrackerDriver.java ./src/PasswordCracker/CandidateRangeInputFormat.java ./src/PasswordCracker/CandidateRangeInputSplit.java ./src/PasswordCracker/CandidateRangeRecordReader.java ./src/PasswordCracker/PasswordCrackerMapper.java ./src/PasswordCracker/PasswordCrackerReducer.java ./src/PasswordCracker/PasswordCrackerUtil.java
jar -cvf PasswordCrackerInHadoop.jar -C classes .
