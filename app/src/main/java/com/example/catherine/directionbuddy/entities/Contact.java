package com.example.catherine.directionbuddy.entities;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Contact {
    String id;
    String name;

    String address;
    List<String> phoneNumber;
    Bitmap picUri;

    public Contact() {
        id = "";
        name = "";
        address = "";
        phoneNumber = new ArrayList<>();
        picUri = null;
    }

    public Contact(String id, String name, String address, List<String> phoneNumber, Bitmap picUri) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.picUri = picUri;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Bitmap getPicUri() {
        return picUri;
    }

    public void setPicUri(Bitmap picUri) {
        this.picUri = picUri;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", picUri=" + picUri +
                '}';
    }
}
