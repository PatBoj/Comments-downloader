package Plugins;

public class LechPoznanForumPl extends PlugIn
{
	static String description = "Forum drużyny Lech Poznań";
	static String adress = "http://forum.hejlech.pl/";
	
	public LechPoznanForumPl(String urlAdress, String fromDate, String toDate, String kind)
    {
    	super(urlAdress, fromDate, toDate, kind, 0, description, adress);
    }
	
	public String getDescription() {return description;}
	public String getAdress() {return adress;}

	public void runLechPoznanForumPl()
	{
		user = "div.post_wrap span.author, h3.guest";
	    date = "abbr.published";
	    content = "div.post";
	    userStatus = "ul.user_fields > li:first-child span.fc";
	    repliedTo = 0;
	    
	    topicPages = "div.topic_controls";
	    additionalPages = "page__st__";
	    multiplyPages = 20;
	    
	    sectionPages = topicPages;
	    urlTopic = "tr.row1 a[class='topic_title'], tr.row2 a[class='topic_title']";
	    additionalSection = "page__prune_day__100__sort_by__Z-A__sort_key__last_post__topicfilter__all__st__";
	    multiplySection = 30;
	    
	    urlSection = "h4 > a, li.newposts > a";
	    
	    fromPage = 0;
	    fromSectionPage = fromPage;
	    ownText = false;
	    type = "Guests";
	    
	    runPlugIn();
	}
}