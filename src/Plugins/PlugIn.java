package Plugins;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Tools.Comment;
import Tools.Connector;
import Tools.DateFormat;
import Tools.Source;
import Tools.Thread;

public class PlugIn extends AbstractPlugIn
{   
    String user; // Html tags uses to extract user names from html document.
    String date; // Html tags uses to extract date from html document.
    String content; // Html tags uses to extract comment from html document.
    String userStatus; // Html tags uses to extract user type from html document. (0 - registered, 1 - unregistered, 2 - unknown)
    String gluedThreadsExeption; // Html tags uses to extract glued thread from section.
    
    int repliedTo; // Comment number to which this comment is a reply.
    String topicPages; // Html tags uses to extract number of topic pages from html document.
    
    /* Storage the additional string to add in order to visit all pages.
     * Example:
     * First page: "http://forum.legionisci.com/showthread.php?t=9022&page=1"
     * Second page: "http://forum.legionisci.com/showthread.php?t=9022&page=2"
     * So additional string will be "&page=". 
     */
    String additionalPages;
    
    /* Storage the number to multiply last integer of page to go to next page.
     * Example:
     * First page: "http://pilkarskieforum.com/viewtopic.php?f=5&t=8368&start=40"
     * Second page: "http://pilkarskieforum.com/viewtopic.php?f=5&t=8368&start=80"
     * If we want to go to next page we must multiply iterator by 40. 
     */
    int multiplyPages;
    
    String sectionPages; // Html tags uses to extract number of section pages from html document.
    String urlTopic; // Html tags uses to extract topic's html addresses.
    String additionalSection; // Storage the additional string to add in order to visit all section pages.
    int multiplySection; // Storage the number to multiply last integer of page to go to next section page.

    String urlSection; // Html tags uses to extract sections's html addresses.
    
    /*
     * From what point we need to substring to get page numbers.
     * Example:
     * We get: "Strona 1 z 134" so we need to substring from 11 char.
     */
    int substringPageNumber;
    int substringSectionNumber; // Same as substringPageNumber.
    int whatVerse; // Which element storage information about page number
	boolean ownText; // If we want to ownText set this as true
	boolean ownDateText; // If we want to ownText in date set this as true.
	int fromTopicUrl; // Exception for first page.
	int fromPage; // If page starts from 0 or 1.
	int fromSectionPage; // If section page starts from 0 or 1.
	String type; // User type. For example "Guest".
	boolean theLastOne; // If number of pages equals number of elements in array.
	
	/*
	 * Constructor of class PlugIn.
	 * kind - all, section, topic, page, article
	 */
    protected PlugIn(String urlAddress, String fDate, String tDate, String kind, int type, String description, String mainUrlAddress)
    {
    	this.urlAddress = urlAddress;
    	htmlDocument = urlConnect(urlAddress);
    	topicTitle = htmlDocument.title();
    	
    	String[] from = fDate.split("\\.");
    	String[] to = tDate.split("\\.");
    	
    	datesArray = new ArrayList<DateFormat>();
    	fromDate = new GregorianCalendar(Integer.parseInt(from[2]), Integer.parseInt(from[1])-1, Integer.parseInt(from[0]));
    	fromDate.set(Calendar.HOUR_OF_DAY, 0);
    	fromDate.set(Calendar.MINUTE, 0);
    	fromDate.set(Calendar.SECOND, 1);
    	toDate = new GregorianCalendar(Integer.parseInt(to[2]), Integer.parseInt(to[1])-1, Integer.parseInt(to[0]));
    	kindOfPage = Choice.valueOf(kind.toUpperCase());
    	idPost = 0;
    	userStatus = "";
    	gluedThreadsExeption = "";
    	
    	fromPage = 1;
    	fromSectionPage = 1;
    	substringPageNumber = 0;
    	substringSectionNumber = 0;
    	multiplySection = 1;
    	multiplyPages = 1;
    	whatVerse = 1;
    	ownText = true;
    	ownDateText = false;
    	fromTopicUrl = 0;
    	theLastOne = false;
    	
    	s = new Source(type, description, mainUrlAddress);
    	saveToDatabase(s);
    }
    
    // Connecting to url address and return a Document object.
    protected Document urlConnect(String urlAddress)
    {
    	Document htmlDoc;
    	/* Try to connect to page. If there will be no connection in 2 minutes then
    	 * program stops.
    	 * It uses agent "Chrome" because some of pages has a bot's security.
    	 */
		try {htmlDoc = Jsoup.connect(urlAddress).timeout(0).userAgent("Chrome").get();} 
    	catch (IOException e) 
    	{
    		System.out.println("Error while contecing with page.");
			e.printStackTrace();
			htmlDoc = null;
			System.exit(0);
    	}
		return htmlDoc;
    }
    
