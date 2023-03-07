/**
 * User.java
 *
 * Abstract User model extended by Staff and Customer models.
 */

package models.user;

public abstract class User {
    protected String forename;
    protected String surname;

    public User() {}

    /**
     * User constructor with parameters.
     *
     * @param forename
     * @param surname
     */
    public User(String forename, String surname) {
        this.forename = forename;
        this.surname = surname;
    }

    /** Accessor for forename. */
    public String getForename() {
        return this.forename;
    }

    /** Mutator for forename. */
    public void setForename(String forename) {
        this.forename = forename;
    }

    /** Accessor for surname. */
    public String getSurname() {
        return this.surname;
    }

    /** Mutator for surname */
    public void setSurname(String surname) {
        this.surname = surname;
    }
}
