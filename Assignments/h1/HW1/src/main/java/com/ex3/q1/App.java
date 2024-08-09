package com.ex3.q1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        File directory = new File("samples");
        File[] allFilesInDir = directory.listFiles();

        for(int l=0; l<allFilesInDir.length; l++){
            File file = allFilesInDir[l];
            File newFile = new File("samples/" + file.getName().substring(0,file.getName().length()-4) + "New.txt");
            try {
                newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            try {
                Scanner sc = new Scanner(file);
                String textString = new String();
                while (sc.hasNextLine()){
                    
                    String first_name = new String();
                    String name = new String();
                    String email = new String();
                    String nextLine = sc.nextLine();
                    System.out.println(nextLine);
                    Integer i=0;
                    Integer j=nextLine.length()-1;
                    while (i < nextLine.length() && nextLine.charAt(i) != ' ') {
                        first_name += nextLine.charAt(i);
                        i++;
                    }
                    while (j >= 0 && nextLine.charAt(j) != ' ') {
                        email = nextLine.charAt(j) + email;
                        j--;
                    }
                    name = nextLine.substring(i+1, j);
                    List<String> strings = new ArrayList<>();
                    strings.add(first_name);
                    strings.add(name);
                    strings.add(email);
                    Collections.shuffle(strings);
                    String finalString = strings.get(0);

                    for(Integer p=1; p<strings.size(); p++){
                        finalString = finalString + " " + strings.get(p);
                    }

                    textString = textString + finalString + '\n';

                }

                try {
                    FileWriter myWriter = new FileWriter("samples/" + newFile.getName());
                    myWriter.write(textString);
                    myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }               

                sc.close();

            } catch (Exception e) {
                e.printStackTrace();
            } 
        } 
    }
}
