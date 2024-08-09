package com.ve472.l1;

import java.util.ArrayList;
import java.util.List;


public class Hall {
    public String name;
    public String movie;
    public List<List<Boolean>> seats;

    public Double measureDist(int j, int k, int nTicket){
        int rowMax = this.seats.size();
        double dist = 0;
        for(int i=0; i<nTicket; i++){
            dist += Math.pow(rowMax - (j+1), 2) + Math.pow( Double.valueOf(this.seats.get(j).size() + 1)/2 - (k+1+i) , 2);
        }
        return dist;
    }
    public String getHallName() { return this.name; }


    public boolean bookTicket(String username, int nTicket){
        if(seats.isEmpty() || nTicket > seats.get(0).size())
            return false;
        
        List<Integer> ansSeats = new ArrayList<>();
        ansSeats.add(-1);
        ansSeats.add(-1);
        double minDistance = Double.MAX_VALUE;
        for(int j=0; j<this.seats.size(); j++){
            for(int k=0; k<this.seats.get(0).size()-nTicket+1; k++){
                boolean yesThereIsOne = true;
                for(int l=0; l<nTicket; l++){
                    if(this.seats.get(j).get(l+k) == false){
                        yesThereIsOne = false;
                        break;
                    }
                }
                if(yesThereIsOne){
                    double dist = measureDist(j, k, nTicket);
                    if(dist < minDistance){
                        minDistance = dist;
                        ansSeats.set(0, j);
                        ansSeats.set(1, k);
                    }
                    else if(dist == minDistance){
                        if(j>ansSeats.get(0)){
                            minDistance = dist;
                            ansSeats.set(0, j);
                            ansSeats.set(1, k);
                        }
                        else if(j == ansSeats.get(0)){
                            if(k < ansSeats.get(1)){
                                minDistance = dist;
                                ansSeats.set(0, j);
                                ansSeats.set(1, k);
                            }
                        }
                    }
                }
            }
        }
        if(ansSeats.get(0) == -1 && ansSeats.get(1) == -1) return false;
        String columns = new String();
        for(int i=0; i<nTicket; i++){
            columns = columns.concat(Integer.toString(ansSeats.get(1)+i+1));
            seats.get(ansSeats.get(0)).set(ansSeats.get(1)+i, false);
            if(i!=nTicket-1) columns = columns.concat(",");
        }
        System.out.println(username + "," + movie + "," + name + "," + Integer.toString(ansSeats.get(0) + 1) + "," + columns);
        
        return true;
    }

    Hall(){
        this.name = new String();
        this.movie = new String();
        this.seats = new ArrayList<>();
    }
}
