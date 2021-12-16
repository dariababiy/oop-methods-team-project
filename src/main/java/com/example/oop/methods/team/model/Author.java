package com.example.oop.methods.team.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fullName;
    private Date birthDay;
    private String lang;
    private String country;

    public Author() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(this.birthDay);
    }

    public void setBirthDay(Date date) {
        this.birthDay = date;
    }

    public void setBirthDay(String birthDay) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.birthDay = sdf.parse(birthDay);
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "Author{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", birthDay='" + sdf.format(birthDay) + '\'' +
                ", lang='" + lang + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}