    // Gets number of pages.
    private int getPages(String tag, Document htmlDoc, int substring)
    {
    	temps = htmlDoc.select(tag);
    	
    	if (temps.size() != 0) // If no elements detected.
    	{
    		if(ownDateText) // Sometimes we need to use ownText to extract date.
    		{
    			if (substring == 0) // If we need to cut string a little bit.
        		{
        			if (theLastOne) return Integer.valueOf(("0" + temps.get(temps.size()-1).ownText()).replaceAll("(\\d*).*", "$1")); // Search for first integer.
        			else return Integer.valueOf(("0" + temps.get(whatVerse).ownText()).replaceAll("(\\d*).*", "$1"));
        		}
        		else 
        		{
        			try {return Integer.parseInt(temps.get(whatVerse).ownText().substring(substring).replaceAll("(\\d*).*", "$1"));}
        			catch(NumberFormatException e) {return 1;}
        		}
    		}
    		else
    		{
    			if (substring == 0) 
        		{
        			if (theLastOne) return Integer.valueOf(("0" + temps.get(temps.size()-1).text()).replaceAll("(\\d*).*", "$1"));
        			else return Integer.valueOf(("0" + temps.get(whatVerse).text()).replaceAll("(\\d*).*", "$1"));
        		}
        		else 
        		{
        			try {return Integer.parseInt(temps.get(whatVerse).text().substring(substring).replaceAll("(\\d*).*", "$1"));}
        			catch(NumberFormatException e) {return 1;}
        		}
    		}
    		
    	}
    	else return 1; // Return 1 if there is no maximum pages information on page.
    }

    // Gets number of pages on sections.
    protected int getSectionPages(Document htmlDoc) {return getPages(sectionPages, htmlDoc, substringSectionNumber);}
    
    // Gets number of pages on topics.
    protected int getTopicPages(Document htmlDoc) {return getPages(topicPages, htmlDoc, substringPageNumber);}    
    
    // Gets section's urls in Elements object.
    protected Elements getSectionsUrl(Document htmlDoc) {return htmlDoc.select(urlSection);}
   
    // Gets topic's urls in Element object.
    protected Elements getTopicsUrl(Document htmlDoc) {return htmlDoc.select(urlTopic);}
    
    // Checking user status.
    protected int userStatus(String user)
    {
    	if(user.equals(type)) return 1;
    	else return 0;
    }
    
    // Saving comment to Database.
    protected void saveToDatabase(Comment c)
    {
    	aConnector = new Connector();
    	try {commentId = aConnector.commentToDatabase(c, ifNew, false);} 
    	catch (ClassNotFoundException e1) 
    	{
    		System.out.println("Error, class not found");
    		e1.printStackTrace();
    		System.exit(0);
    	}
    	catch (SQLException e2)
    	{
    		System.out.println("Error while connecting to Database");
    		e2.printStackTrace();
    		System.exit(0);
    	}
    }
    
    // Saving thread information to database.
    protected void saveToDatabase(Thread t)
    {
    	aConnector = new Connector();
    	try {trc = aConnector.threadToDatabase(t); threadId = trc.threadId; ifNew = trc.ifNew;} 
    	catch (ClassNotFoundException e1) 
    	{
    		System.out.println("Error, class not found");
    		e1.printStackTrace();
    		System.exit(0);
    	}
    	catch (SQLException e2)
    	{
    		System.out.println("Error while connecting to Database");
    		e2.printStackTrace();
    		System.exit(0);
    	}
    }
    
    // Saving source information to database.
    protected void saveToDatabase(Source s)
    {
    	aConnector = new Connector();
    	try {sourceId = aConnector.sourceToDatabase(s);} 
    	catch (ClassNotFoundException e1) 
    	{
    		System.out.println("Error, class not found");
    		e1.printStackTrace();
    		System.exit(0);
    	}
    	catch (SQLException e2)
    	{
    		System.out.println("Error while connecting to Database");
    		e2.printStackTrace();
    		System.exit(0);
    	}
    }
    
    // Downloads all content from page (topics and sections).
    protected void downloadAll()
    {
    	urlSections = getSectionsUrl(htmlDocument); // Gets urls of sections.
    	for (int i=0; i<urlSections.size(); i++)
    	{
    		setUrlAddress(urlSections.get(i).attr("abs:href"));
    		htmlDocument = urlConnect(urlAddress);
    		downloadSection(htmlDocument, urlAddress);
    	}
    }
    
