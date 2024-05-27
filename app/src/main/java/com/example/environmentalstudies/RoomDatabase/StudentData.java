package com.example.environmentalstudies.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Student_data")
public class StudentData {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;

    int correctQuestions;
    int totalQuestions;
    int completedQuizz;

    public StudentData( int correctQuestions, int totalQuestions, int completedQuizz) {
        this.correctQuestions = correctQuestions;
        this.totalQuestions = totalQuestions;
        this.completedQuizz = completedQuizz;
    }


    public int getCorrectQuestions() {
        return correctQuestions;
    }

    public void setCorrectQuestions(int correctQuestions) {
        this.correctQuestions = correctQuestions;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCompletedQuizz() {
        return completedQuizz;
    }

    public void setCompletedQuizz(int completedQuizz) {
        this.completedQuizz = completedQuizz;
    }
}
