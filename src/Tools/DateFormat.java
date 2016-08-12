package Tools;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * The class DateFormat converts string into
 * Calendar object.
 * <p>
 * Input string cannot contains any integer
 * before date.
 * <p>
 * <b>Note: </b> It contains only polish
 * names of the months.
 * 
 * @author Patryk Bojarski
 * @since 29.07.2016
 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html">Calendar</a>,
 * 		<a href="https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
 */

public class DateFormat 
{
	// Fields store date information from input string
	private String year;
	private String month;
	private String day;
	private String hour;
	private String minute;
	private String second;

	private Calendar date; // Main date storage
	// If months is not represent as number but as a name like "luty" or "listopad"
	private enum Months {sty, lut, mar, kwi, maj, cze, lip, sie, wrz, paź, lis, gru};
	private int i; // Iterator
	
	/**
	 * Constructor of DateFormat.
	 * <p>
	 * Divide input string into two pieces and reorders
	 * substrings. It can be useful when time is before the date.
	 * @param inputString string to convert.
	 * @param substring where to substring.
	 */
	public DateFormat(String inputString, int substring)
	{
		day =  "";
		month =  "";
		year =  "";
		hour = "";
		minute = "";
		second = "";
		date = new GregorianCalendar();
		i = 0;
		dateFormat(inputString.substring(substring) + " " + inputString.substring(0,substring));
	}
	
	/**
	 * Constructor of DateFormat.
	 * @param inputString string to convert.
	 */
	public DateFormat(String inputString)
	{
		this(inputString, inputString.length());
	}
	
	/**
	 * Default constructor with empty input string.
	 */
	DateFormat() {this("");}
	
	// It's method to search time (hours, minutes and seconds) in string
	private String time(char[] c)
	{
		while (i < c.length)
		{
			if (Character.isDigit(c[i]))
			{
				if(i+1 < c.length && Character.isDigit(c[i+1])) {i+=2; return (Character.toString(c[i-2]) + Character.toString(c[i-1]));}
				else {i+=2; return Character.toString(c[i-2]);}
			}
			i++;
		}
		return "";
	}
	
