package com.example.environmentalstudies.Admin.Model;

public class TaskUpload {
    String deadline,publishdate;
    String title,desc;

    public TaskUpload(String deadline,String publishdate, String title, String desc) {
        this.deadline = deadline;
        this.publishdate=publishdate;
        this.title = title;
        this.desc = desc;
    }


    public TaskUpload() {
    }

    public String getPublishdate() {
        return publishdate;
    }

    public void setPublishdate(String publishdate) {
        this.publishdate = publishdate;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
