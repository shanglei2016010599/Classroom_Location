package com.example.classroom_location;

public class Student{

    private String name;

    private int imageId;

    private int col;

    private int row;

    public Student(String name, int imageId, int row, int col) {
        this.name = name;
        this.imageId = imageId;
        this.row = row;
        this.col = col;
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
