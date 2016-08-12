package Plugins;

public class WislaKrakowForumCom extends PlugIn
{    
	static String description = "Forum drużyny Wisła Kraków";
	static String adress = "http://www.wislakrakow.com/forum";
	
	public WislaKrakowForumCom(String urlAdress, String fromDate, String toDate, String kind)
    {
    	super(urlAdress, fromDate, toDate, kind, 0, description, adress);
    }
	
	public String getDescription() {return description;}
	public String getAdress() {return adress;}

	public void runWislaKrakowForumCom()
	{
		user = "a.bigusername";
	    date = "div[style*='padding-top: 2px']";
	    content = "div.post_msg[style*=padding-bottom]";
	    repliedTo = 0;
	    
	    topicPages = "td.vbmenu_control";
	    additionalPages = "&page=";
	    
	    sectionPages = topicPages;
	    urlTopic = "div > a[href*='showthread']:first-of-type";
	    additionalSection = "&order=desc&page=";
	    
	    urlSection = "div > a[href*='forumdisplay']";
	    whatVerse = 0;
	    substringPageNumber = 11;
	    substringSectionNumber = substringPageNumber;
	    gluedThreadsExeption = "img[alt*='Przylepiony temat']";
	    
	    runPlugIn();
	}
}