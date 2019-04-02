public class Value {
	
	String latitude,longitude;
	
	public Value(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String toString() {
		return (latitude + " - " + longitude);
	}
}