package com.myavro.Avro;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;

import com.myavro.Avro.avro.binaryFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExtractSmallFiles {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        String inputFilePath = "avros/binaries.avro";
        String outputDir = "extractedBins/";

        binaryFile b = new binaryFile();
        File inputFile = new File(inputFilePath);
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(b.getSchema());
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(inputFile, datumReader);

        GenericRecord bin = null;
        while (dataFileReader.hasNext()) {
            bin = dataFileReader.next(bin);

            String filename = bin.get("filename").toString();
            byte[] fileContent = ((ByteBuffer) bin.get("filecontent")).array();
            String checksum = bin.get("checksum").toString();

            // Verify checksum
            String calculatedChecksum = calculateChecksum(fileContent);
            if (!calculatedChecksum.equals(checksum)) {
                System.err.println("Checksum mismatch for file: " + filename);
                continue;
            }  
            File outputFile = new File(outputDir, filename);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(fileContent);
            }          
        }

        dataFileReader.close();
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
