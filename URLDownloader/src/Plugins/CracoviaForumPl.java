package Plugins;

public class CracoviaForumPl extends PlugIn
{
	static String description = "Forum drużyny Cracovia Kraków";
	static String adress = "http://www.cracovia.krakow.pl/";
	
	public CracoviaForumPl(String urlAdress, String fromDate, String toDate, String kind)
    {
    	super(urlAdress, fromDate, toDate, kind, 0, description, adress);
    }
	
	public String getDescription() {return description;}
	public String getAdress() {return adress;}

	public void runCracoviaForumPl()
	{
		user = "div.message-author";
	    date = "div.generic small";
	    content = "div.message-body";
	    repliedTo = 0;
	    
	    topicPages = "div.nav > div.paging";
	    additionalPages = ",page=";
	    
	    sectionPages = topicPages;
	    urlTopic = "h4 > a";
	    additionalSection = additionalPages;
	    
	    urlSection = "h3 > a";
	    substringPageNumber = 11;
	    substringSectionNumber = substringPageNumber;
	    ownDateText = true;
	    
	    runPlugIn();
	}
}
