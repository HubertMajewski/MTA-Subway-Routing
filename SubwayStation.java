/**
 * @author Hubert
 */
public final class SubwayStation implements Comparable<SubwayStation> {

	public final String stationName;

	// The given SubwayStation and the time associated from this station to the
	// given SubwayStation
	private final java.util.HashMap<SubwayStation, Double> connectedStations = new java.util.HashMap<>();

	private SubwayStation() {
		stationName = null;
	}

	/**
	 * Create a SubwayStation 
	 * @param stationName
	 */
	public SubwayStation(String stationName) {
		this(stationName, null);
	}

	/**
	 * Create a new subwayStation and add it apart of a subway system.
	 *
	 * @param stationName The name of the station
	 * @param system The Subway System that the new station is apart of
	 */
	public SubwayStation(String stationName, SubwaySystem system) {
		this.stationName = stationName;
		if (system != null) {
			system.addStation(this);
		}
	}

	/**
	 * Connects a given station to the current station.
	 *
	 * @param station the station the object is connected to
	 * @param time The time it takes for the train to travel to the given
	 * station in minutes
	 *
	 * @return Returns weather the station has been added to the connected
	 * stations list
	 */
	public boolean connectToStation(SubwayStation station, Double time) {
		if (time == 0) {
			throw new IllegalArgumentException("Travel time cannot be 0 over a distance.");
		}
		if (!connectedStations.containsKey(station)) {
			connectedStations.put(station, time);
			station.connectToStation(this, time); // ASSUMING SAME TIME BOTH WAYS
			return true;
		}
		return false;
	}

	/**
	 * Update the time between this station and the given station
	 *
	 * @param station The station that this object is connected to. If given
	 * station is not connected, do nothing
	 * @param time The new time. If new time = old time, do nothing
	 */
	public void updateTrainStation(SubwayStation station, Double time) {
		if (time == 0) {
			throw new IllegalArgumentException("Travel time cannot be 0 over a distance.");
		}
		if (connectedStations.containsKey(station)) {
			if (!java.util.Objects.equals(connectedStations.get(station), time)) {
				connectedStations.put(station, time); // Update time
				station.updateTrainStation(this, time); // Update foreign station to given time ASSUMING BIDIRECTIONAL
			} // If the time is already set ignore
		} // Dont set if given station is not connected
	}

	/**
	 * Searches a list from the current object (SubwayStation) to the desired
	 * station in occurring order
	 *
	 * @param desiredStation The station to find the shortest path to
	 *
	 * @return A list of stations from the current to the destination. Returns
	 * null if doesn't exist
	 */
	public java.util.LinkedList<SubwayStation> findStation(SubwayStation desiredStation) {
		java.util.HashMap<SubwayStation, LengthLinkedList<SubwayStation>> starting = new java.util.HashMap<>();
		LengthLinkedList<SubwayStation> startinStation = new SubwayStation.LengthLinkedList<>();
		starting.put(this, startinStation);

		java.util.HashMap<SubwayStation, LengthLinkedList<SubwayStation>> map = DijkstraRecursion(starting, new java.util.HashSet<>());
		java.util.LinkedList<SubwayStation> returnable = map.get(desiredStation);

		if (returnable == null) {
			return new java.util.LinkedList<>();
		}
		
		returnable.add(desiredStation);
		return returnable;
	}
	
	// Dijstra's Alg for shortest path
	private java.util.HashMap<SubwayStation, LengthLinkedList<SubwayStation>> DijkstraRecursion(java.util.HashMap<SubwayStation, LengthLinkedList<SubwayStation>> mapShortestStations, java.util.HashSet<SubwayStation> visited) {

		//Set as visiting
		visited.add(this);

		//For every non-visited adjacent
		for (SubwayStation s : connectedStations.keySet()) {
			if (visited.contains(s)) {
				continue;
			}

			LengthLinkedList<SubwayStation> shortestStation = mapShortestStations.get(this).clone();
			shortestStation.add(this);
			shortestStation.length += connectedStations.get(s);

			if (mapShortestStations.get(s) == null || mapShortestStations.get(s).length > shortestStation.length) {
				mapShortestStations.put(s, shortestStation);
			}
		}

		//Pick station with smallest length
		SubwayStation shortestUnvisitedStation = null;
		for (SubwayStation s : mapShortestStations.keySet()) {
			if (visited.contains(s)) {
				continue;
			}
			if (shortestUnvisitedStation == null) {
				shortestUnvisitedStation = s;
				continue;
			}

			if (mapShortestStations.get(s).length < mapShortestStations.get(shortestUnvisitedStation).length) {
				shortestUnvisitedStation = s;
			}
		}

		if (shortestUnvisitedStation == null) {
			return mapShortestStations;
		}

		//Iterate for all unvisited
		return shortestUnvisitedStation.DijkstraRecursion(mapShortestStations, visited);
	}

	/**
	 * If a station is adjacent to another, return 1, -1 otherwise and 0 iff station is itself.
	 * 
	 * @param o A station to compare object to
	 *
	 * @return 0 If parameter is the same as the object being referenced, 1 if
	 * it is an adjacent station, -1 otherwise.
	 */
	@Override
	public int compareTo(SubwayStation o) {
		if (o.equals(this)) {
			return 0;
		}
		if (connectedStations.containsKey(o)) {
			return 1;
		}
		return -1;
	}

	/**
	 * Check if two stations are equal if they have the same station name
	 * @param obj
	 * @return
	 */
	@java.lang.Override
	public boolean equals(Object obj) {
		if (obj instanceof SubwayStation) {
			return (((SubwayStation) obj).stationName).equalsIgnoreCase(this.stationName);
		}
		return false;
	}

	@java.lang.Override
	public String toString() {
		return this.stationName;
	}

	@java.lang.Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + java.util.Objects.hashCode(stationName.toLowerCase());
		return hash;
	}

	// A linkedList class with a length described
	private final class LengthLinkedList<E> extends java.util.LinkedList<E> {

		Double length = 0.0;

		/**
		 * Copy all elements into a new LengthLinkedList
		 * @return Copied list
		 */
		@Override
		public LengthLinkedList<E> clone() {
			LengthLinkedList<E> temp = new LengthLinkedList<>();
			temp.length = this.length;

			this.forEach((e) -> {
				temp.add(e);
			});

			return temp;
		}
	}
}




