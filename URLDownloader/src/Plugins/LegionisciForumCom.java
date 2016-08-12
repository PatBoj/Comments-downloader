package Plugins;

public class LegionisciForumCom extends PlugIn
{
	static String description = "Forum drużyny Legia Warszawa";
	static String adress = "http://forum.legionisci.com/";
	
	public LegionisciForumCom(String urlAdress, String fromDate, String toDate, String kind)
    {
    	super(urlAdress, fromDate, toDate, kind, 0, description, adress);
    }
	
	public String getDescription() {return description;}
	public String getAdress() {return adress;}

	public void runLegionisciForumCom()
	{
		user = "div.username_container strong, div.username_container:only-of-type";
	    date = "div.posthead";
	    content = "div.content";
	    userStatus = "span.usertitle";
	    repliedTo = 0;
	    
	    topicPages = "a.popupctrl";
	    additionalPages = "&page=";
	    
	    sectionPages = topicPages;
	    urlTopic = "h3 > a";
	    additionalSection = additionalPages;
	    
	    urlSection = "h2.forumtitle > a";
	    
	    substringPageNumber = 11;
	    substringSectionNumber = substringPageNumber;
	    ownText = false;
	    gluedThreadsExeption = "img[alt*='przyklejony']";
	    type = "Guest";
	    
	    runPlugIn();
	}
}