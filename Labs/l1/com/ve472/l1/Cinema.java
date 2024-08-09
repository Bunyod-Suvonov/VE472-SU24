package com.ve472.l1;


import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Cinema{
    public HashMap<String, Hall> halls = new HashMap<>();
    public HashMap<String, List<Hall>> moviesToHalls = new HashMap<>();
    
    Cinema(){
        this.halls = new HashMap<>();
        this.moviesToHalls = new HashMap<>();
    }

    public void bookTicket(Cinema cinema, String username, String movie, Integer nTicket){

        // book the ticket
        Boolean booked = false;
        if (moviesToHalls.containsKey(movie)) {
            List<Hall> hallListForCurrentMovie = moviesToHalls.get(movie);
            // find the hall which can book the ticket
            for (Hall hall : hallListForCurrentMovie) {
                if (hall.bookTicket(username, nTicket)) {
                    booked = true;
                    break;
                }
            }
            // no seat available
            if (booked == false)
                System.out.println(username + "," + movie);
            return;
        }
        // no movie matched
        System.out.println(username + "," + movie);
    }








    void init(File[] allFilesInDir){
        for(int i=0; i<allFilesInDir.length; i++){
            File confFile = allFilesInDir[i];
            try {
                Scanner sc = new Scanner(confFile);
                Hall hall = new Hall();

                int cnt=0;
                while (sc.hasNextLine()){
                    String nextLine = sc.nextLine();
                    if(cnt==0){
                        hall.name = nextLine;
                    }
                    else if(cnt==1){
                        hall.movie = nextLine;
                    }
                    else{
                        List<Boolean> row = new ArrayList<>();
                        for(int j = 0; j < nextLine.length(); j++){
                            if(nextLine.charAt(j) == '1'){
                                row.add(true);
                            }
                            else if(nextLine.charAt(j) == '0'){
                                row.add(false);
                            }
                        }
                        hall.seats.add(row);
                    }
                    cnt++;
                }
                this.halls.put(hall.name, hall);
                if(this.moviesToHalls.get(hall.movie)==null){
                    List<Hall> tmp = new ArrayList<Hall>();
                    this.moviesToHalls.put(hall.movie, tmp);
                }
                List<Hall> hallsForMovie = this.moviesToHalls.get(hall.movie);
                hallsForMovie.add(hall);
                this.moviesToHalls.put(hall.movie, hallsForMovie);
                
                

                sc.close();

            } catch (Exception e) {
                e.printStackTrace();
            }            
        }
        for (String hall : moviesToHalls.keySet()) {
            List<Hall> hallList = moviesToHalls.get(hall);
            hallList.sort(Comparator.comparing(Hall::getHallName));
            moviesToHalls.replace(hall, hallList);
        }
    }
}