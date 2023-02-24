package com.example.myapplication;

import android.util.Log;

public class Mobile {
    String name;
    String color;
    int ram;
    String os;
    public Mobile(){
        ram=8;
    }
    public void test(){
        Mobile samsung =new Mobile();
        samsung.name="Samsung Galaxy";
        samsung.color="Blue";
        samsung.os="Android";
        samsung.ram=12;
        Log.d("Name : ", samsung.name+", Color : "+samsung.color+", OS : " + samsung.os+", Ram : "+ samsung.ram);

        Mobile iphone =new Mobile();
        iphone.name="Apple iPhone";
        iphone.color="White";
        iphone.os="IOS";
//        iphone.ram=6;
        Log.d("Name : ", iphone.name+", Color : "+iphone.color+", OS : " + iphone.os+", Ram : "+ iphone.ram);


    }
}