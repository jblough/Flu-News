package com.josephblough.fluchallenge.transport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.josephblough.fluchallenge.reports.FluReport;

import android.text.format.DateUtils;
import android.util.Log;

@SuppressWarnings("unchecked")
public class DataRetriever {
    private final static String TAG = "DataRetriever";

    private static final String FLU_VACCINATION_ESTIMATES_URL = "http://www.cdc.gov/flue/professionals/vaccination/reporti1011/resources/2010-11_Coverage.xls";
    private static final String WEEKLY_FLU_ACTIVITY_REPORT_URL = "http://www.cdc.gov/flu/weekly/flureport.xml";
    private static final String FLU_PAGES_AS_XML_URL = "http://t.cdc.gov/feed.aspx?tpc=26829&days=90";
    private static final String FLU_PAGES_AS_JSON_URL = "http://t.cdc.gov/feed.aspx?tpc=26829&days=90&fmt=json";
    private static final String FLU_UPDATES_URL = "http://www2c.cdc.gov/podcasts/createrss.asp?t=r&c=20";
    private static final String FLU_PODCASTS_URL = "http://www2c.cdc.gov/podcasts/searchandcreaterss.asp?topic=flue";
    private static final String CDC_FEATURES_PAGES_AS_XML_URL = "http://t.cdc.gov/feed.aspx?tpc=26816&fromdate=1/1/2011";
    private static final String CDC_FEATURES_PAGES_AS_JSON_URL = "http://t.cdc.gov/feed.aspx?tpc=26816&fromdate=1/1/2011&fmt=json";
    
    
    public void getFluVaccinationEstimates() {
	// Excel spreadsheet
	HttpClient client = new DefaultHttpClient();
	HttpGet get = new HttpGet(FLU_VACCINATION_ESTIMATES_URL);
	try {
	    HttpResponse response = client.execute(get);
	    Log.d(TAG, "Spreadsheet has " + response.getEntity().getContentLength() + " bytes");
	} catch (IOException e) {
	    Log.e(TAG, e.getMessage(), e);
	}
    }
    
