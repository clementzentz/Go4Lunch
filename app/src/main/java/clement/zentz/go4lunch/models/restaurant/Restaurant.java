package clement.zentz.go4lunch.models.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import clement.zentz.go4lunch.models.rating.Rating;
import clement.zentz.go4lunch.models.workmate.Workmate;

public class Restaurant implements Parcelable {

    @SerializedName("address_components")
    @Expose
    private List<AddressComponent> addressComponents = null;
    @SerializedName("adr_address")
    @Expose
    private String adrAddress;
    @SerializedName("business_status")
    @Expose
    private String businessStatus;
    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("plus_code")
    @Expose
    private PlusCode plusCode;
    @SerializedName("price_level")
    @Expose
    private Integer priceLevel;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("reviews")
    @Expose
    private List<Review> reviews = null;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("user_ratings_total")
    @Expose
    private Integer userRatingsTotal;
    @SerializedName("utc_offset")
    @Expose
    private Integer utcOffset;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("website")
    @Expose
    private String website;

    private List<Workmate> workmatesJoining = new ArrayList<>();
    private double globalRating;

    public List<Workmate> getWorkmatesJoining(){
        return workmatesJoining;
    }

    public void setWorkmatesJoining(List<Workmate> workmates){
        workmatesJoining = workmates;
    }

    public double getGlobalRating(){
        return globalRating;
    }

    public void setGlobalRating(double globalRating){
        this.globalRating = globalRating;
    }

    public Restaurant(){

    }

    public Restaurant(Parcel in) {
        adrAddress = in.readString();
        businessStatus = in.readString();
        formattedAddress = in.readString();
        formattedPhoneNumber = in.readString();
        icon = in.readString();
        id = in.readString();
        internationalPhoneNumber = in.readString();
        name = in.readString();
        placeId = in.readString();
        if (in.readByte() == 0) {
            priceLevel = null;
        } else {
            priceLevel = in.readInt();
        }
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
        reference = in.readString();
        scope = in.readString();
        types = in.createStringArrayList();
        url = in.readString();
        if (in.readByte() == 0) {
            userRatingsTotal = null;
        } else {
            userRatingsTotal = in.readInt();
        }
        if (in.readByte() == 0) {
            utcOffset = null;
        } else {
            utcOffset = in.readInt();
        }
        vicinity = in.readString();
        website = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(adrAddress);
        dest.writeString(businessStatus);
        dest.writeString(formattedAddress);
        dest.writeString(formattedPhoneNumber);
        dest.writeString(icon);
        dest.writeString(id);
        dest.writeString(internationalPhoneNumber);
        dest.writeString(name);
        dest.writeString(placeId);
        if (priceLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(priceLevel);
        }
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rating);
        }
        dest.writeString(reference);
        dest.writeString(scope);
        dest.writeStringList(types);
        dest.writeString(url);
        if (userRatingsTotal == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userRatingsTotal);
        }
        if (utcOffset == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(utcOffset);
        }
        dest.writeString(vicinity);
        dest.writeString(website);
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getAdrAddress() {
        return adrAddress;
    }

    public void setAdrAddress(String adrAddress) {
        this.adrAddress = adrAddress;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public Integer getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(Integer priceLevel) {
        this.priceLevel = priceLevel;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(Integer userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public Integer getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(Integer utcOffset) {
        this.utcOffset = utcOffset;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}