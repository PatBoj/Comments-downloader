package Plugins;

public class PogonForumPl extends PlugIn
{
	static String description = "Forum drużyny Pogoń Szczecin";
	static String adress = "http://forum.pogononline.pl/";
	
	public PogonForumPl(String urlAdress, String fromDate, String toDate, String kind)
    {
    	super(urlAdress, fromDate, toDate, kind, 0, description, adress);
    }
	
	public String getDescription() {return description;}
	public String getAdress() {return adress;}

	public void runPogonForumPl()
	{
		user = "a[class='nagl3'], a[class='nagl3b']";
	    date = "td.nagl3[valign='top'] a:eq(1), td.nagl3b[valign='top'] a:eq(1)";
	    content = "td[valign='top']";
	    repliedTo = 0;
	    
	    topicPages = "a[class='nagl1b']";
	    additionalPages = "&strona=";
	    
	    sectionPages = "table[cellspacing='0'] td[class='nagl1']:first-of-type";
	    urlTopic = "div.lista a[class='nagl3']:first-of-type";
	    additionalSection = additionalPages;
	    
	    urlSection = "td[class='nagl3'] a[class='nagl3']";
	    
	    theLastOne = true;
	    whatVerse = 0;
	    substringSectionNumber = 11;
	    
	    runPlugIn();
	}
}