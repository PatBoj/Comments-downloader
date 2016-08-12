package Plugins;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Scanner;

import org.jsoup.Jsoup;
import Tools.DateFormat;
import Tools.Source;
import Tools.Connector;
import Tools.Thread;
import Tools.Comment;

/**
 * Class for downloading threads and comments dedicated to "SlaskNet.com" portal.
 * It has methods for "walking" through articles of the portal's archive which enters every thread and downloads comments.
 * To use this class it is necessary to specify period of time when comments to download were added.
 * @author	Mikołaj Synowiec
 * @since	2016-08-08
 */
public class SlaskNet extends ArticleWebsite
{
	/**
	 * Constructor of SlaskNet class.
	 * @param website Corresponds to website field
	 * @throws IOException Result of connecting to the portal.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to database.
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
	public SlaskNet(String website) throws IOException, ClassNotFoundException, SQLException
	{
		super(website);
		
		type = 1;
		abstrct = "Strona poświęcona klubowi piłkarskiemu Śląsk Wrocław";
		
		source = new Source(type, abstrct, this.website);
		conn = new Connector();
		sourceId = conn.sourceToDatabase(source);
	}
	
	/**
     * Method countering number of the portal's archive subpages (articles).
     * @return number of all articles of the portal's archive.
     * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
     * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Scanner.html">java.util.Scanner documentation</a>
     */
	public int checkWebsiteSubpagesNumber()
	{
		articles = urlParent.getElementsByTag("article").select("a");
		newestArticleAbsHref = articles.get(0).attr("abs:href");
		
		@SuppressWarnings("resource")
		Scanner idOfNewestArticleScanner = new Scanner(newestArticleAbsHref).useDelimiter("[^0-9]+"); //Changing url aka "walking" through articles - there is only one
																									  //difference between next url addresses: int number.
		subpagesNumber = idOfNewestArticleScanner.nextInt(); //id of newest article is at the same time number of all articles.
        
		return subpagesNumber;
	}
	
	/**
	 * Method "walking" through articles of the portal's archive.
	 * It connects to every article's url and checks if the article contains comments.
	 * If there is at least one comment it proceeds to downloading comments via proper method.
	 * @param from Specifies "up" date of specified acquisition period of time.
	 * @param to Specifies "down" date of specified acquisition period of time.
	 * @throws IOException Result of connecting to the portal.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to database.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Scanner.html">java.util.Scanner documentation</a>
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
	public void dataDownloading(String from, String to) throws IOException, ClassNotFoundException, SQLException
	{
		fromDate = new DateFormat(from).getDate(); //Converts input strings into Calendar-type fields.
    	toDate = new DateFormat(to).getDate();
		
		for (int ii = checkWebsiteSubpagesNumber(); ii > 0; ii--)
		{
			articlesAbsHref = newestArticleAbsHref.replaceAll(Integer.toString(checkWebsiteSubpagesNumber()), Integer.toString(ii));
			articleUrl = Jsoup.connect(articlesAbsHref).timeout(0).get();
			
			dateFormat = new DateFormat(articleUrl.select("span[style='color: grey']").text()); //Used to save date of a thread formatted via DateFormat class.
			
			dateComparision = new DateFormat(from);
			
			if (dateFormat.compareDateLowerBound(dateComparision.getDate())) break; //Stops data downloading when meets too old thread.
			
			comments = articleUrl.select("div[style='padding:0 0 2em 0']");

    								 				//Checking number of comments in the thread.
    		if (comments.size() != 0)				//If it is equal zero there is no need to use commentDownloading method.
    		{
    			articleParts = articleUrl.getElementsByTag("p");
			
    			title = articleUrl.getElementsByTag("title").text(); //All theses objects depend on website html architecture.
    			article = "";
    			rawArticle = "";
			
    			for (int jj = 0; jj < articleParts.size(); jj++) //Adding parts of article contained in articleParts variable.
    			{
    				article = article + articleParts.get(jj).text();
    				rawArticle = rawArticle + articleParts.get(jj).outerHtml();
    			}
    			
    			thread = new Thread(sourceId, articlesAbsHref, title, article, rawArticle, dateFormat.displayDate("dd.MM.yyyy, HH:mm:ss"));
    			conn = new Connector();
    			threadReturnCluster = conn.threadToDatabase(thread);
            	
            	threadId = threadReturnCluster.threadId;
            	ifNew = threadReturnCluster.ifNew;
            	    			
            	commentDownloading(articlesAbsHref, fromDate, toDate);
    		}
    		
    		System.out.println("SlaskNet: article " + ii + " downloaded!"); //Monitoring progress.
		}
	}
	
	/**
	 * Method used to download all comments from one thread.
	 * @param articlesURL Url address of an article (from url address).
	 * @param fromDate Calendar-type variable which specifies "up" date of specified acquisition period of time.
	 * @param toDate Calendar-type variable which specifies "down" date of specified acquisition period of time.
	 * @throws IOException Result of connecting to the portal.
	 * @throws SQLException Result of connecting to database.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/lang/Math.html#min(int,%20int)">java.lang.Math.min(int, int) documentation</a>
	 */
	public void commentDownloading(String articlesURL, Calendar fromDate, Calendar toDate) throws IOException, ClassNotFoundException, SQLException
	{
		articleUrl = Jsoup.connect(articlesURL).timeout(0).get();
		
		users = articleUrl.select("span[style='font-weight:bold']");
		dates = articleUrl.select("span[style='font-size:10px']");
		contents = articleUrl.select("div[style='padding:.3em 0 0 .5em; border-left:3px solid #C3C3C3']"); //All these objects depend on website html architecture.
		
        int iterator = Math.min(users.size(), (Math.min(dates.size(), contents.size()))); //Prevents list out of bound errors: iteration until the end of the shortest list.
 
        for (int ll = 0; ll < iterator; ll++)
        {
        	periodCheck = new DateFormat(dates.get(ll).ownText()); //Variable created for every comment, which is compared with acquisition period of time.
        	
        	if (periodCheck.compareDate(fromDate, toDate)) //Checking if comment is from specified period of time.
        	{
        		int userType; //Temporary variable which determines type of a user.
            	if (users.get(ll).text().startsWith("~")) userType = 0; //"~" before user name stands for unregistered on this website. 
            	else userType = 1;
            	
            	DateFormat dateFormat = new DateFormat(dates.get(ll).ownText()); //Formatting date string via DateFormat class.
            	
            	comment = new Comment(threadId, contents.get(ll).text(), contents.get(ll).outerHtml(),
            			dateFormat.displayDate("dd.MM.yyyy, HH:mm:ss"), users.get(ll).text(), userType, 0); //idRepliedTo is always 0, because there is
            																								//no option like "answer" on this portal
            	conn = new Connector();
            	conn.commentToDatabase(comment, ifNew, false);
        	}
        }
	}
}
