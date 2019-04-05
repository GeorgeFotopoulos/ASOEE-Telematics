public class Value {

    private String latitude, longitude;

    public Value(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }


    public String toString() {
        return (latitude + " - " + longitude);
    }

}