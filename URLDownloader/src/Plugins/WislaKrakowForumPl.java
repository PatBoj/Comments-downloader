package Plugins;

public class WislaKrakowForumPl extends PlugIn
{    
	static String description = "Forum drużyny Wisła Kraków";
	static String adress = "http://skwk.pl/skwkforum";
	
	public WislaKrakowForumPl(String urlAdress, String fromDate, String toDate, String kind)
    {
    	super(urlAdress, fromDate, toDate, kind, 0, description, adress);
    }
	
	public String getDescription() {return description;}
	public String getAdress() {return adress;}

	public void runWislaKrakowForumPl()
	{
		user = "dl > dt";
	    date = "p.author";
	    content = "div.content";
	    repliedTo = 0;
	    
	    topicPages = "a[title='Kliknij, aby skoczyć do strony…'] strong";
	    additionalPages = "start,";
	    multiplyPages = 15;
	    fromPage = 0;
	    
	    sectionPages = topicPages;
	    urlTopic = "dt > a[class='topictitle']";
	    additionalSection = additionalPages;
	    multiplySection = 30;
	    fromSectionPage = fromPage;
	    
	    urlSection = "a.forumtitle";
	    ownDateText = true;
	    
	    runPlugIn();
	}
}