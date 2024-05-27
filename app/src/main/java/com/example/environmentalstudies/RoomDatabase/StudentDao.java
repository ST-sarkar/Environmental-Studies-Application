package com.example.environmentalstudies.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StudentDao {
    @Insert
    void insertStudentData(StudentData data);

    @Query("SELECT * FROM Student_data")
    LiveData<List<StudentData>> getStudentData();



}
