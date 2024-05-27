package com.example.environmentalstudies.RoomDatabase;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "downloaded_files")
public class DownloadedFile {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fileName;

    public String topic;

    public String description;

    public String type;

    public DownloadedFile(String fileName,String topic,String description,String type) {
        this.fileName = fileName;
        this.topic=topic;
        this.description=description;
        this.type=type;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTopic() {
        return topic;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }
}

