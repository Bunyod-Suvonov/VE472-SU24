package com.ex3.q2;


public class App 
{
    public static void main( String[] args )
    {
        Car gtr_r34 = new Car("Nisssan", "Japan", "vrrooommmmm rororororororor");
        Bike yamaha_r15 = new Bike("Yamaha", "Japan", "vrvrvrvrvrvrvr vrnn vrnn vrnn vrnn");

        gtr_r34.makeNoise();
        yamaha_r15.makeNoise();

    }
}


abstract class Company{
    String brand;
    String location;

    Company(String brand, String location){
        this.brand = brand;
        this.location = location;
    }
}

class Car extends Company{
    String carNoise;
    Car(String brand, String location, String noise){
        super(brand, location);
        this.carNoise = noise;
    }
    void makeNoise(){
        System.out.println(this.carNoise);
    }
}

class Bike extends Company{
    String bikeNoise;

    public Bike(String brand, String location, String noise) {
        super(brand, location);
        this.bikeNoise = noise;
    }
    void makeNoise(){
        System.out.println(bikeNoise);
    }
}