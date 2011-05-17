package com.josephblough.fluchallenge.data;

public class PodcastFeedEntry extends FeedEntry {

    public String duration;
    public int length;
    public String type;
    public String mp3url;
    
    @Override
    public String toString() {
	return title + "\\" + date + "\\" + duration;
    }
}
