package com.example.environmentalstudies.User.Model;

public class NoteDataModel {
    private String uri;
    private String topic,desc;
    private String filename;
    private String uploadDate;
    private String type;
    private boolean book;
    public NoteDataModel(String uri, String topic, String desc, String filename, String uploadDate,String type,boolean book) {
        this.uri = uri;
        this.topic = topic;
        this.desc = desc;
        this.filename = filename;
        this.uploadDate=uploadDate;
        this.type=type;
        this.book=book;
    }

    public NoteDataModel() {
    }

    public boolean isBook() {
        return book;
    }

    public void setBook(boolean book) {
        this.book = book;
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
