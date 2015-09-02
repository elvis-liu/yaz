package com.exmertec.yaz.model;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

@Entity
@Table(name = "USER")
@SecondaryTable(name = "[ORDER]", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id"),
    foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
public class UserOrder {
    @Id
    @Column(name = "id")
    private Long userId;

    @Column(name = "name", length = 64)
    private String userName;

    @Column(name = "points")
    private Integer userPoints;

    @Column(name = "type")
    private UserType userType;

    @Column(table = "[ORDER]", name = "id")
    private Long orderId;

    @Column(table = "[ORDER]", name = "amount")
    private Double orderAmount;

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getUserPoints() {
        return userPoints;
    }

    public UserType getUserType() {
        return userType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }
}
