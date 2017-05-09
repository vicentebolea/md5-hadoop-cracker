package PasswordCracker;

import static PasswordCracker.PasswordCrackerUtil.findPasswordInRange;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.util.concurrent.*;

public class PasswordCrackerMapper
        extends Mapper<Text, Text, Text, Text> {

    //  After reading a key/value, it compute the password by using a function of PasswordCrackerUtil class
    //  If it receive the original password, pass the original password to reducer. Otherwise is not.
    //  FileSystem class : refer to https://hadoop.apache.org/docs/r2.7.3/api/org/apache/hadoop/fs/FileSystem.html

    public void map(Text key, Text value, Context context)
            throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        String flagFilename = conf.get("terminationFlagFilename");
        FileSystem hdfs = FileSystem.get(conf);

        TerminationChecker terminationChecker = new TerminationChecker(hdfs, flagFilename);
        terminationChecker.checkPeriodically();

        long rangeBegin = Long.valueOf(key.toString());
        long rangeEnd = Long.valueOf(value.toString());

        System.out.println("B: " + rangeBegin + " E: " + rangeEnd);

        String encryptedPassword = conf.get("encryptedPassword");
        String password = findPasswordInRange(rangeBegin, rangeEnd, encryptedPassword, terminationChecker);
        if (password != null)
            context.write(new Text(encryptedPassword), new Text(password));

    }
}

//  It is class for early termination.
//  In this assignment, a particular file becomes an ealry termination signal.
//  So, If a task find the original password, then the task creates a file using a function in this class.
//  Therefore, tasks will determine whether the quit or not by checking presence of file.
//  FileSystem class : refer to https://hadoop.apache.org/docs/r2.7.3/api/org/apache/hadoop/fs/FileSystem.html

class TerminationChecker {
    FileSystem fs;  
    Path flagPath;
    boolean isDone = false;

    TerminationChecker(FileSystem fs, String flagFilename) {
        this.fs = fs;
        this.flagPath = new Path(flagFilename);
    }

    public boolean isTerminated() throws IOException {
        return isDone;
    }

    public void setTerminated() throws IOException {
        fs.create(flagPath);
    }

    public void checkPeriodically() {
        Runnable temp =  new Runnable(){      
            @Override
            public void run(){
                try {
                    Thread.sleep(1000);
                    if (fs.exists(flagPath))
                        isDone = true;
                } catch (Exception e) {

                }
            }};
        new Thread(temp).start();
    }
}