	/**
	 * Converts input string into Calendar object.
	 * <p>
	 * If input string does not contains date than method
	 * sets default values: "01.01.0001, 00:00:00"
	 * @param inputString string to convert.
	 */
	public void dateFormat(String inputString)
	{
		char[] c = inputString.toCharArray(); // Converting input string to table of chars
		
		// Searching for day or year
		while (i < c.length)
		{
			if (Character.isDigit(c[i])) // If finds number
			{
				if (Character.isDigit(c[i+1]) && i+1 < c.length) // If next char is also a number
				{
					// If next two chars are numbers (for example 2014) it must be a year
					if (i+3 < c.length && Character.isDigit(c[i+2]) && Character.isDigit(c[i+3])) 
					{
						for (int j=0; j<4; j++) year += Character.toString(c[i+j]);
						i += 4;
						break;
					}
					else // If there is only two chars next to each other (for example 03) it must be a day
					{
						day = Character.toString(c[i]) + Character.toString(c[i+1]); 
						i += 2; 
						break;
					}
				}
				else {day = Character.toString(c[i]); i++; break;} // If there is only one number (for example 2) it must be a day
			}
			i++;
		}
		
		// Searching for month by name
		int d = 1; // We start from first month January
		for (Months m : Months.values()) // Search every name of months
		{
			try
			{
				if (inputString.substring(i-1, i+4).toLowerCase().contains(m.toString())) // If input string contains name of the month
				{
					month = Integer.toString(d);
					break;
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				date.set(Calendar.YEAR, 1);
				return;
			}
			d++;
		}
		// Searching for month by number
		while (i < c.length && month.length() == 0) // If we cannot find month by name we search month by number
		{
			if (Character.isDigit(c[i])) // If finds number
			{
				if (Character.isDigit(c[i+1]) && i+1 < c.length) // If next char is also a number it must be a month
				{
					month = Character.toString(c[i]) + Character.toString(c[i+1]); 
					i += 2; 
					break;
				}
				else {month = Character.toString(c[i]); i++; break;} // If there is only one number it's month too
			}
			i++;
		}
		
		// Searching for year or day
		while (i < c.length)
		{
			if (Character.isDigit(c[i])) // If finds number
			{
				if (i+1 < c.length && Character.isDigit(c[i+1])) // If next char is also a number
				{
					if (day.length() == 0) // If we cannot find day before it must be a day
					{
						day = Character.toString(c[i]) + Character.toString(c[i+1]);
						i += 2;
						break;
					}
					else // If we have day of the month this must be year
					{
						year += Character.toString(c[i]) + Character.toString(c[i+1]); // If there is only two digits (for example 14, but it means 2014)
						// If there is 4 digits (for example 2014)
						if (i+3 < c.length && Character.isDigit(c[i+2]) && Character.isDigit(c[i+3])) {year += Character.toString(c[i+2]) + Character.toString(c[i+3]); i += 2;}
						i += 2;
						break;
					}
				}
			}
			i++;
		}
		
		hour = time(c); // Search for hours
		if (hour != "") minute = time(c); // If finds hours search for minutes
		if (minute != "") second = time(c); // If finds minutes search for seconds
		
		if (day.length() == 1) day = "0" + day; // For example if day is "2" converts to "02"
		if (month.length() == 1) month = "0" + month; // For example if month is "9" converts to "09"
		if (year.length() == 2) year = "20" + year; // For example if year is "14" converts to "2014"
		
		// If cannot find date then sets default values
		if (day.equals("")) day = "1";
		if (month.equals("")) month = "1";
		if (year.equals("")) year = "0";
		if (hour.equals("")) hour = "0";
		if (minute.equals("")) minute = "0";
		if (second.equals("")) second = "0";
		
		// Connects every date information in Calendar class format
		date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
		date.set(Calendar.MINUTE, Integer.parseInt(minute));
		date.set(Calendar.SECOND, Integer.parseInt(second));
		date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
		date.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		date.set(Calendar.YEAR, Integer.parseInt(year));
	}
	
	/**
	 * Display converted date by format.
	 * @param dateFormat date format from class SimpleDateFormat
	 * @return string to display
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
	 */
	public String displayDate(String dateFormat)
	{
		SimpleDateFormat dateStyle = new SimpleDateFormat(dateFormat);
		return dateStyle.format(date.getTime());
	}
	
	/**
	 * Display converted date using default format: "dd.mm.yyyy, hh:mm:ss"
	 * for example: "30.11.1994, 19:21:31".
	 * @return string to display
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
	 */
	public String displayDate()
	{
		SimpleDateFormat dateStyle = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
		return dateStyle.format(date.getTime());
	}
	
	/**
	 * Compares dates.
	 * <p>
	 * Compare storage date if it contains between
	 * input dates.
	 * @param fromDate the lower limit of date.
 	 * @param toDate the upper limit of date.
	 * @return true if date contains between input dates,
	 * 		   false if date do not contains between input dates
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html">Calendar</a>
	 */
	public boolean compareDate(Calendar fromDate, Calendar toDate)
	{
		if (date.after(fromDate) && toDate.after(date)) return true;
		else return false;
	}
	
	/**
	 * Compares dates.
	 * <p>
	 * Compare storage date if it is after input date.
	 * @param toDate the upper limit of date.
	 * @return true if date is after input date,
	 * 		   false if date is before input date.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html">Calendar</a>
	 */
	public boolean compareDateUpperBound(Calendar toDate)
	{
		if (date.after(toDate)) return true;
		else return false;
	}
	
	/**
	 * Compares dates.
	 * <p>
	 * Compare storage date if it is before input date.
	 * @param fromDate the lower limit of date.
	 * @return true if date is before input date,
	 * 		   false if date is after input date.
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html">Calendar</a>
	 */
	public boolean compareDateLowerBound(Calendar fromDate)
	{
		if (fromDate.after(date)) return true;
		else return false;
	}
	
	/**
	 * Returns date as Calendar object.
	 * @return date
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html">Calendar</a>
	 */
	public Calendar getDate() {return date;}
}