package Plugins;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Tools.DateFormat;
import Tools.Connector;
import Tools.Source;
import Tools.Thread;
import Tools.Comment;
import Tools.ThreadReturnCluster;

/**
 * Abstract class for storing fields and methods which are used in specified portals plugins.
 * @author	Mikołaj Synowiec
 * @since	2016-08-08
 */
public abstract class ArticleWebsite
{
	/**
	 * Url addresses of the portal, subpage, article and newest article.
	 */
	public String website, subpagesAbsHref, articlesAbsHref, newestArticleAbsHref;
	
	/**
	 * Field specifying type of a source - required while initializing Source-class object.
	 */
	public int type;
	
	/**
	 * Field specifying shortened description of a source - required while initializing Source-class object.
	 */
	public String abstrct;
	
	/**
	 * Field (class Document imported from Jsoup library - for interpreting html) used for setting up a connecting with the portal and with article.
	 * @see <a href="https://jsoup.org/apidocs/org/jsoup/nodes/Document.html">jsoup.nodes.Document documentation</a>
	 */
	public Document urlParent, articleUrl;
	
	/**
	 * Field (class Element from Jsoup library) used to store block of comments on the subpage.
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
	public Element articlesBlock;
	
	/**
	 * Fields (class Elements from Jsoup library) used to store lists of subpages, articles on a subpage, number of comments in every article and "next page" buttons.
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
    public Elements subpages, articles, commentsNumber, nextPage;
    
    /**
     * Fields used for store number of subpages on the portal and id of an article (from url address of an article).
     */
    public int subpagesNumber, id;
    
    /**
     * Field (class Element from Jsoup library) used for storing heading of an article.
     * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
     */
	public Element heading;
	
	/**
	 * Fields (class Element from Jsoup library) used for storing parts of an article text and comment blocks.
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
	public Elements intro, articleParts, comments;
    
	/**
	 * Fields used to transfer title, text, raw text and date of an article into Thread-type field.
	 */
    public String title, article, rawArticle, date;
    
	/**
	 * Fields (class Elements from Jsoup library) used to store lists of user names who added a comment,
	 * dates of adding a comment, raw text of comments and information about existence of answer buttons.
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
	public Elements users, dates, contents, answerButtons;
    
    /**
     * Fields specifying acquisition period of time.
     * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html">java.util.Calendar documentation</a>
     */
    public Calendar fromDate, toDate;
	
	/**
	 * Fields used for formatting date and checking if comment and thread are from specified period of time.
	 */
	public DateFormat dateFormat, periodCheck, dateComparision;
	
    /**
     * Field used for connecting and sending data about source, threads and comments to a database.
     */
    public Connector conn;
    
    /**
     * Field used for keeping and sending to database data about source.
     */
    public Source source;
    
	/**
	 * Field used for keeping and sending to database data about single thread.
	 */
	public Thread thread;
	
	/**
	 * Field used for keeping and sending to database data about single comment.
	 */
	public Comment comment;
	
	/**
	 * Fields used for storing id of a source, thread and comment.
	 */
	public static int sourceId, threadId, commentId;
	
	/**
	 * Field used as a container for data returning via threadToDatabase method (class Connector).
	 */
	public ThreadReturnCluster threadReturnCluster;
	
	/**
	 * Field used for storing information about novelty of a thread.
	 */
	public static boolean ifNew, withReply;
	
	/**
	 * Constructor of ArticleWebsite class.
	 * @param website Corresponds to website field.
	 * @throws IOException Result of connecting to the portal.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to database.
	 * @see <a href="https://jsoup.org/apidocs/">Jsoup documentation</a>
	 */
	public ArticleWebsite(String website) throws IOException, ClassNotFoundException, SQLException
	{
		this.website = website;
		urlParent = Jsoup.connect(this.website).timeout(0).get(); //timeout(0) means infinite timeout (practically about 2 minutes).
	}
	
	/**
	 * Method countering number of the portal's archive subpages
	 * @throws IOException Result of connecting to the portal.
	 * @return number of all subpages of the portal's archive.
	 */
	abstract public int checkWebsiteSubpagesNumber() throws IOException;
	
	/**
	 * Method "walking" through subpages of the portal's archive.
	 * @param from Specifies "up" date of specified acquisition period of time.
	 * @param to Specifies "down" date of specified acquisition period of time.
	 * @throws IOException Result of connecting to the portal.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to database.
	 */
	abstract public void dataDownloading(String from, String to) throws IOException, ClassNotFoundException, SQLException;
	
	/**
	 * Method used to download all comments from one thread.
	 * @param articlesURL Url address of an article (from url address).
	 * @param fromDate Calendar-type variable which specifies "up" date of specified acquisition period of time.
	 * @param toDate Calendar-type variable which specifies "down" date of specified acquisition period of time.
	 * @throws IOException Result of connecting to the portal.
	 * @throws SQLException Result of connecting to database.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 */
	abstract public void commentDownloading(String articlesURL, Calendar fromDate, Calendar toDate) throws IOException, SQLException, ClassNotFoundException;
}
