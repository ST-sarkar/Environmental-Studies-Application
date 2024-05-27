package com.example.environmentalstudies.User.Model;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.environmentalstudies.User.ExamQuize;

public class StudentData {

    SharedPreferences StudentsharedPreferences;
    SharedPreferences loginSharedPreferences;

    public void setStudentSharedPreferences(Context context) {
        this.StudentsharedPreferences = context.getSharedPreferences("student_data",0);
    }

    public void setLoginSharedPreferences(Context context) {
        this.loginSharedPreferences = context.getSharedPreferences("LoginData",0);
    }

    public void UpdateStudentData(int no_questions, int no_correctAnswer) {

        int total_q=StudentsharedPreferences.getInt("total_questions",0);
        int correct_q=StudentsharedPreferences.getInt("correct_questions",0);
        int no_quizz=StudentsharedPreferences.getInt("completed_quizz",0);

        total_q=total_q+no_questions;
        correct_q=correct_q+no_correctAnswer;
        no_quizz=no_quizz+1;

        SharedPreferences.Editor editor=StudentsharedPreferences.edit();
        editor.putInt("total_questions",total_q);
        editor.putInt("correct_questions",correct_q);
        editor.putInt("completed_quizz",no_quizz);
        editor.apply();

    }

    public void updateLoginInfo(String uid,String password,String name){
        SharedPreferences.Editor editor = loginSharedPreferences.edit();
        editor.putString("UserName", uid);
        editor.putString("PassWord", password);
        editor.putString("Name",name);
        editor.apply();
    }


    public String getName(){
        return loginSharedPreferences.getString("Name","st");
    }

    public String getUid(){
        return loginSharedPreferences.getString("UserName","username");
    }

    public String getPassword(){return loginSharedPreferences.getString("PassWord","pass");}


    public int getTotalQuestions(){
        return StudentsharedPreferences.getInt("total_questions",0);
    }

    public int getCorrectQuestions(){
        return StudentsharedPreferences.getInt("correct_questions",0);
    }

    public int getTotalCompletedQuizz(){
        return StudentsharedPreferences.getInt("completed_quizz",0);
    }

}
