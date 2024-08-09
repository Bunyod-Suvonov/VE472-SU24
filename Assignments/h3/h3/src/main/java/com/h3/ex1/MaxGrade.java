package com.h3.ex1;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MaxGrade {

    public static class Map
            extends Mapper<Object, Text, Text, IntWritable> {

        private Text studentID = new Text();
        private IntWritable grade = new IntWritable(0);

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] entry = value.toString().split(",");
            if (entry.length != 3)
                return;
            studentID.set(entry[1]);
            grade.set(Integer.parseInt(entry[2]));
            context.write(studentID, grade);
        }
    }

    public static class Reduce
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable grade = new IntWritable(0);

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int maxGrade = 0;
            while (values.iterator().hasNext())
                maxGrade = Math.max(maxGrade, values.iterator().next().get());
            grade = new IntWritable(maxGrade);
            context.write(key, grade);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "get max grade");

        job.setJarByClass(MaxGrade.class);
        job.setMapperClass(Map.class);
        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path("/input"));
        FileOutputFormat.setOutputPath(job, new Path("/output"));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
