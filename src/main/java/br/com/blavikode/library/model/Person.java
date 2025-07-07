package br.com.blavikode.library.model;

import br.com.blavikode.library.type.AbstractAtivoType;
import jakarta.persistence.*;

import static br.com.blavikode.library.ApplicationConstants.*;


@Entity
@Table(name = PERSON)
public class Person extends AbstractAtivoType {

    @Column(name = FIRST_NAME, nullable = false, length = 80)
    private String firstName;
    @Column(name = LAST_NAME, nullable = false, length = 80)
    private String lastName;
    @Column(nullable = false, length = 100)
    private String address;
    @Column(nullable = false, length = 100)
    private String gender;

    public Person() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
