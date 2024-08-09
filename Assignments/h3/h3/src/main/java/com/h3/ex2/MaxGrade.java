package com.h3.ex2;

import org.apache.avro.Schema;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.*;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.student.avro.Student;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.Text;
import java.io.IOException;

public class MaxGrade extends Configured implements Tool {
    private static final IntWritable GRADE = new IntWritable(1);
    private static final Text STUDENT_ID = new Text();

    public static class GradeMapper extends Mapper<AvroKey<Student>, NullWritable, Text, IntWritable> {
        @Override
        public void map(AvroKey<Student> key, NullWritable value, Context context) throws IOException, InterruptedException {
            String fileContent = new String(key.datum().getFilecontent().array());
            String[] records = fileContent.split("\n");

            for (String record : records) {
                String[] fields = record.split(",");
                STUDENT_ID.set(fields[1]);
                GRADE.set(Integer.parseInt(fields[2]));
                context.write(STUDENT_ID, GRADE);
            }
        }
    }

    public static class GradeReducer extends Reducer<Text, IntWritable, AvroKey<CharSequence>, AvroValue<Integer>> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            Integer highestGrade = 0;
            CharSequence studentId = key.toString();
            for (IntWritable value : values) {
                highestGrade = Integer.max(value.get(), highestGrade);
            }
            context.write(new AvroKey<>(studentId), new AvroValue<>(highestGrade));
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Nepravilno bratan!");
            return -1;
        }

        Job job = Job.getInstance(getConf(), "Max Grade Finder");
        job.setJarByClass(MaxGrade.class);

        job.setMapperClass(GradeMapper.class);
        job.setReducerClass(GradeReducer.class);

        job.setInputFormatClass(AvroKeyInputFormat.class);
        AvroJob.setInputKeySchema(job, Student.getClassSchema());

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputFormatClass(AvroKeyValueOutputFormat.class);
        AvroJob.setOutputKeySchema(job, Schema.create(Schema.Type.STRING));
        AvroJob.setOutputValueSchema(job, Schema.create(Schema.Type.INT));

        FileInputFormat.addInputPath(job, new Path("/input"));
        FileOutputFormat.setOutputPath(job, new Path("/output"));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MaxGrade(), args);
        System.exit(exitCode);
    }
}
