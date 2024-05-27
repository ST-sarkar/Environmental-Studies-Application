package com.example.environmentalstudies.Admin.Model;

public class UserProfile {
    String Name,Phone,Address, Username,Password;

    public UserProfile(String name, String phone, String address, String username, String password) {
        this.Name = name;
        this.Phone = phone;
        this.Address = address;
        this.Username = username;
        this.Password = password;
    }

    public UserProfile() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
