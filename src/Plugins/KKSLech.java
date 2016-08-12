package Plugins;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import Tools.Comment;
import Tools.Connector;
import Tools.DateFormat;
import Tools.Source;
import Tools.Thread;

/**
 * Class for downloading threads and comments dedicated to "kkslech.com" portal.
 * It has methods for getting subpages number, "walking" through subpages of archive of the portal, entering every thread of a subpage and downloading comments.
 * To use this class it is necessary to specify period of time when comments to download were added.
 * @author	Mikołaj Synowiec
 * @since	2016-08-08
 */
public class KKSLech extends ArticleWebsite
{
	/**
	 * Constructor of KKSLech class extending super class constructor.
	 * @param website Corresponds to website field.
	 * @throws IOException Result of connecting to the portal.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to database.
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
	public KKSLech(String website) throws IOException, ClassNotFoundException, SQLException
	{
		super(website);
		
		type = 1;
		abstrct = "Strona poświęcona klubowi piłkarskiemu Lech Poznań";
		
		source = new Source(type, abstrct, this.website);
		conn = new Connector();
		sourceId = conn.sourceToDatabase(source);
	}
	
	/**
     * Method countering number of the portal's archive subpages.
     * @return number of all subpages of the portal's archive.
	 * @throws IOException Result of connecting to the portal.
     * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
     * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Scanner.html">java.util.Scanner documentation</a>
     */
	public int checkWebsiteSubpagesNumber() throws IOException
	{
		boolean condition = true;
		subpagesNumber = 0;
		
		String websiteTmp = website; //Copy of a link to main page of the portal.
		Document urlTmp; //Used for connecting with the following subpages.
		
		while (condition)
		{
			@SuppressWarnings("resource") //"Walking" trough subpages.
			Scanner subpageChangingScanner = new Scanner(websiteTmp).useDelimiter("[^0-9]+");
			websiteTmp = websiteTmp.replaceAll(Integer.toString(subpageChangingScanner.nextInt()), Integer.toString(subpagesNumber + 1));	
			
			urlTmp = Jsoup.connect(websiteTmp).timeout(0).get();
			
			nextPage = urlTmp.select("li[class='next right']"); //Finds "next page" button on the website.
			condition = nextPage.text().contains("Następna strona"); //If there is a "next page" button loop is still running and it means that there is at least one more subpage.
			
			subpagesNumber += 1;
		}
		
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
		subpagesNumber = checkWebsiteSubpagesNumber();
		
		outerLoop:
		for (int ii = 0; ii < subpagesNumber; ii++)
		{
			@SuppressWarnings("resource")
			Scanner subpageChangingScanner = new Scanner(website).useDelimiter("[^0-9]+");
            website = website.replaceAll(Integer.toString(subpageChangingScanner.nextInt()), Integer.toString(ii + 1)); //Changing url aka "walking" through subpages - there is only one
            																											//difference between next url addresses: int number.
            urlParent = Jsoup.connect(website).timeout(0).get();
            
            fromDate = new DateFormat(from).getDate(); //Converts input strings into Calendar-type fields.
        	toDate = new DateFormat(to).getDate();
            
            articles = urlParent.select("div[class='post-inner post-hover']"); //There are two types of posts, so it is necessary to split for loop into two for loops.
            
            for (int jj = 1; jj < (articles.size() - 1); jj++) //First and last post of this kind is the "news of the week" so it has to be ignored.
            {
            	if (articles.get(jj).select("i[class='fa fa-comments-o']").size() != 0) //That means there is a field with number of comments - it is a post which can have comments.
            	{
            		if (Integer.parseInt(articles.get(jj).select("a.post-comments").text().replaceAll(" ", "")) != 0)
            		{
            			articlesAbsHref = articles.get(jj).child(0).child(0).attr("abs:href");
            			articlesAbsHref = articlesAbsHref.replaceAll("#comments", "");
            			
            			date = articles.get(jj).select("p.post-date").text(); //Used to save date of a thread.
            			DateFormat dateFormat = new DateFormat(date); //Formatting date string via DateFormat class.
            			dateComparision = new DateFormat(from);
            			
            			if (articlesAbsHref == "http://kkslech.com/2016/01/10/smietnik-kibica-2016/") {} //Prevents from downloading data from "Śmietnik kibica" article which
            																							 //is not interestingin this case and has different html architecture.
            			else
            			{
                			articleUrl = Jsoup.connect(articlesAbsHref).timeout(0).get();

                			if (dateFormat.compareDateLowerBound(dateComparision.getDate())) break outerLoop; //Stops data downloading when meets too old thread.
                			
                			heading = articleUrl.select("h1.post-title").get(0); //Used to save title of an article.
                			articleParts = articleUrl.select("div[class='entry themeform']").select("p"); //Used to save text of an article.
                			
                			title = heading.text();
                			article = "";
                			rawArticle = "";
                			
                			for (int ll = 0; ll < (articleParts.size() - 4); ll++) //Adding parts of article contained in articleParts variable.
                			{
                				article = article + articleParts.get(ll).text();
                				rawArticle = rawArticle + articleParts.get(ll).outerHtml();
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
            }
            
            articles = urlParent.select("div[class='post-meta group mini-meta']"); //Second type of posts.
            
            for (int kk = 0; kk < articles.size(); kk++)
            {
            	if (articles.get(kk).select("i[class='fa fa-comments-o']").size() != 0)
            	{
            		if (Integer.parseInt(articles.get(kk).select("a.post-comments").text().replaceAll(" ", "")) != 0)
            		{
            			articlesAbsHref = articles.get(kk).child(0).attr("abs:href");
            			articlesAbsHref = articlesAbsHref.replaceAll("#comments", "");
            			
            			date = articles.get(kk).select("p.post-date").text();
            			DateFormat dateFormat = new DateFormat(date);
            			dateComparision = new DateFormat(from);
            			
            			if (articlesAbsHref == "http://kkslech.com/2016/01/10/smietnik-kibica-2016/") {}
            			
            			else
            			{
            				articleUrl = Jsoup.connect(articlesAbsHref).timeout(0).get();

                			if (dateFormat.compareDateLowerBound(dateComparision.getDate())) break outerLoop;
                			
                			heading = articleUrl.select("h1.post-title").get(0);
                			articleParts = articleUrl.select("div[class='entry themeform']").select("p");
                			
                			title = heading.text();
                			article = "";
                			rawArticle = "";
                			
                			for (int mm = 0; mm < (articleParts.size() - 4); mm++)
                			{
                				article = article + articleParts.get(mm).text();
                				rawArticle = rawArticle + articleParts.get(mm).outerHtml();
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
            }
            
            System.out.println("KKSLech: subpage " + (ii + 1) + " downloaded!"); //Monitoring progress.
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
		
		users = articleUrl.select("ol.commentlist").select("cite.fn");
		dates = articleUrl.select("ol.commentlist").select("div[class='comment-meta commentmetadata']").select("a");
		
		int iterator = Math.min(users.size(), dates.size());		
		
		int idLastReplied = 0; //Temporary variable which acts as id of "parent" comment (comment can be a reply to a "parent" comment).
		
		for (int nn = 0; nn < iterator; nn++)
		{
			periodCheck = new DateFormat(dates.get(nn).ownText()); //Variable created for every comment, which is compared with acquisition period of time.
			
			if (periodCheck.compareDate(fromDate, toDate)) //Checking if comment is from specified period of time.
			{
				contents = articleUrl.select("ol.commentlist").select("div.comment-body").get(nn).select("p"); //All these objects depend on website html architecture.
				String content = ""; //Temporary variable for connecting parts of a comment and raw comment.
				String rawContent = "";
				
				for (int oo = 0; oo < contents.size(); oo++)
				{
					content = content + contents.get(oo).text();
					rawContent = rawContent + contents.get(oo).outerHtml();
				}
				
				int userType = 2; //Temporary variable which determines type of a user equal 2, because there is no information about user type on this website (see Comment class).
				
				int idRepliedTo = 0; //Temporary variable to save id of a "parent" comment (takes 0 if comment is not an answer).
				
				if (articleUrl.select("ol.commentlist").select("div.comment-body").get(nn).select("div.reply").size() != 0) //If size of this list is not equal 0 it means that
																															//comment corresponding to this element is not an answer.
				{
            		idRepliedTo = 0; //idRepliedTo = 0 means that comment is not an answer.
            		withReply = false;
            	}
				
				else
				{
					idRepliedTo = idLastReplied; //If comment is an answer it means that it is answer to the previous comment which was not an answer.
					withReply = true;
				}
				
				DateFormat dateFormat = new DateFormat(dates.get(nn).ownText()); //Formatting date string via DateFormat class.
            	
            	comment = new Comment(threadId, content, rawContent, dateFormat.displayDate("dd.MM.yyyy, HH:mm:ss"), users.get(nn).text(), userType, idRepliedTo);
            	conn = new Connector();
            	commentId = conn.commentToDatabase(comment, ifNew, withReply);
            	
            	if (articleUrl.select("ol.commentlist").select("div.comment-body").get(nn).select("div.reply").size() != 0)
            		idLastReplied = commentId; //It is possible that actual comment which is not an answer will have some answers,
											   //so it is assumed that next comment will be an answer to this one.
			}
		}
	}
}
