package com.example.environmentalstudies.User.Model;

public class boardModel {
    String name;
    int utMarks;
    int finalMarks;
    long utTime;
    long finalTime;
    long utSort;
    long finalSort;

    public boardModel(String name, int utMarks, int finalMarks, long utTime, long finalTime, long utSort, long finalSort) {
        this.name = name;
        this.utMarks = utMarks;
        this.finalMarks = finalMarks;
        this.utTime = utTime;
        this.finalTime = finalTime;
        this.utSort = utSort;
        this.finalSort = finalSort;
    }

    public boardModel() {
    }

    public void setFinalTime(long finalTime) {
        this.finalTime = finalTime;
    }

    public void setFinalSort(long finalSort) {
        this.finalSort = finalSort;
    }

    public int getUtMarks() {
        return utMarks;
    }

    public void setUtMarks(int utMarks) {
        this.utMarks = utMarks;
    }

    public long getUtTime() {
        return utTime;
    }

    public void setUtTime(long utTime) {
        this.utTime = utTime;
    }

    public long getUtSort() {
        return utSort;
    }

    public void setUtSort(long utSort) {
        this.utSort = utSort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }





    public int getFinalMarks() {
        return finalMarks;
    }

    public void setFinalMarks(int finalMarks) {
        this.finalMarks = finalMarks;
    }


    public long getFinalTime() {
        return finalTime;
    }





    public long getFinalSort() {
        return finalSort;
    }

}
