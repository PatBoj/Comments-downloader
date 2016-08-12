package Tools;

/**
 * Class used for storage and formatting data corresponding to thread-type.
 * @author	Mikołaj Synowiec
 * @since	2016-07-05
 */
public class Thread
{
	/**
	 * Identification number of a thread's source.
	 */
	public int sourceId;
	
	/**
	 * Url address of a thread.
	 */
	public String link;
	
	/**
	 * Title, text, raw text and date of an article.
	 */
	public String title, article, rawArticle, date;
	
	/**
	 * Constructor of Thread class.
	 * @param sourceId Corresponds to sourceId field.
	 * @param link Corresponds to link field.
	 * @param title Corresponds to title field.
	 * @param article Corresponds to article field.
	 * @param rawArticle Corresponds to rawArticle field.
	 * @param date Corresponds to date field.
	 */
	public Thread(int sourceId, String link, String title, String article, String rawArticle, String date)
	{
		this.sourceId = sourceId;
		this.link = link;
		this.title = title.replaceAll("'", "`"); //"'" causes errors during SQL querying, so it has to be replaced.
		this.article = article.replaceAll("'", "`");
		this.rawArticle = rawArticle.replaceAll("'", "`");
		this.date = date;
	}
	
	/**
	 * Method used to format fields of a thread-type object in order to send it to a database.
	 * @return Formatted string which is ready to be send to a database.
	 */
	public String threadToSave()
	{
		String formatedThread = new String("'" + link + "', '" + title + "', '" + article + "', '" + rawArticle + "', '"
				+ date + "', " + Integer.toString(sourceId));//"'" separator is necessary for creating SQL statement.
		return formatedThread;
	}
}