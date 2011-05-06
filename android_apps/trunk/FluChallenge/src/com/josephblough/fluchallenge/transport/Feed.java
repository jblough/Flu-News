package com.josephblough.fluchallenge.transport;

import java.util.ArrayList;
import java.util.List;

public class Feed {

    public String title;
    public String description;
    public String link;
    public String logoUrl;
    public List<String> categories = new ArrayList<String>();
    public List<FeedEntry> items = new ArrayList<FeedEntry>();
}
