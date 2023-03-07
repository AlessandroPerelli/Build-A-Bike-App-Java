/**
 * Address.java
 *
 * Address model class.
 */

package models.user.customer;

public class Address {
    private int houseNumber;
    private String road;
    private String postcode;
    private String city;

    /**
     * Constructor with parameters.
     *
     * @param houseNumber
     * @param road
     * @param postcode
     * @param city
     */
    public Address(int houseNumber, String road, String postcode, String city) {
        this.houseNumber = houseNumber;
        this.road = road;
        this.postcode = postcode;
        this.city = city;
    }

    /** House number accessor. */
    public int getHouseNumber() {
        return this.houseNumber;
    }

    /** House number mutator. */
    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    /** Road name accessor. */
    public String getRoad() {
        return this.road;
    }

    /** Road name mutator. */
    public void setRoad(String road) {
        this.road = road;
    }

    /** Postcode accessor. */
    public String getPostcode() {
        return this.postcode;
    }

    /** Postcode mutator. */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /** City name accessor. */
    public String getCity() {
        return this.city;
    }

    /** City name mutator. */
    public void setCity(String city) {
        this.city = city;
    }
}
