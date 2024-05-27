package com.example.environmentalstudies.Admin.Model;

public class PPTUpModel {
    private String uri;
    private String topic,desc;
    private String filename;
    private String uploadDate;
    private String type;
    public PPTUpModel(String uri, String topic, String desc, String filename, String uploadDate,String type) {
        this.uri = uri;
        this.topic = topic;
        this.desc = desc;
        this.filename = filename;
        this.uploadDate=uploadDate;
        this.type=type;
    }

    public PPTUpModel() {
    }

    public String getType() {
        return type;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getUri() {
        return uri;
    }

    public String getTopic() {
        return topic;
    }

    public String getDesc() {
        return desc;
    }

    public String getFilename() {
        return filename;
    }
}
