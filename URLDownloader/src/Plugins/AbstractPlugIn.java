package Plugins;
import java.util.ArrayList;
import java.util.Calendar;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Tools.Comment;
import Tools.Connector;
import Tools.DateFormat;
import Tools.Source;
import Tools.Thread;
import Tools.ThreadReturnCluster;

abstract class AbstractPlugIn 
{
	protected String urlAddress;
	protected Document htmlDocument;
	protected String topicTitle;
	
	protected Elements rawDates;
	protected Elements rawUsers;
	protected Elements rawContents;
	protected Elements rawUserStatus;
	protected Elements temp;
	
	protected Elements urlTopics;
	protected Elements urlSections;
	protected Elements temps;
    
	protected ArrayList<DateFormat> datesArray;  //converted date
    
	protected Calendar fromDate;
	protected Calendar toDate;
    
	protected Comment aComment;
	protected Source s;
	protected Thread t;
	protected Connector aConnector;
	
	public static int sourceId;
	public static int threadId;
	public static int commentId;
	public ThreadReturnCluster trc;
	public static boolean ifNew;
    
	protected enum Choice {PAGE, TOPIC, SECTION, ALL};
	protected Choice kindOfPage;
    
	protected int lastTopicPage;
	protected int lastSectionPage;
	protected int idPost;
	
	protected abstract Document urlConnect(String urlAdress);
	
    protected abstract void downloadAll();
	protected abstract void downloadSection();
	protected abstract void downloadTopic();
	protected abstract void downloadSinglePage();
	protected abstract void downloadSinglePage(Document htmlDoc);
	
	protected abstract int getSectionPages(Document htmlDoc);
	protected abstract int getTopicPages(Document htmlDoc);
	
	protected abstract Elements getSectionsUrl(Document htmlDoc);
	protected abstract Elements getTopicsUrl(Document htmlDoc);
	
	protected abstract void runPlugIn();
}
