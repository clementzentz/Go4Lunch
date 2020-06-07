package clement.zentz.go4lunch.service;

interface GooglePlacesService {
    public static final String ENDPOINT_FIND_PLACES = "https://maps.googleapis.com/maps/api/place/findplacefromtext/output?parameters";
    public static final String ENDPOINT_NEARBY_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters";
    public static final String ENDPOINT_FIND_PLACES_EXAMPLE = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=YOUR_API_KEY";
}