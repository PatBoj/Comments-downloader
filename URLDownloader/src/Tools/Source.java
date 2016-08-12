package Tools;

/**
 * Class used for storage and formatting data corresponding to source-type.
 * @author	Mikołaj Synowiec
 * @since	2016-07-05
 */
public class Source
{
	/**
	 * Either 0 - forum, 1 - page or 2 - both of them.
	 */
	public int type;
	
	/**
	 * Shortened description of a source.
	 */
	public String abstrct;
	
	/**
	 * Url address of a source.
	 */
	public String link;
	
	/**
	 * Constructor of Source class.
	 * @param type Corresponds to type field.
	 * @param abstrct Corresponds to abstrct field.
	 * @param link Corresponds to link field.
	 */
	public Source(int type, String abstrct, String link)
	{
		this.type = type;
		this.abstrct = abstrct;
		this.link = link;
	}
	
	/**
	 * Method used to format fields of a source-type object in order to send it to a database.
	 * @return Formatted string which is ready to be send to a database.
	 */
	public String sourceToSave()
	{
		String formatedSource = new String(Integer.toString(type) + ", '" + abstrct + "', '" + link + "'"); //"'" separator is necessary for creating SQL statement.
		return formatedSource;
	}
}