/**
 * @author Hubert
 */
public final class SubwaySystem {

	public final String subwaySystemName;
	final java.util.HashSet<SubwayStation> subwayStationsSet = new java.util.HashSet<>();

	//Do not use default constructor
	private SubwaySystem() {
		subwaySystemName = null;
	}

	/**
	 * Creates a new subway line for a collection of subway stations
	 * @param systemName Name of the Line
	 * @param stations An array of stations to add.
	 */
	public SubwaySystem(String systemName, SubwayStation... stations) {
		subwaySystemName = systemName;
		subwayStationsSet.addAll(java.util.Arrays.asList(stations));
	}

	/**
	 * Gets the number of stations in the line
	 * @return size
	 */
	public int numberOfStations() {
		return subwayStationsSet.size();
	}

	/**
	 * Adds a new station to the line
	 * @param station station to be added
	 */
	public void addStation(SubwayStation station) {
		if (subwayStationsSet.contains(station)) {
			throw new IllegalArgumentException("Station \"" + station.stationName + "\" already exists in the system.");
		}
		subwayStationsSet.add(station);
	}

	/**
	 * Returns the station if it already exists within the line
	 * @param station station to search for
	 * @return 
	 */
	public SubwayStation containsStation(SubwayStation station) {
		for (SubwayStation s : subwayStationsSet) {
			if (s.equals(station)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Removes a station from the subway line, DOES NOT disconnect the adjacent stations.
	 * @param station 
	 */
	public void removeStation(SubwayStation station) {
		subwayStationsSet.remove(station);
	}

	/**
	 * Finds the shortest path between two stations given in the graph
	 *
	 * @param start The starting station
	 * @param end The end station
	 *
	 * @return Returns a linked list of occuring stations from start to end. If
	 * no connection exists, an empty LinkedList returned
	 */
	public static java.util.LinkedList<SubwayStation> findPath(SubwayStation start, SubwayStation end) {
		java.util.LinkedList<SubwayStation> returnable = start.findStation(end);
		if (returnable == null) {
			return new java.util.LinkedList<>();
		}
		return returnable;
	}

	/**
	 * Converts the object to a string by stating the line name and the stations in the line (not in order)
	 * @return 
	 */
	@java.lang.Override
	public String toString() {
		return subwaySystemName + ": " + subwayStationsSet.toString();
	}
}


