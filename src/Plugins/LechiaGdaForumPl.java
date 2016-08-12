package Plugins;

public class LechiaGdaForumPl extends PlugIn
{
	static String description = "Forum drużyny Legia Gdańsk";
	static String adress = "http://lechia.gda.pl/forum/";
	
	public LechiaGdaForumPl(String urlAdress, String fromDate, String toDate, String kind)
    {
    	super(urlAdress, fromDate, toDate, kind, 0, description, adress);
    }
	
	public String getDescription() {return description;}
	public String getAdress() {return adress;}

	public void runLechiaGdaForumPl()
	{
		user = "div.aut_new";
	    date = "div.dat_new";
	    content = "div.tre";
	    repliedTo = 0;
	    
	    topicPages = "h2";
	    additionalPages = "";
	    
	    sectionPages = topicPages;
	    urlTopic = "tr.row_1 > td.wat_1 > a:eq(0), tr.row_2 > td.wat_1 > a:eq(0)";
	    additionalSection = additionalPages;
	    
	    substringPageNumber = 9;
	    substringSectionNumber = substringPageNumber;
	    whatVerse = 0;
	    
	    switch(kindOfPage) 
	    {
	    case ALL: kindOfPage = Choice.SECTION; break;
	    default: break;
	    }
	    
	    runPlugIn();
	}
}
