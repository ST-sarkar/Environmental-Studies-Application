package com.example.environmentalstudies.User.Model;

public class DownloadModel {
   private String filename,topic,desc;

    public DownloadModel(String filename, String topic, String desc) {
        this.filename = filename;
        this.topic = topic;
        this.desc = desc;
    }

    public String getFilename() {
        return filename;
    }

    public String getTopic() {
        return topic;
    }

    public String getDesc() {
        return desc;
    }
}
