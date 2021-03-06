/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package PasswordCracker;

import static PasswordCracker.PasswordCrackerUtil.TOTAL_PASSWORD_RANGE_SIZE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class CandidateRangeInputFormat extends InputFormat<Text, Text> {
    private List<InputSplit> splits;

    @Override
    public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        return new CandidateRangeRecordReader();
    }


    // It generate the splits which are consist of string (or solution space range) and return to JobClient.
    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException, InterruptedException {
       splits = new ArrayList<>();

        int numberOfSplit = job.getConfiguration().getInt("numberOfSplit", 1);    //get map_count
        long subRangeSize = (TOTAL_PASSWORD_RANGE_SIZE + numberOfSplit - 1) / numberOfSplit;

        // For each subrange store it in the InputSlip list
        for (int i = 0; i < numberOfSplit; i++) {
            long currentSubRange = i*subRangeSize;
            CandidateRangeInputSplit split = new CandidateRangeInputSplit(
                    String.valueOf(currentSubRange), subRangeSize, null);
            splits.add(split);
        }

        return splits;
    }
}
