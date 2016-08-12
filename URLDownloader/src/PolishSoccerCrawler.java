import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import Plugins.CracoviaForumPl;
import Plugins.FanatikOgiComPl;
import Plugins.KKSLech;
import Plugins.LechPoznanForumPl;
import Plugins.LechiaGdaForumPl;
import Plugins.LegiaNet;
import Plugins.LegionisciForumCom;
import Plugins.PilkarskieForumCom;
import Plugins.PogonForumPl;
import Plugins.SlaskNet;
import Plugins.TerazPasy;
import Plugins.WislaKrakowForumCom;
import Plugins.WislaKrakowForumPl;
import Tools.MetadataContainer;

/**
 * Class executing plugins LegiaNet and SlaskNet containing only main method of the project.
 * @author	Mikołaj Synowiec
 * @since	2016-08-08
 */
public class PolishSoccerCrawler
{
	/**
	 * Main function of the class which uses previously implemented methods (from URLDownloader project) to download comments from portals included in text file typed when starting program.
	 * @param args Standard main method parameters.
	 * @throws IOException Result of connecting to the portal.
	 * @throws ClassNotFoundException Result of getting driver from "org.postgresql.Driver".
	 * @throws SQLException Result of connecting to database.
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException
	{	
		String[] addresses = new String[13]; //Table of url inlcuded addressess.
		addresses[0] = "http://www.cracovia.krakow.pl/";
		addresses[1] = "http://lechia.gda.pl/forum/";
		addresses[2] = "http://forum.hejlech.pl/";
		addresses[3] = "http://forum.legionisci.com/";
		addresses[4] = "http://pilkarskieforum.com/";
		addresses[5] = "http://forum.pogononline.pl/";
		addresses[6] = "http://www.wislakrakow.com/forum/";
		addresses[7] = "http://skwk.pl/skwkforum";
		addresses[8] = "http://fanatik.ogicom.pl/";
		addresses[9] = "http://legia.net/index.php?page=1&typ=forum_list";
		addresses[10] = "http://SlaskNet.com/";
		addresses[11] = "http://kkslech.com/page/1/";
		addresses[12] = "http://terazpasy.pl/Pilka-nozna/Aktualnosci/(offset)/0";
		
		String line = null;
		String fileName = args[0]; //Path and name of the file containing meta-data.
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader); //These components are used while reading from file.
		
		while((line = bufferedReader.readLine()) != null) //Reading from file until its very end.
		{
			MetadataContainer metadataContainer = new MetadataContainer(); //Object containing metadata of a source.
			metadataContainer.archive = line;
			metadataContainer.fromDate = bufferedReader.readLine();
			metadataContainer.toDate = bufferedReader.readLine();
			metadataContainer.type = bufferedReader.readLine();
			
			if (metadataContainer.archive.contains(addresses[0]))
			{
				CracoviaForumPl a = new CracoviaForumPl(metadataContainer.archive, metadataContainer.fromDate, metadataContainer.toDate, metadataContainer.type);
			    a.runCracoviaForumPl();
			}
			else if (metadataContainer.archive.contains(addresses[1]))
			{
				LechiaGdaForumPl b = new LechiaGdaForumPl(metadataContainer.archive, metadataContainer.fromDate, metadataContainer.toDate, metadataContainer.type);
				b.runLechiaGdaForumPl();
			}
			else if (metadataContainer.archive.contains(addresses[2]))
			{
				LechPoznanForumPl c = new LechPoznanForumPl(metadataContainer.archive, metadataContainer.fromDate, metadataContainer.toDate, metadataContainer.type);
				c.runLechPoznanForumPl();
			}
			else if (metadataContainer.archive.contains(addresses[3]))
			{
				LegionisciForumCom d = new LegionisciForumCom(metadataContainer.archive, metadataContainer.fromDate, metadataContainer.toDate, metadataContainer.type);
				d.runLegionisciForumCom();
			}
			else if (metadataContainer.archive.contains(addresses[4]))
			{
				PilkarskieForumCom e = new PilkarskieForumCom(metadataContainer.archive, metadataContainer.fromDate, metadataContainer.toDate, metadataContainer.type);
				e.runPilkarskieForumCom();
			}
			else if (metadataContainer.archive.contains(addresses[5]))
			{
				PogonForumPl f = new PogonForumPl(metadataContainer.archive, metadataContainer.fromDate, metadataContainer.toDate, metadataContainer.type);
				f.runPogonForumPl();
			}
			else if (metadataContainer.archive.contains(addresses[6]))
			{
				WislaKrakowForumCom g = new WislaKrakowForumCom(metadataContainer.archive, metadataContainer.fromDate, metadataContainer.toDate, metadataContainer.type);
				g.runWislaKrakowForumCom();
			}
			else if (metadataContainer.archive.contains(addresses[7]))
			{
				WislaKrakowForumPl h = new WislaKrakowForumPl(metadataContainer.archive, metadataContainer.fromDate, metadataContainer.toDate, metadataContainer.type);
				h.runWislaKrakowForumPl();
			}
			else if (metadataContainer.archive.contains(addresses[8]))
			{
				FanatikOgiComPl i = new FanatikOgiComPl(metadataContainer.archive, metadataContainer.fromDate, metadataContainer.toDate, metadataContainer.type);
				i.runFanatikOgiComPl();
			}
			
			else if (metadataContainer.archive.contains(addresses[9])) //Running specified by url address plugins.
			{
				LegiaNet legiaNet = new LegiaNet(metadataContainer.archive);
				legiaNet.dataDownloading(metadataContainer.fromDate, metadataContainer.toDate);
			}
			
			else if (metadataContainer.archive.contains(addresses[10]))
			{
				SlaskNet slaskNet = new SlaskNet(metadataContainer.archive);
				slaskNet.dataDownloading(metadataContainer.fromDate, metadataContainer.toDate);
			}
			
			else if (metadataContainer.archive.contains(addresses[11]))
			{
				KKSLech kksLech = new KKSLech(metadataContainer.archive);
				kksLech.dataDownloading(metadataContainer.fromDate, metadataContainer.toDate);
			}
			
			else if (metadataContainer.archive.contains(addresses[12]))
			{
				TerazPasy terazPasy = new TerazPasy(metadataContainer.archive);
				terazPasy.dataDownloading(metadataContainer.fromDate, metadataContainer.toDate);
			}
			
			if (bufferedReader.readLine() == null) break;
		}
		
		bufferedReader.close();
	}
}
