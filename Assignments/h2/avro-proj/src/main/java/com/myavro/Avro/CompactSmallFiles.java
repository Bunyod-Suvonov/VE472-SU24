package com.myavro.Avro;

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import com.myavro.Avro.avro.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hello world!
 *
 */
public class CompactSmallFiles
{
    public static void main( String[] args )
    {
        String inputDir = "bins";
        String outputFilePath = "avros/binaries.avro";

        binaryFile b = new binaryFile();
        DatumWriter<binaryFile> userDatumWriter = new SpecificDatumWriter<binaryFile>(binaryFile.class);
        DataFileWriter<binaryFile> dataFileWriter = new DataFileWriter<binaryFile>(userDatumWriter);
        try{
            //dataFileWriter.setCodec(CodecFactory.snappyCodec());
            dataFileWriter.create(b.getSchema(), new File(outputFilePath));      

            File folder = new File(inputDir);
            File files[] = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        byte[] fileContent = Files.readAllBytes(file.toPath());

                        String checksum = calculateChecksum(fileContent);

                        ByteBuffer byteBuffer = ByteBuffer.allocate(fileContent.length);
                        byteBuffer.put(fileContent);
                        byteBuffer.rewind();
                        binaryFile bin = new binaryFile(file.getName(), byteBuffer, checksum);
                        dataFileWriter.append(bin);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try{
            dataFileWriter.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String calculateChecksum(byte[] fileContent) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(fileContent);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
