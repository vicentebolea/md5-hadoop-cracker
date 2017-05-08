package PasswordCracker;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class CrackerDriver {
    //  Set up execution information about mapreduce job and start job.
    public static void main(String[] args)
            throws Exception {
        if (args.length != 3) {
            System.err.println("Usage : hadoop jar PasswordCracker.jar outputPath encryptedPassword numberOfSplit");
            System.exit(1);
        }

        Configuration conf = new Configuration();

        String outputPath = args[0];
        String encryptedPassword = args[1];
        int numberOfSplit = Integer.parseInt(args[2]);

        conf.setInt("numberOfSplit", numberOfSplit);
        conf.set("encryptedPassword", encryptedPassword);
        conf.set("terminationFlagFilename", "Found" + System.currentTimeMillis());

        Job job = Job.getInstance(conf);

        job.setJarByClass(CrackerDriver.class);
        job.setMapperClass(PasswordCrackerMapper.class);
        job.setReducerClass(PasswordCrackerReducer.class);
        job.setInputFormatClass(CandidateRangeInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
