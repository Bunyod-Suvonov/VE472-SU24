package com.h3.ex3;

import com.student.avro.Student;

import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroKeyValueOutputFormat;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.hadoop.util.hash.Hash;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.hadoop.fs.Path;


public class BFjob extends Configured implements Tool {
    private static final Text ID = new Text();

    public static class Map extends Mapper<AvroKey<Student>, NullWritable, NullWritable, BloomFilter> {
        @Override
        public void map(AvroKey<Student> key, NullWritable value, Context context) throws IOException, InterruptedException {
            BloomFilter bloomFilter = new BloomFilter(1000, 5, Hash.MURMUR_HASH);
            bloomFilter.add(new Key("3".getBytes()));

            String data = new String(key.datum().getFilecontent().array());
            String[] lines = data.split("\n");

            for (String line : lines) {
                String[] words = line.split(",");
                if (words.length > 1) {
                    String id = words[1];
                    String lastBit = id.substring(id.length() - 1);
                    if (bloomFilter.membershipTest(new Key(lastBit.getBytes()))) {
                        ID.set(id);
                        bloomFilter.add(new Key(ID.getBytes()));
                    }
                }
            }
            context.write(NullWritable.get(), bloomFilter);
        }
    }

    public static class Reduce extends Reducer<NullWritable, BloomFilter, AvroKey<CharSequence>, NullWritable> {
        @Override
        public void reduce(NullWritable key, Iterable<BloomFilter> values, Context context) throws IOException, InterruptedException {
            BloomFilter combinedBF = new BloomFilter(1000, 5, Hash.MURMUR_HASH);
            for (BloomFilter value : values) {
                combinedBF.or(value);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            combinedBF.write(dataOutputStream);

            context.write(new AvroKey<CharSequence>(byteArrayOutputStream.toString()), NullWritable.get());
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(getConf());
        job.setJarByClass(BFjob.class);
        job.setJobName("Bloom Filter Job");

        FileInputFormat.setInputPaths(job, new Path("/input"));
        FileOutputFormat.setOutputPath(job, new Path("/output"));

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setInputFormatClass(AvroKeyInputFormat.class);
        AvroJob.setInputKeySchema(job, Student.getClassSchema());

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(BloomFilter.class);
        job.setOutputFormatClass(AvroKeyValueOutputFormat.class);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int result = ToolRunner.run(new BFjob(), args);
        System.exit(result);
    }
}
