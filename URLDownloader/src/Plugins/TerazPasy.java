package Plugins;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Scanner;

import org.jsoup.Jsoup;

import Tools.Comment;
import Tools.Connector;
import Tools.DateFormat;
import Tools.Source;
import Tools.Thread;

/**
 * Class for downloading threads and comments dedicated to "terazpasy.pl" portal.
 * It has methods for getting subpages number, "walking" through subpages of archive of the portal, entering every thread of a subpage and downloading comments.
 * To use this class it is necessary to specify period of time when comments to download were added.
 * @author	Mikołaj Synowiec
 * @since	2016-08-08
 */
public class TerazPasy extends ArticleWebsite
{
	/**
	 * Constructor of TerazPasy class extending super class constructor.
	 * @param website Corresponds to website field.
	 * @throws IOException Result of connecting to the portal.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to database.
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
	public TerazPasy(String website) throws IOException, ClassNotFoundException, SQLException
	{
		super(website);
		
		type = 1;
		abstrct = "Strona poświęcona klubowi piłkarskiemu Cracovia Kraków";
		
		source = new Source(type, abstrct, this.website);
		conn = new Connector();
		sourceId = conn.sourceToDatabase(source);
	}
	
	/**
     * Method countering number of the portal's archive subpages.
     * @return number of all subpages of the portal's archive.
     * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
     * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Scanner.html">java.util.Scanner documentation</a>
     */
	public int checkWebsiteSubpagesNumber()
	{
		subpages = urlParent.select("span.other"); //Selects all links from main page of the portal's archive.
		subpagesNumber = Integer.parseInt(subpages.last().text()); //Getting number of the last subpage from its text.
        
		return subpagesNumber;
	}
	
	/**
	 * Method "walking" through subpages of the portal's archive.
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
		outerLoop:
		for (int ii = 0; ii < checkWebsiteSubpagesNumber(); ii++)
		{
			@SuppressWarnings("resource")
			Scanner subpageChangingScanner = new Scanner(website).useDelimiter("[^0-9]+");
            website = website.replaceAll(Integer.toString(subpageChangingScanner.nextInt()), Integer.toString(ii * 10)); //Changing url aka "walking" through subpages - there is only one
            																											 //difference between next url addresses: int number.
            urlParent = Jsoup.connect(website).timeout(0).get();
            
            fromDate = new DateFormat(from).getDate(); //Converts input strings into Calendar-type fields.
        	toDate = new DateFormat(to).getDate();
        	
        	articles = urlParent.select("h2 a"); //This object depends on website html architecture.
        	
        	for (int jj = 0; jj < articles.size(); jj++)
        	{
        		articlesAbsHref = articles.get(jj).attr("abs:href");
        		
        		articleUrl = Jsoup.connect(articlesAbsHref).timeout(0).get();
        		
            	date = articleUrl.select("p.date").get(0).text(); //Used to save date of a thread.
            	DateFormat dateFormat = new DateFormat(date); //Formatting date string via DateFormat class.
            	
            	dateComparision = new DateFormat(from);
            	
            	if (dateFormat.compareDateLowerBound(dateComparision.getDate())) break outerLoop; //Stops data downloading when meets too old thread.
            	
            	heading = articleUrl.select("div.attribute-header").select("h1").get(0); //Used to save title of an article.
            	articleParts = articleUrl.select("p, li"); //Used to save text of an article.
            	
            	title = heading.text();
            	article = "";
            	rawArticle = "";
            	
            	for (int kk = 18;  kk < articleParts.size(); kk++) //Adding parts of article contained in articleParts variable (kk = 18 eliminates trash).
            	{
            		if (articleParts.get(kk).text().contains("Zaloguj się lub załóż nowe konto.")) break; //That the means end of article.
            		article = article + articleParts.get(kk).text();
            		rawArticle = rawArticle + articleParts.get(kk).outerHtml();
            	}
            	
            	thread = new Thread(sourceId, articlesAbsHref, title, article, rawArticle, dateFormat.displayDate("dd.MM.yyyy, HH:mm:ss"));
            	conn = new Connector();
            	threadReturnCluster = conn.threadToDatabase(thread);
            	
            	threadId = threadReturnCluster.threadId;
            	ifNew = threadReturnCluster.ifNew;
            	
    			commentDownloading(articlesAbsHref, fromDate, toDate);
        	}
        	
        	System.out.println("TerazPasy: subpage " + (ii + 1) + " downloaded!"); //Monitoring progress.
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
	public void commentDownloading(String articlesURL, Calendar fromDate, Calendar toDate) throws IOException, SQLException, ClassNotFoundException
	{
		articleUrl = Jsoup.connect(articlesURL).timeout(0).get();
		
		users = articleUrl.select("p.author");
        dates = articleUrl.select("p.date");
        contents = articleUrl.select("div.attribute-message"); //All these objects depend on website html architecture.
        
        int iterator = Math.min(users.size(), (Math.min(dates.size(), contents.size()))); //Prevents list out of bound errors: iteration until the end of the shortest list.
        
        for (int ll = 0; ll < iterator; ll++)
        {
        	periodCheck = new DateFormat(dates.get(ll).ownText()); //Variable created for every comment, which is compared with acquisition period of time.
        	
        	if (periodCheck.compareDate(fromDate, toDate)) //Checking if comment is from specified period of time.
        	{
        		int userType = 0; //Temporary variable which determines type of a user equal 0, because only registered users can add comments on this website (see Comment class).
            	
            	int idRepliedTo = 0; //Temporary variable to save id of a "parent" comment equal 0, because there is no information about answers.
            	withReply = false;
            	
            	DateFormat dateFormat = new DateFormat(dates.get(ll + 1).text()); //Formatting date string via DateFormat class.
            	
            	comment = new Comment(threadId, contents.get(ll).text(), contents.get(ll).outerHtml(),
            			dateFormat.displayDate("dd.MM.yyyy, HH:mm:ss"), users.get(ll + 1).text(), userType, idRepliedTo);
            	conn = new Connector();
            	commentId = conn.commentToDatabase(comment, ifNew, withReply);
        	}
        }
	}
}
