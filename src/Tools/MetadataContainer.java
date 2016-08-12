package Tools;
/**
 * Class used as a container for data read from text file in the main function of the project.
 * @author 	Mikołaj Synowiec
 * @since	2016-08-02
 */
public class MetadataContainer
{
	/**
	 * Fields containing url address of a website's archive.
	 */
	public String archive;
	
	/**
	 * Fields specifying "up" and "down" date of specified acquisition period of time.
	 */
	public String fromDate, toDate;
	
	/**
	 * Field specifying type of data to download (one of: all, section, topic, page, article).
	 */
	public String type;
	
	/**
	 * Simple constructor of a class MetadataContainer.
	 * @param archive Corresponds to archive field.
	 * @param fromDate Corresponds to fromDate field.
	 * @param toDate Corresponds to toDate field.
	 * @param type Corresponds to type field.
	 */
	public MetadataContainer(String archive, String fromDate, String toDate, String type)
	{
		this.archive = archive;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.type = type;
	}
	
	/**
	 * Simple constructor of a class MetadataContainer with no arguments
	 */
	public MetadataContainer() {}
}