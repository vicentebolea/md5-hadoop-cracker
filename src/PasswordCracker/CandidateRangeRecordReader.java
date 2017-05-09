package PasswordCracker;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class CandidateRangeRecordReader extends RecordReader<Text, Text> {
    private String rangeBegin;
    private String rangeEnd;
    private boolean done = false;

    CandidateRangeRecordReader() {

    }

    @Override
    public Text getCurrentKey() throws IOException, InterruptedException {
        return new Text(rangeBegin);
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return new Text(rangeEnd);
    }

    // After creating this class, It is called with a inputSplit as a parameter. and It divides inputSplit by a record of key/value.
    @Override
    public void initialize(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        CandidateRangeInputSplit candidataRangeSplit = (CandidateRangeInputSplit) split;

        rangeBegin = candidataRangeSplit.getInputRange();
        rangeEnd = String.valueOf(Long.valueOf(rangeBegin) + candidataRangeSplit.getLength());

    }

    // Normally, this function in the RecordReader is called repeatedly to polulate the key and value objects for the mapper.
    // and When the reader gets to the end of the stream, the next method false, and the map task completes.
    // But in our case, it is called only one.

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        boolean tmpDone = false;
        if (done == false) {
            tmpDone = true;
            done = true;
        }

        return tmpDone;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        if (done) {
            return 1.0f;
        }
        return 0.0f;
    }

    @Override
    public void close() throws IOException {
    }
}
