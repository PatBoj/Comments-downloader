package Tools;

/**
 * Class used as a container for data returning via threadToDatabase method (class Connector).
 * @author	Mikołaj Synowiec
 * @since	2016-08-08
 */
public class ThreadReturnCluster
{
	/**
	 * Field used to store id of a thread.
	 */
	public int threadId;
	
	/**
	 * Field used to store information about novelty of a thread.
	 */
	public boolean ifNew;
	
	/**
	 * Constructor of ThreadReturnCluster class with two arguments.
	 * @param threadId Corresponds to threadId field.
	 * @param ifNew Correspond to ifNew field.
	 */
	public ThreadReturnCluster(int threadId, boolean ifNew)
	{
		this.threadId = threadId;
		this.ifNew = ifNew;
	}
	
	/**
	 * Simple constructor of ThreadReturnCluster class with no arguments.
	 */
	public ThreadReturnCluster() {}
}