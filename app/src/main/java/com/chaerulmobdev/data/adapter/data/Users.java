package com.chaerulmobdev.data.adapter.data;

public class Users {
    String email;
    String name;
    String number;
    String location;
    String skill;
    String tanggal_lahir;
    String magang;
    String pendidikan;
    boolean online;

    public Users(String name, String email, String number, String location, String skill, String pendidikan, String tanggal_lahir, String magang, boolean online){
        this.name = name;
        this.email = email;
        this.number = number;
        this.location = location;
        this.skill = skill;
        this.online = online;
        this.pendidikan = pendidikan;
        this.magang = magang;
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getPendidikan() {
        return pendidikan;
    }

    public String getMagang() {
        return magang;
    }

    public String getTanggalLahir() {
        return tanggal_lahir;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getSkill() {
        return skill;
    }

    public String getNumber() {
        return number;
    }

    public boolean isOnline(){
        return online;
    }
}
