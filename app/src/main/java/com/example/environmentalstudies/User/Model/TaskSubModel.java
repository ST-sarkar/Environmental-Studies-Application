package com.example.environmentalstudies.User.Model;

public class TaskSubModel {

    String subDate;
    String pdfUri;
    String remark;

    public TaskSubModel(String subDate, String pdfUri, String remark) {
        this.subDate = subDate;
        this.pdfUri = pdfUri;
        this.remark = remark;
    }

    public TaskSubModel() {
    }

    public String getSubDate() {
        return subDate;
    }

    public void setSubDate(String subDate) {
        this.subDate = subDate;
    }

    public String getPdfUri() {
        return pdfUri;
    }

    public void setPdfUri(String pdfUri) {
        this.pdfUri = pdfUri;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
