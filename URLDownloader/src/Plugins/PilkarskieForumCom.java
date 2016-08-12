package Plugins;

public class PilkarskieForumCom extends PlugIn
{    
	static String description = "Ogólnotematyczne forum piłkarskie";
	static String adress = "http://pilkarskieforum.com/";
	
	public PilkarskieForumCom(String urlAdress, String fromDate, String toDate, String kind)
    {
    	super(urlAdress, fromDate, toDate, kind, 0, description, adress);
    }
	
	public String getDescription() {return description;}
	public String getAdress() {return adress;}
	
	public void runPilkarskieForumCom()
	{
		user = "b.postauthor";
	    date = "td.gensmall > div[style*=right]";
	    content = "div.postbody";
	    repliedTo = 0;
	    
	    topicPages = "td.nav > strong";
	    additionalPages = "&start=";
	    multiplyPages = 40;
	    
	    sectionPages = topicPages;
	    urlTopic = "td.row1 > a";
	    additionalSection = additionalPages;
	    multiplySection = 30;
	    fromPage = 0;
	    fromSectionPage = fromPage;
	    
	    urlSection = "td.row1 > a";
	    
	    runPlugIn();
	}
}