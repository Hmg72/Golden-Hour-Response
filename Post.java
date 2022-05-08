package com.example.sarahshaikh.goldenhourresponse;
/**
* Created by Sarah Shaikh on 13-01-2018.
*/
public class Post {
private String Location;
private String Image;
private String TOA;
private String NOC;
public Post() {
}
public Post(String location, String image, String toa, String noc) {
this.Location = location;
this.Image = image;
this.TOA = toa;
this.NOC = noc;
}
public String getLocation() {
return Location;
}
public void setLocation(String location) {
Location = location;
}
public String getTOA() {
return TOA;
}
public void setTOA(String toa) {
TOA = toa;
}
public String getNOC() {
return NOC;
}
  public void setNOC(String noc) {
NOC = noc;
}
public String getImage() {
return Image;
}
public void setImage(String image) {
Image = image;
}
}