    protected void downloadSection(Document htmlDocument, String urlParent)
    {
    	int sectionPages = getSectionPages(htmlDocument); // Gets section pages.
    	int k = 0;
    	for (int i=0; i<sectionPages; i++)
    	{
    		setUrlAddress(urlParent + additionalSection + Integer.toString(multiplySection*(i+fromSectionPage)));
    		htmlDocument = urlConnect(urlAddress);
    		urlTopics = getTopicsUrl(htmlDocument);
    		if (gluedThreadsExeption != "") {temp = htmlDocument.select(gluedThreadsExeption); fromTopicUrl = temp.size();}
    		for (int j=k; j<urlTopics.size(); j++)
    		{
    			/*TEST*/ System.out.println("Section " + (i+1) + " of " + sectionPages + ", Topic " + (j+1) + " of " + urlTopics.size());
        		setUrlAddress(urlTopics.get(j).attr("abs:href"));
        		htmlDocument = urlConnect(urlAddress);
        		downloadTopic(htmlDocument, urlAddress);
        		k = fromTopicUrl;
    		}
    	}
    }
    
    protected void downloadSection() {downloadSection(htmlDocument, urlAddress);}
    
    protected void downloadTopic(Document htmlDocument, String urlParent)
    {
    	idPost = 0;
    	lastTopicPage = getTopicPages(htmlDocument);
    	for (int i=0; i<lastTopicPage; i++)
    	{
    		setUrlAddress(urlParent + additionalPages + Integer.toString(multiplyPages*(i+fromPage)));
    		htmlDocument = urlConnect(urlAddress);
    		downloadSinglePage(htmlDocument);
    		/*TEST*/ System.out.println("Downloading new topic: " + getUrlTitle(htmlDocument));
    		/*TEST*/ if (i != 0 && (i+1)%25 == 0) System.out.println("Downloaded " + (i+1) + " pages of " + lastTopicPage);
    		/*TEST*/ if (i+1 == lastTopicPage) System.out.println("Downloaded last page " + (i+1));
    	}
    }
    
    protected void downloadTopic() {downloadTopic(htmlDocument, urlAddress);}
    
    protected void downloadSinglePage(Document htmlDocument)
    {
    	rawUsers = htmlDocument.select(user);
        rawDates = htmlDocument.select(date);
        rawContents = htmlDocument.select(content);
        int iterator = Math.min(rawUsers.size(), (Math.min(rawDates.size(), rawContents.size())));
        
        // Convert string into DateFormat object.
        if (ownText) for (int i=0; i<rawDates.size(); i++) datesArray.add(new DateFormat(rawDates.get(i).ownText()));
        else for (int i=0; i<rawDates.size(); i++) datesArray.add(new DateFormat(rawDates.get(i).text()));
        	
        if(!userStatus.equals(""))rawUserStatus = htmlDocument.select(userStatus); // Searching for user status.
        int status;        
        
        // Saving first post or article in thread.
    	if (idPost == 0) 
    	{
    		if(ownText) t = new Thread(sourceId, getUrlAddress(), getUrlTitle(htmlDocument), rawContents.get(0).ownText(), rawContents.get(0).outerHtml(), datesArray.get(0).displayDate());
    		else t = new Thread(sourceId, getUrlAddress(), getUrlTitle(htmlDocument), rawContents.get(0).text(), rawContents.get(0).outerHtml(), datesArray.get(0).displayDate());
    		saveToDatabase(t);
    		idPost++;
    	}
        
        loop: for(int i=1; i<iterator; i++)
        {
        	// Default user status is 0.
        	if(userStatus.equals("")) status = 0;
        	else status = userStatus(rawUserStatus.get(i).text());

        	if (datesArray.get(i).compareDateUpperBound(toDate)) break loop; // From the oldest post to the latest.
        	
        	if (ownText) aComment = new Comment(threadId, rawContents.get(i).ownText(), rawContents.get(i).outerHtml(), datesArray.get(i).displayDate(), rawUsers.get(i).text(), status, repliedTo);
        	else aComment = new Comment(threadId, rawContents.get(i).text(), rawContents.get(i).outerHtml(), datesArray.get(i).displayDate(), rawUsers.get(i).text(), status, repliedTo);
        	
        	idPost++;
        	
        	if (datesArray.get(i).compareDate(fromDate, toDate)) saveToDatabase(aComment); // Saving to database.
        }
        datesArray.clear(); // Clear dates array.
    }
    
    protected void downloadSinglePage() {downloadSinglePage(htmlDocument);}
    
    protected void runPlugIn()
    {
    	switch (kindOfPage)
    	{
    		case PAGE: downloadSinglePage(); break;
    		case TOPIC: downloadTopic(); break;
    		case SECTION: downloadSection(); break;
    		case ALL: downloadAll(); break;
    		default: downloadAll(); break;
    	}
    }
    
    protected String getUrlAddress() {return urlAddress;}
    protected String getUrlTitle(Document htmDoc) {return htmDoc.title();}
    protected void setUrlAddress(String urlAddress) {this.urlAddress = urlAddress;} // Sets new url address.
}
