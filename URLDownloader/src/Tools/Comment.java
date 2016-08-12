package Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Class used for storage and formatting data corresponding to comment-type.
 * @author	Mikołaj Synowiec
 * @since	2016-07-05
 */
public class Comment
{
	/**
	 * Identification number of a thread which contains this comment and comment to which this comment is a reply.
	 */
	public int threadId, idRepliedTo;
	
	/**
	 * Text of a comment without html markers, comment's raw text and date and hour of comment's appearance, name of a user who added a comment and date and hour of downloading a comment.
	 */
	public String text, rawText, date, user, timeStamp;
	
	/**
	 * Type of a user who added a comment (either 0 - registered, 1 - unregistered or 2 - unknown).
	 */
	public int userType;
	
	/**
	 * Constructor of Comment class.
	 * @param threadId Corresponds to threadId field;
	 * @param text Corresponds to text field.
	 * @param rawText Corresponds to rawText field.
	 * @param date Corresponds to date field.
	 * @param user Corresponds to user field.
	 * @param userType Corresponds to userType field.
	 * @param idRepliedTo Corresponds to idRepliedTo field.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">java.text.SimpleDateFormat documentation</a>
	 */
	public Comment(int threadId, String text, String rawText, String date, String user, int userType, int idRepliedTo)
	{
		this.threadId = threadId;
		this.text = text.replaceAll("'", "`"); //"'" causes errors during SQL querying, so it has to be replaced.
		this.rawText = rawText.replaceAll("'", "`");
		this.date = date;
		this.user = user.replaceAll("'", "`");
		this.userType = userType;
		this.idRepliedTo = idRepliedTo;
		
		timeStamp = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(Calendar.getInstance().getTime()); //Getting date and hour of downloading a comment.
	}
	
	/**
	 * Method used to format fields of a comment-type object in order to send it to a database.
	 * @return Formatted string which is ready to be send to a database.
	 */
	public String commentToSave()
	{
		String formatedComment = new String(Integer.toString(threadId) + ", '" + text + "', '" +
				rawText + "', '" + date + "', '" + user + "', '" + timeStamp + "', " + Integer.toString(userType)); //"'" separator is necessary for creating SQL statement
		return formatedComment;
	}
	
	/**
	 * Method used to format fields of a comment-type object in order to send it to a database (including id of "parent" comment).
	 * @return Formatted string which is ready to be send to a database.
	 */
	public String commentToSaveWithReply()
	{
		String formatedComment = new String(Integer.toString(threadId) + ", '" + text + "', '" +
				rawText + "', '" + date + "', '" + user + "', " + Integer.toString(idRepliedTo) +
				", '" + timeStamp + "', " + Integer.toString(userType)); //"'" separator is necessary for creating SQL statement);
		return formatedComment;
	}
}