package com.example.environmentalstudies.User;

public class QuizeQueModel {

    int id;
    String Que,Option1,Option2,Option3,Option4,Ans,Unit;

    public QuizeQueModel(int id, String que, String option1, String option2, String option3, String option4, String ans, String unit) {
        this.id = id;
        Que = que;
        Option1 = option1;
        Option2 = option2;
        Option3 = option3;
        Option4 = option4;
        Ans = ans;
        Unit = unit;
    }

    public int getId() {
        return id;
    }

    public String getQue() {
        return Que;
    }

    public String getOption1() {
        return Option1;
    }

    public String getOption2() {
        return Option2;
    }

    public String getOption3() {
        return Option3;
    }

    public String getOption4() {
        return Option4;
    }

    public String getAns() {
        return Ans;
    }

    public String getUnit() {
        return Unit;
    }
}
