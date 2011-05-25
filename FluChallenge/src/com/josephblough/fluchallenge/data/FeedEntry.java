package com.josephblough.fluchallenge.data;


public class FeedEntry {

    public String title;
    public String description;
    public String link;
    public String guid;
    public String date; // TODO - make this a date
    
    @Override
    public String toString() {
	return title + "\\" + date;
    }
}
