package com.exmertec.yaz.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Contact {
    @Column(name = "email")
    private String email;

    @Embedded
    private Address address;

    public Contact() {
    }

    public Contact(String email, Address address) {
        this.email = email;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
