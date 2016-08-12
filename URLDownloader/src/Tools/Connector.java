package Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class used for connecting and sending data from URLDownloader to database.
 * It has three methods serving as transmitters for different data types (objects of a class Source, Thread and Comment).
 * @author	Mikołaj Synowiec
 * @since	2016-08-08
 */
public class Connector
{
	/**
	 * Connection-type field used to connecting to a specified database.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/sql/Connection.html">java.sql.Connection documentation</a>
	 */
	public Connection conn;
	
	/**
	 * Field necessary for creating and executing SQL statement.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html">java.sql.Statement documentation</a>
	 */
	public Statement statement;
	
	/**
	 * Field used to transfer text of a query into SQL statement.
	 */
	public String checkIfExists;
	
	/**
	 * Field returning results from SQL querying.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html">java.sql.ResultSet documentation</a>
	 */
	public ResultSet resultSet;
	
	/**
	 * Default constructor of a class Connector with no arguments.
	 */
	public Connector()
	{
		conn = null;
	}
	
	/**
	 * Method used to send data of source-type to a database.
	 * @param source Object of class Source which is to be send to a database.
	 * @return Id of a source;
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to database.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html">java.sql.Statement documentation</a>
	 */
	public int sourceToDatabase(Source source) throws ClassNotFoundException, SQLException
	{
		int sourceId; //Variable used for returning value of source's id.
		
		try
		{
			Class.forName("org.postgresql.Driver"); //Getting driver from "org.postgresql.Driver".
			conn = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "postgres"); //Connecting to a database.
			statement = conn.createStatement();
			
			checkIfExists = "SELECT id_src FROM sources WHERE link = '" + source.link + "'"; //Checking if specified source.
			
			statement.execute(checkIfExists);
			resultSet = statement.getResultSet();
			
			if (resultSet.next()) //If source already exists method should return its id.
				sourceId = resultSet.getInt(1);
			
			else //If source doesn't exist yet, it has to be created and method should return its id.
			{
				statement.execute("INSERT INTO sources (type, abstract, link) VALUES (" + source.sourceToSave() + ");"); //Inserting source into database.
				statement.execute(checkIfExists);
				resultSet = statement.getResultSet();
				resultSet.next();
				sourceId = resultSet.getInt(1);
			}
		}
		
		finally
		{
			if (conn != null) conn.close();
		}
		
		return sourceId;
	}
	
	/**
	 * Method used to send data of thread-type to a database.
	 * @param thread Object of class Thread which is to be send to a database.
	 * @return Id of a thread.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to a database.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html">java.sql.Statement documentation</a>
	 */
	public ThreadReturnCluster threadToDatabase(Thread thread) throws ClassNotFoundException, SQLException
	{
		int threadId; //Variable used for returning value of thread's id.
		boolean ifNew; //Variable used for returning information about novelty of a thread.
		
		try
		{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "postgres");
			statement = conn.createStatement();
			
			checkIfExists = "SELECT id_thr FROM threads WHERE link = '" + thread.link + "'";
			
			statement.execute(checkIfExists);
			resultSet = statement.getResultSet();
			
			if (resultSet.next())
			{
				threadId = resultSet.getInt(1);
				ifNew = false;
			}
			
			else
			{
				statement.execute("INSERT INTO threads (link, title, text, raw_text, update, id_src) VALUES ("+ thread.threadToSave() + ");");
				statement.execute(checkIfExists);
				resultSet = statement.getResultSet();
				resultSet.next();
				threadId = resultSet.getInt(1);
				ifNew = true;
			}
		}
		
		finally
		{
			if (conn != null) conn.close();
		}
		
		ThreadReturnCluster threadReturnCluster = new ThreadReturnCluster(threadId, ifNew); //Variable containing threadId and ifNew variables to return.
		
		return threadReturnCluster;
	}
	
	/**
	 * Method used to send data of comment-type to a database.
	 * @param comment Object of class Comment which is to be send to a database.
	 * @param ifNew Indicates if thread is  being downloaded for the first time or if it is updated.
	 * @param withReply Indicates if comment is an answer or no.
	 * @return Id of a comment.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to a database.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html">java.sql.Statement documentation</a>
	 */
	public int commentToDatabase(Comment comment, boolean ifNew, boolean withReply) throws ClassNotFoundException, SQLException
	{
		int commentId; //Variable used for returning value of comment's id.
		
		try
		{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "postgres");
			statement = conn.createStatement();
			
			checkIfExists = "SELECT id_cmn FROM comments WHERE text = '" + comment.text + "' AND date = '" + comment.date + "' AND usr_name = '" + comment.user + "'";
			
			if (ifNew) //If it is a new thread there is no need to check if comments exist.
			{	
				if (withReply)
					statement.execute("INSERT INTO comments (id_thr, text, raw_text, date, usr_name, id_reply, timestamp, usr_type) VALUES ("+ comment.commentToSaveWithReply() + ");");
				
				else
					statement.execute("INSERT INTO comments (id_thr, text, raw_text, date, usr_name, timestamp, usr_type) VALUES ("+ comment.commentToSave() + ");");
				
				statement.execute(checkIfExists);
				resultSet = statement.getResultSet();
				resultSet.next();
				commentId = resultSet.getInt(1);
			}
			
			else //If a thread has already been downloaded there is a need to check if comments exist.
			{
				statement.execute(checkIfExists);
				resultSet = statement.getResultSet();
				
				if (resultSet.next())
					commentId = resultSet.getInt(1);
				
				else
				{					
					if (withReply)
						statement.execute("INSERT INTO comments (id_thr, text, raw_text, date, usr_name, id_reply, timestamp, usr_type) VALUES ("+ comment.commentToSaveWithReply() + ");");
					
					else
						statement.execute("INSERT INTO comments (id_thr, text, raw_text, date, usr_name, timestamp, usr_type) VALUES ("+ comment.commentToSave() + ");");
					
					statement.execute(checkIfExists);
					resultSet = statement.getResultSet();
					resultSet.next();
					commentId = resultSet.getInt(1);
				}
			}	
		}
		
		finally
		{
			if (conn != null) conn.close();
		}
		
		return commentId;
	}
}