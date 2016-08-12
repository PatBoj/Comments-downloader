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
 * Class for downloading threads and comments dedicated to "legia.net" portal.
 * It has methods for getting subpages number, "walking" through subpages of archive of the portal, entering every thread of a subpage and downloading comments.
 * To use this class it is necessary to specify period of time when comments to download were added.
 * @author	Mikołaj Synowiec
 * @since	2016-08-08
 */
public class LegiaNet extends ArticleWebsite
{
	/**
	 * Constructor of LegiaNet class extending super class constructor.
	 * @param website Corresponds to website field.
	 * @throws IOException Result of connecting to the portal.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to database.
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
	public LegiaNet(String website) throws IOException, ClassNotFoundException, SQLException
	{
		super(website);
		
		type = 2;
		abstrct = "Strona poświęcona klubowi piłkarskiemu Legia Warszawa oraz odpowiadające jej froum";
		
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
		subpages = urlParent.select("a"); //Selects all links from main page of the portal's archive.
		subpagesAbsHref = subpages.get(subpages.size()- 3).attr("abs:href"); //Getting link for the last subpage of the portal's archive (it has index equal list size - 3).
		
		@SuppressWarnings("resource")
		Scanner subpagesNumberScanner = new Scanner(subpagesAbsHref).useDelimiter("[^0-9]+");
		subpagesNumber = subpagesNumberScanner.nextInt(); //Getting number of the last subpage from its url address.
        
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
            website = website.replaceAll(Integer.toString(subpageChangingScanner.nextInt()), Integer.toString(ii + 1)); //Changing url aka "walking" through subpages - there is only one
            																											//difference between next url addresses: int number.
           	urlParent = Jsoup.connect(website).timeout(0).get();
           	
           	articlesBlock = urlParent.getElementById("center");
        	articles = articlesBlock.select("a");
        	commentsNumber = articlesBlock.select("p.nowdate"); //All theses objects depend on website html architecture.
        	
        	fromDate = new DateFormat(from).getDate(); //Converts input strings into Calendar-type fields.
        	toDate = new DateFormat(to).getDate();
        	
            for (int jj = 0; jj < articles.size(); jj++)
            {
            	if (articles.get(jj).text().contains("Wszystkie tematy")) //If text of a link from articles list is "Wszystkie tematy" it means that it is the first link that is not an
            		jj = (articles.size() - 1);							  //article and it is necessary to break the loop.
            	
            	else
            	{
            		@SuppressWarnings("resource")
					Scanner commentsNumberScanner = new Scanner(commentsNumber.get(jj).text()).useDelimiter("[^0-9]+");
            																											//Checking number of comments in the thread.
            		if (commentsNumberScanner.nextInt() != 0)														    //If it is equal zero there is no need
            		{																									//to use commentDownloading method.
            			articlesAbsHref = articles.get(jj).attr("abs:href");
            		
            			@SuppressWarnings("resource")
            			Scanner articleIdScanner = new Scanner(articlesAbsHref).useDelimiter("[^0-9]+");
            			id = articleIdScanner.nextInt(); //Necessary to eliminate errors connected with specified articles (id = 52888 or id = 60607) - see commentDownloading method.
            		
            			articlesAbsHref = articlesAbsHref.replaceAll("forum", "news"); //Converting articles to more suitable look.
            		
            			articleUrl = Jsoup.connect(articlesAbsHref).timeout(0).get();
            			
                    	date = articleUrl.select("div.data").text(); //Used to save date of a thread.
                    	DateFormat dateFormat = new DateFormat(date); //Formatting date string via DateFormat class.
                    	
                    	dateComparision = new DateFormat(from);
                    	
                    	if (dateFormat.compareDateLowerBound(dateComparision.getDate())) break outerLoop; //Stops data downloading when meets too old thread.
                    
            			heading = articleUrl.getElementById("naglowek_rss").child(0); //Used to save title of an article.
            			intro = articleUrl.getElementById("news2").select("div.zajawka");
            			articleParts = articleUrl.getElementById("news2").select("p"); //Used to save text of an article.
            		
                    	title = heading.getElementsByTag("h4").text();
                    	article = intro.text();
                    	rawArticle = intro.outerHtml();
                    
                    	for (int kk = 0; kk < articleParts.size(); kk++) //Adding parts of article contained in articleParts variable.
                    	{
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
            	}
            }
            
            System.out.println("LegiaNet: subpage " + (ii + 1) + " downloaded!"); //Monitoring progress.
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

        users = articleUrl.select("div.user");
        dates = articleUrl.select("div.date");
        contents = articleUrl.select("div.content"); //All these objects depend on website html architecture.
        
        //Tags have the same type as comments (content), but in addition they are links.
        int shift = 0; 										//Temporary variable which acts as a gap between iterating through users, dates and contents in case of existing tags.
        if (contents.select("a").size() != 0) shift = 1; 	//If there are tags it is necessary to make a gap - then tags will not be included.
        
        answerButtons = articleUrl.select("div.odp"); //Used to determine if comment is an answer to some other comment.
        
        int iterator = Math.min(users.size(), (Math.min(dates.size(), contents.size()))); //Prevents list out of bound errors: iteration until the end of the shortest list.
        
        if ((id == 52888) || (id == 60607)) iterator =- 1; //For threads which have id = 52888 or id = 60607 occurs an error with the last comment so it has to be skipped.
        
        int idLastReplied = 0; //Temporary variable which acts as id of "parent" comment (comment can be a reply to a "parent" comment).
        
        for (int ll = 0; ll < iterator; ll++)
        {
        	periodCheck = new DateFormat(dates.get(ll).ownText()); //Variable created for every comment, which is compared with acquisition period of time.
        	
        	if (periodCheck.compareDate(fromDate, toDate)) //Checking if comment is from specified period of time.
        	{
        		int userType; //Temporary variable which determines type of a user.
            	if (users.get(ll).text().startsWith("~")) userType = 0; //"~" before user name stands for unregistered on this website. 
            	else userType = 1;
            	
            	int idRepliedTo; //Temporary variable to save id of a "parent" comment (takes 0 if comment is not an answer).
            	
            	if (answerButtons.get(ll).text().contains("Odpowiedz")) //If element of answerButtons contains "Odpowiedz" it means
            															//that comment corresponding to this element is not an answer.
            	{
            		idRepliedTo = 0; //idRepliedTo = 0 means that comment is not an answer.
            		withReply = false;
            	}
            	
            	else
            	{
            		idRepliedTo = idLastReplied; //If comment is an answer it means that it is answer to the previous comment which was not an answer.
            		withReply = true;
            	}
            	
            	DateFormat dateFormat = new DateFormat(dates.get(ll).ownText()); //Formatting date string via DateFormat class.
            	
            	comment = new Comment(threadId, contents.get(ll + shift).text(), contents.get(ll + shift).outerHtml(),
            			dateFormat.displayDate("dd.MM.yyyy, HH:mm:ss"), users.get(ll).text(), userType, idRepliedTo);
            	conn = new Connector();
            	commentId = conn.commentToDatabase(comment, ifNew, withReply);
            	
            	if (answerButtons.get(ll).text().contains("Odpowiedz"))
            		idLastReplied = commentId; //It is possible that actual comment which is not an answer will have some answers,
										       //so it is assumed that next comment will be an answer to this one.
        	}
        }
	}
}
