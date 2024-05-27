package com.example.environmentalstudies.RoomDatabase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DownloadedFileViewModel extends AndroidViewModel {
    private DownloadedFileRepository repository;
    private LiveData<List<DownloadedFile>> allNoteDownloadedFiles;
    private LiveData<List<DownloadedFile>> allProjectDownloadedFiles;
    private LiveData<List<DownloadedFile>> allQbankDownloadedFiles;

    private LiveData<List<StudentData>> listLiveData;

    public DownloadedFileViewModel(@NonNull Application application) {
        super(application);
        repository = new DownloadedFileRepository(application);
        allNoteDownloadedFiles = repository.getAllNoteDownloadedFiles();
        allProjectDownloadedFiles = repository.getAllProjectDownloadedFiles();
        allQbankDownloadedFiles = repository.getAllQbankDownloadedFiles();

        listLiveData=repository.getMstudentData();
    }

    public void insert(DownloadedFile downloadedFile) {
        repository.insert(downloadedFile);
    }

    public void deleteFile(String fileName){
        repository.deleteFile(fileName);
    }

    public void insertStudData(StudentData data) {
        repository.insertStudData(data);
    }

    public LiveData<List<DownloadedFile>> getAllNoteDownloadedFiles() {
        return allNoteDownloadedFiles;
    }

    public LiveData<List<DownloadedFile>> getAllProjectDownloadedFiles() {
        return allProjectDownloadedFiles;
    }

    public LiveData<List<DownloadedFile>> getAllQbankDownloadedFiles() {
        return allQbankDownloadedFiles;
    }
    public LiveData<List<StudentData>> getStudentData() {
        return listLiveData;
    }
}

