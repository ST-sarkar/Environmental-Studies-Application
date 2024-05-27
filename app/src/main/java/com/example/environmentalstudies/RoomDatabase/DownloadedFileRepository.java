package com.example.environmentalstudies.RoomDatabase;
import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DownloadedFileRepository {
    private DownloadedFileDao mDownloadedFileDao;
    private StudentDao studentDao;
    private LiveData<List<StudentData>> mstudentData;
    private LiveData<List<DownloadedFile>> mAllNoteDownloadedFiles;
    private LiveData<List<DownloadedFile>> mAllProjectDownloadedFiles;
    private LiveData<List<DownloadedFile>> mAllQbankDownloadedFiles;

    DownloadedFileRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDownloadedFileDao = db.downloadedFileDao();
        studentDao=db.studentDao();
        mstudentData=studentDao.getStudentData();
        mAllNoteDownloadedFiles = mDownloadedFileDao.getAllNoteDownloadedFiles();
        mAllProjectDownloadedFiles = mDownloadedFileDao.getAllProjectDownloadedFiles();
        mAllQbankDownloadedFiles = mDownloadedFileDao.getAllQbankDownloadedFiles();
    }

    LiveData<List<StudentData>> getMstudentData(){return mstudentData;}

    LiveData<List<DownloadedFile>> getAllNoteDownloadedFiles() {
        return mAllNoteDownloadedFiles;
    }

    LiveData<List<DownloadedFile>> getAllProjectDownloadedFiles() {
        return mAllProjectDownloadedFiles;
    }

    LiveData<List<DownloadedFile>> getAllQbankDownloadedFiles() {
        return mAllQbankDownloadedFiles;
    }

    public void insertStudData(StudentData studentData){
        new insertStudDataAsyncTask(studentDao).execute(studentData);
    }

    public void insert(DownloadedFile file) {
        new insertAsyncTask(mDownloadedFileDao).execute(file);
    }

    public void deleteFile(String filename) {
        new deleteAsyncTask(mDownloadedFileDao).execute(filename);
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {
        private DownloadedFileDao mAsyncTaskDao;

        deleteAsyncTask(DownloadedFileDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... filenames) {
            // Assuming deleteFile method in DownloadedFileDao takes a filename as a parameter
            String filename = filenames[0];
            mAsyncTaskDao.deleteFile(filename);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<DownloadedFile, Void, Void> {
        private DownloadedFileDao mAsyncTaskDao;

        insertAsyncTask(DownloadedFileDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DownloadedFile... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }


    }

    private static class insertStudDataAsyncTask extends AsyncTask<StudentData, Void, Void> {
        private StudentDao mstudAsyncTaskDao;

        insertStudDataAsyncTask(StudentDao dao) {
            mstudAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final StudentData... params) {
            mstudAsyncTaskDao.insertStudentData(params[0]);
            return null;
        }
    }
}

