package com.example.environmentalstudies.RoomDatabase;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DownloadedFileDao {
    @Insert
    void insert(DownloadedFile file);

    @Query("DELETE FROM downloaded_files WHERE fileName=:filename")
    void deleteFile(String filename);

    @Query("SELECT * FROM downloaded_files WHERE type='NOTE'")
    LiveData<List<DownloadedFile>> getAllNoteDownloadedFiles();

    @Query("SELECT * FROM downloaded_files WHERE type='PROJECT'")
    LiveData<List<DownloadedFile>> getAllProjectDownloadedFiles();

    @Query("SELECT * FROM downloaded_files WHERE type='QBANK'")
    LiveData<List<DownloadedFile>> getAllQbankDownloadedFiles();

}
