package Plugins;

public class FanatikOgiComPl extends PlugIn
{
	static String description = "Strona poświęcona klubowi piłkarskiemu Śląsk Wrocław";
	static String adress = "http://fanatik.ogicom.pl/";
	
	public FanatikOgiComPl(String urlAdress, String fromDate, String toDate, String kind)
    {
    	super(urlAdress, fromDate, toDate, kind, 0, description, adress);
    }
	
	public String getDescription() {return description;}
	public String getAdress() {return adress;}

	public void runFanatikOgiComPl()
	{
		user = "section.content strong:last-of-type, span.author";
	    date = "time.entry-date, time[pubdate]";
	    content = "section.content";
	    repliedTo = 0;
	    
	    topicPages = "";
	    additionalPages = "";
	    
	    sectionPages = "li.counter";
	    urlTopic = "h2 a[rel='bookmark']";
	    additionalSection = "&paged=";
	    
	    urlSection = "";
	    
	    substringSectionNumber = 10;
	    whatVerse = 0;
	    ownText = false;
	    
	    runPlugIn();
	}
}
