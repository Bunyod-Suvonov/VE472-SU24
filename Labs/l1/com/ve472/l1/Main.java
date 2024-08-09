package com.ve472.l1;

import java.io.File;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;


public class Main
{
    private static final Option ARG_HELP = new Option("h", "help", false, "print this message");
    private static final Option ARG_HALL = new Option(null, "hall", true, "path of the hall config directory");
    private static final Option ARG_QUERY = new Option(null, "query", true, "query of customer orders");

    private static void printHelp(Options options) {
        org.apache.commons.cli.HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("cinema", options);
        System.exit(0);
    }

    public static void main( String[] args )
    {
        CommandLineParser clp = new DefaultParser();
        
        Options options = new Options();
        options.addOption(ARG_HALL);
        options.addOption(ARG_HELP);
        options.addOption(ARG_QUERY);
        CommandLine cl;
        try {
            cl = clp.parse(options, args);

            if(cl.hasOption(ARG_HELP.getOpt()) || cl.hasOption(ARG_HELP.getLongOpt())){
                printHelp(options);
            }
            if(!((cl.hasOption(ARG_HALL.getLongOpt()) || cl.hasOption(ARG_HALL.getOpt())) && (cl.hasOption(ARG_QUERY.getLongOpt()) || cl.hasOption(ARG_QUERY.getOpt())))){
                printHelp(options);
            }
        }
        catch(Exception e){
            printHelp(options);
            return;
        }
        String dir = "./";
        if(cl.hasOption(ARG_HALL.getLongOpt())){
            dir = cl.getOptionValue(ARG_HALL.getLongOpt());
        }
        File directory = new File(dir);
        File[] allFilesInDir = directory.listFiles();

        
        Cinema cinema = new Cinema();
        cinema.init(allFilesInDir);                  // read conf files

        String path = new String();
        if(cl.hasOption(ARG_QUERY.getLongOpt())){
            path = cl.getOptionValue(ARG_QUERY.getLongOpt());
        }
        File queryFile = new File(path);
        Scanner queryReader;
        try{
            queryReader = new Scanner(queryFile);
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }

        while(queryReader.hasNextLine()){
            String nextLine = queryReader.nextLine();
            int nComma = 0;
            String username = new String();
            String movieName = new String();
            String numberOfTickets = new String();
            for(int t=0; t<nextLine.length(); t++){
                if(nextLine.charAt(t) == ','){
                    nComma++;
                    continue;
                }

                switch (nComma) {
                    case 0:
                        username = username.concat(String.valueOf(nextLine.charAt(t)));
                        break;
                    case 1:
                        movieName = movieName.concat(String.valueOf(nextLine.charAt(t)));
                        break;
                    case 2:
                        numberOfTickets = numberOfTickets.concat(String.valueOf(nextLine.charAt(t)));
                        break;
                }
            }
            username = username.trim();
            movieName = movieName.trim();
            numberOfTickets = numberOfTickets.trim();
            int nTickets = Integer.parseInt(numberOfTickets);
            cinema.bookTicket(cinema, username, movieName, nTickets);
        }
        queryReader.close();

    
    }
}

