package com.example.classroom_location;

public class Student{

    private String name;

    private int imageId;

    private int col;

    private int row;

    private String url;

    private String message;

    private String status;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Student(String name, int imageId, int row, int col) {
        this.name = name;
        this.imageId = imageId;
        this.row = row;
        this.col = col;
        this.url = null;
        this.message = null;
        this.status = "0";
    }

    public Student(String name, int row, int col, String url, String message, String status){
        this.name = name;
        this.row = row;
        this.col = col;
        if (url != null){
            this.url = url;
            this.imageId = -1;
        }
        if (message != null){
            this.message = message;
        }
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }


}
