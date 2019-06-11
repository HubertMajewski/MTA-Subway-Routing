/**
 * @author Hubert
 */
public class Main {

	static FileReader fileOfLines = null;
	static FileLogger linesLogger = null;

	//Create a null SubwaySystem for evey subway line 
	static int numberOfStations = 0;
	static final java.util.ArrayList<SubwaySystem> lines = new java.util.ArrayList<>();

	public static void main(String[] args) {

		boolean exceptionThrown = false;
		//Setup readerfile
		try {
			fileOfLines = new FileReader("lines.data");
		} catch (Exception e) {
			//File does not exist, setup a reader
			linesLogger = new FileLogger("lines.data");
			fileOfLines = null;
		}

		//If there not a file use the web to make one. If file exists, use that file
		if (fileOfLines == null) {
			downloadMTAInfo();
		} else {

			//Read lines from file if the file exists
			try {
				String lineString = fileOfLines.readNextLine();

				//For every line in file....
				for (int i = 0; lineString != null && !lineString.trim().isEmpty() && i < fileOfLines.length - 1; i++) {
					//Skip if no station names provided
					if (!lineString.contains(":")) {
						continue;
					}

					//Create a new SubwaySystem for every line in the file
					SubwaySystem line = new SubwaySystem(lineString.substring(0, lineString.indexOf(':')));

					//Special case, skip shuttle line
					if (line.subwaySystemName.equalsIgnoreCase("sline")) {
						lineString = fileOfLines.readNextLine();
						continue;
					}

					lineString = lineString.substring(lineString.indexOf(":") + 1); // Should not throw an exception assuming every line has atleast one station
					String subwayStations[] = lineString.split(",");

					//Iterate for every station name in a given line from a file
					SubwayStation prev = null;
					for (String s : subwayStations) {
						SubwayStation S = new SubwayStation(s);

						//Check if the subwaystation exists already for any other line
						for (SubwaySystem z : lines) {
							if (z == line) {
								continue; // Skip current working line
							}
							// If the station already exists in another station line, add the station
							if (z.containsStation(new SubwayStation(s)) != null) {
								// System.out.println("\t" + stationName + "\t" + line.subwaySystemName);
								S = z.containsStation(new SubwayStation(s));
							}
						}

						//Set a previous station
						if (prev == null) {
							line.addStation(S);
							prev = S;
							continue;
						}

						//Connect to previous station and add to subway system
						prev.connectToStation(S, 1.0);
						line.addStation(S);
						prev = S;
					}
					//Add the line to MTA map and read next line form file
					lines.add(line);
					lineString = fileOfLines.readNextLine();
				}

				//If any exception occures, reset the file and read from the web
			} catch (Exception ex) {
				//Print error using logger
				java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

				//Check to see if an exception has been thrown already before, if it has, then exit the program.
				if (exceptionThrown) {
					System.exit(1);
				}
				exceptionThrown = true;
				lines.clear();
				downloadMTAInfo(); //Write to file from web
			}
		}

		//Setup GUI using Swing
		java.awt.EventQueue.invokeLater(() -> {
			GUI_MTA gui = new GUI_MTA();
			gui.setVisible(true);
		});
	}

	/**
	 * This function sets up the html read to parse MTA's website and store it
	 * to a file. While reading from the website, also add it into the data structure.
	 */
	private static void downloadMTAInfo() {

		//The service mainpage
		org.jsoup.nodes.Document servicePage = null;
		try {
			servicePage = org.jsoup.Jsoup.connect("http://web.mta.info/nyct/service/").get();
		} catch (java.io.IOException ex) {
			java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
			System.exit(1); //Exit due to a malform connection exception
		}

		//Get elements list of lines available from the services page
		org.jsoup.select.Elements linesElements = servicePage.select("p[align*=center]");
		linesElements = linesElements.select("a[href]");

		//For every line element
		int i = 0;
		for (org.jsoup.nodes.Element x : linesElements) {
			//Write station into file separated by comma
			String lineStations = "";

			// Get weblink to line's page
			org.jsoup.nodes.Document linePage = null;
			try {
				linePage = org.jsoup.Jsoup.connect("http://web.mta.info/nyct/service/" + x.attributes().get("href")).get();
			} catch (java.io.IOException ex) {
				java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

				//Remove corrupt lines.data
				java.io.File file = new java.io.File("lines.data");
				file.delete();

				System.exit(1); //Exit due to a malform connection exception
			}

			//Create the line 
			String systemName = x.attributes().get("href").replaceFirst(".htm", "");

			//Ignore if shuttle
			if (systemName.equalsIgnoreCase("sline")) // Layout inconsistant with rest of the lines
			{
				i++;
				lines.add(new SubwaySystem("sline"));
				linesLogger.writeLine("sline: //TEMP: Skip shuttle for now");
				continue; //skip the sline for now
			}
			lines.add(new SubwaySystem(systemName));
			lineStations += systemName + ":";

			//Gets stations from rows
			org.jsoup.select.Elements stations = linePage.select("*[height=25]"); //Select all rows with 25 height (stations)

			//Set elements to be the collection of station names
			org.jsoup.select.Elements temp = new org.jsoup.select.Elements();
			for (org.jsoup.nodes.Element e : stations) {
				try {
					temp.add(e.select("span.emphasized").first());
				} catch (Exception ex) {
					//Skip elements which cannot be read because they are not stations (i.e null)
				}
			}
			stations = temp;

			//Previous station to connect to within the line
			SubwayStation prevStation = null;

			//For every station element create station in system
			for (org.jsoup.nodes.Element station : stations) {
				if (station == null) //Skip stations that have not been defined in the array.
				{
					continue;
				}

				// Get station name from station element (also remove '/' and trim it)
				String stationName = station.text().replace("/", "").replace("-", " ").replace("  ", " ").trim();

				// If the station is a valid station with a name
				if (!stationName.isEmpty()) {
					SubwayStation newStation = new SubwayStation(stationName);

					if (prevStation != null && prevStation.equals(newStation) || lines.get(i).containsStation(newStation) != null) {
						continue;
					}

					// Check if given station exists in another station (transfer lines)
					for (SubwaySystem line : lines) {
						if (line == lines.get(i)) {
							continue; //Skip current working line
						}
						// If the station already exists in another station line, add that station
						if (line.containsStation(new SubwayStation(stationName)) != null) {
							newStation = line.containsStation(new SubwayStation(stationName));
						}
					}

					//Add station name to datafile for current line
					lineStations += stationName + ",";

					// Link station to prev station
					if (prevStation == null) {
						prevStation = newStation;
					} else {
						newStation.connectToStation(prevStation, 1.0);
						prevStation = newStation;
					}

					if (lines.get(i).containsStation(newStation) == null) {
						lines.get(i).addStation(newStation);
					}
				}
			} // For Every station

			lineStations = lineStations.substring(0, lineStations.length() - 1); //Remove trailing comma
			linesLogger.writeLine(lineStations);

			i++;

		} // For Every Line
	}

}

