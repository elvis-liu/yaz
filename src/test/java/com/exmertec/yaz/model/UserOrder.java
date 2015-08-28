package com.exmertec.yaz.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "USER")
public class UserOrder {
    @Id
    private Long id;

    @Column(name = "name", length = 64)
    private String userName;

    @Column(name = "type")
    private UserType type;

    @OneToMany
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private Set<Order> orders;

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public UserType getType() {
        return type;
    }

    public Set<Order> getOrders() {
        return orders;
    }
}
