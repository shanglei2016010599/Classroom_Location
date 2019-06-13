package com.example.classroom_location;

public class User {

    private String id;

    private String name;

    private String url;

    private String location;

    private String message;

    private String status;

    private String count;

    private String account;

    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public User(String id, String account, String password, String name, String url, String message,
                String status, String count, String location){
        this.id = id;
        this.account = account;
        this.password = password;
        this.name = name;
        if (url != null){
            this.url = url;
        }
        if (url != null){
            this.message = message;
        }
        this.status = status;
        this.count = count;
        if (location != null){
            this.location = location;
        }
    }

}
