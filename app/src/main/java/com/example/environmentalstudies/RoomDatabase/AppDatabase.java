package com.example.environmentalstudies.RoomDatabase;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DownloadedFile.class,StudentData.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DownloadedFileDao downloadedFileDao();
    public abstract StudentDao studentDao();

    // Make the AppDatabase a singleton to prevent having multiple instances of the database opened at the same time.
    private static volatile AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration() // Strategy for migration if needed.
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