    public FluReport getFluActivityReport() {
	Log.d(TAG, "Flu activity report:");
	try {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpMethod = new HttpGet(WEEKLY_FLU_ACTIVITY_REPORT_URL);
	    HttpResponse response = client.execute(httpMethod);
	    HttpEntity entity = response.getEntity();

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    WeeklyFluActivityXmlHandler handler = new WeeklyFluActivityXmlHandler();
	    reader.setContentHandler(handler);
	    reader.parse(new InputSource(entity.getContent()));
	    Log.d(TAG, "Report: " + handler.report.subtitle);
	    Log.d(TAG, "Report has " + handler.report.periods.size() + " time periods");
	    return handler.report;
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new FluReport();
    }
    
    public /*List<SyndEntry>*/SyndicatedFeed getFluPagesAsXml() {
	Log.d(TAG, "Flu pages:");
	//return retrieveXmlRssFeed(FLU_PAGES_AS_XML_URL);
	try {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpMethod = new HttpGet(FLU_PAGES_AS_XML_URL);
	    HttpResponse response = client.execute(httpMethod);
	    HttpEntity entity = response.getEntity();

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    SyndicatedFeedXmlHandler handler = new SyndicatedFeedXmlHandler();
	    reader.setContentHandler(handler);
	    reader.parse(new InputSource(entity.getContent()));
	    Log.d(TAG, "Report: " + handler.feed.title);
	    Log.d(TAG, "Report has " + handler.feed.items.size() + " items");
	    return handler.feed;
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new SyndicatedFeed();
    }
    
    public /*JSONArray*/List<SyndicatedFeedEntry> getFluPagesAsJson() {
	Log.d(TAG, "Flu pages:");
	return retrieveJsonRssFeed(FLU_PAGES_AS_JSON_URL);
    }
    
    public /*List<SyndEntry>*/Feed getFluUpdates() {
	Log.d(TAG, "Flu updates:");
	//return retrieveXmlRssFeed(FLU_UPDATES_URL);
	try {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpMethod = new HttpGet(FLU_UPDATES_URL);
	    HttpResponse response = client.execute(httpMethod);
	    HttpEntity entity = response.getEntity();

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    FluUpdatesFeedXmlHandler handler = new FluUpdatesFeedXmlHandler();
	    reader.setContentHandler(handler);
	    reader.parse(new InputSource(entity.getContent()));
	    Log.d(TAG, "Report: " + handler.feed.title);
	    Log.d(TAG, "Report has " + handler.feed.items.size() + " items");
	    return handler.feed;
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new Feed();
    }
    
    public /*List<SyndEntry>*/Feed getFluPodcasts() {
	Log.d(TAG, "Flu podcasts:");
	//return retrieveXmlRssFeed(FLU_PODCASTS_URL);
	try {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpMethod = new HttpGet(FLU_PODCASTS_URL);
	    HttpResponse response = client.execute(httpMethod);
	    HttpEntity entity = response.getEntity();

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    FluPodcastsFeedXmlHandler handler = new FluPodcastsFeedXmlHandler();
	    reader.setContentHandler(handler);
	    reader.parse(new InputSource(entity.getContent()));
	    Log.d(TAG, "Report: " + handler.feed.title);
	    Log.d(TAG, "Report has " + handler.feed.items.size() + " items");
	    return handler.feed;
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new Feed();
    }
    
    public /*List<SyndEntry>*/SyndicatedFeed getCdcFeaturesPagesAsXml() {
	Log.d(TAG, "CDC Features pages:");
	//return retrieveXmlRssFeed(CDC_FEATURES_PAGES_AS_XML_URL);
	try {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpMethod = new HttpGet(CDC_FEATURES_PAGES_AS_XML_URL);
	    HttpResponse response = client.execute(httpMethod);
	    HttpEntity entity = response.getEntity();

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    SyndicatedFeedXmlHandler handler = new SyndicatedFeedXmlHandler();
	    reader.setContentHandler(handler);
	    reader.parse(new InputSource(entity.getContent()));
	    Log.d(TAG, "Report: " + handler.feed.title);
	    Log.d(TAG, "Report has " + handler.feed.items.size() + " items");
	    return handler.feed;
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new SyndicatedFeed();
    }
    
    public /*JSONArray*/List<SyndicatedFeedEntry> getCdcFeaturesPagesAsJson() {
	Log.d(TAG, "CDC Features pages:");
	return retrieveJsonRssFeed(CDC_FEATURES_PAGES_AS_JSON_URL);
    }
    
    private List<SyndEntry> retrieveXmlRssFeed(final String url) {
	try {
	    URL feedUrl = new URL(url);
	    SyndFeedInput input = new SyndFeedInput();
	    SyndFeed feed = input.build(new XmlReader(feedUrl));
	    /*Log.d(TAG, "SyndFeed Array has " + feed.getEntries().size() + " elements");
	    List<SyndEntry> entries = (List<SyndEntry>)feed.getEntries();
	    for (SyndEntry entry : entries) {
		Log.d(TAG, entry.getTitle());
	    }*/
	    Log.d(TAG, "categories:");
	    for (Object category : feed.getCategories()) {
		Log.d(TAG, "   category: " + category.toString());
	    }
	    Log.d(TAG, "entries:");
	    List<SyndEntry> entries = (List<SyndEntry>)feed.getEntries();
	    for (SyndEntry entry : entries) {
		Log.d(TAG, "   title: " + entry.getTitle());
		Log.d(TAG, "   description: " + entry.getDescription().getValue());
		Log.d(TAG, "   enclosures: " + entry.getEnclosures());
		Log.d(TAG, "   contents: " + entry.getContents());
	    }
	    return feed.getEntries();
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new ArrayList<SyndEntry>();
    }
    
    private /*JSONArray*/List<SyndicatedFeedEntry> retrieveJsonRssFeed(final String url) {
	HttpClient client = new DefaultHttpClient();
	HttpGet get = new HttpGet(url);
	try {
	    ResponseHandler<String> handler = new BasicResponseHandler();
	    String response = client.execute(get, handler);
	    JSONArray objects = new JSONArray(response);
	    List<SyndicatedFeedEntry> entries = new ArrayList<SyndicatedFeedEntry>();
	    int length = objects.length();
	    for (int i=0; i<length; i++) {
		/*JSONObject object = objects.getJSONObject(i);
		JSONArray catalogItems = object.getJSONArray("CatalogItem");
		//Log.d(TAG, "CatalogItem = " + item.getJSONObject(0).toString());
		JSONObject contentItem = catalogItems.getJSONObject(0).getJSONArray("ContentItem").getJSONObject(0);
		Log.d(TAG, "Title: " + contentItem.getString("Title"));
		JSONArray topics = contentItem.getJSONArray("Topics").getJSONObject(0).getJSONArray("Topic");
		int topicCount = topics.length();
		for (int j=0; j<topicCount; j++) {
		    Log.d(TAG, "   " + topics.getJSONObject(j).getString("TopicName"));
		}*/
		entries.add(convertFromJson(objects.getJSONObject(i)));
	    }
	    Log.d(TAG, "There are " + entries.size() + " entries");
	    return entries;
	} catch (JSONException e) {
	    Log.e(TAG, e.getMessage(), e);
	} catch (IOException e) {
	    Log.e(TAG, e.getMessage(), e);
	} catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new ArrayList<SyndicatedFeedEntry>();
    }
    
    private SyndicatedFeedEntry convertFromJson(JSONObject json) throws JSONException {
	SyndicatedFeedEntry entry = new SyndicatedFeedEntry();

	JSONArray catalogItems = json.getJSONArray("CatalogItem");
	JSONObject contentItem = catalogItems.getJSONObject(0).getJSONArray("ContentItem").getJSONObject(0);
	
	Log.d(TAG, contentItem.getString("Title"));
	entry.date = contentItem.getString("CreatedDateTime");

	try {
	    Long date = Long.parseLong(entry.date.replace("/Date(", "").replace(")/", ""));
	    Log.d(TAG, contentItem.getString("CreatedDateTime") + " : " + date + " = " + new Date(date).toString());
	    /*Long time = Long.parseLong(entry.date.replace("/Date(", "").replace(")/", ""));
	    Date date = new Date(time.longValue());
	    Log.d(TAG, entry.date + " to " + time + " as date " + date.toString());
	    Log.d(TAG, "Date: " + new SimpleDateFormat().format(date));*/
/*
	    GsonBuilder gsonb = new GsonBuilder();
	    DateDeserializer ds = new DateDeserializer();
	    gsonb.registerTypeAdapter(Date.class, ds);
	    Gson gson = gsonb.create();
	     
	    JSONObject j;
	    Date date = null;
	     
	    try
	    {
	        date = gson.fromJson("{\"ModifiedDateTime\":\"" + contentItem.getString("ModifiedDateTime").toString() + "\"}", Date.class);
	        Log.d(TAG, date.toString());
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }*/
	
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	
	entry.description = contentItem.optString("Description");
	entry.guid = contentItem.getString("GUID");
	entry.link = contentItem.getString("PersistentUrl");
	entry.title = contentItem.getString("Title");

	JSONArray topics = contentItem.getJSONArray("Topics").getJSONObject(0).getJSONArray("Topic");
	int topicCount = topics.length();
	for (int j=0; j<topicCount; j++) {
	    JSONObject jsonTopic = topics.getJSONObject(j);
	    SyndicatedFeedEntryTopic topic = new SyndicatedFeedEntryTopic();
	    topic.id = jsonTopic.getInt("TopicId");
	    topic.name = jsonTopic.getString("TopicName");
	    entry.topics.add(topic);
	}
	
	return entry;
    }
    
    public class DateDeserializer implements JsonDeserializer<Date> {
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
	throws JsonParseException {
	    String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
	    Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
	    Matcher matcher = pattern.matcher(json.getAsJsonPrimitive().getAsString());
	    String result = matcher.replaceAll("$2");

	    return new Date(new Long(result));
	}
    }
}
