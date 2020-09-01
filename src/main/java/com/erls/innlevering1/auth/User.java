package com.erls.innlevering1.auth;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import static com.erls.innlevering1.auth.User.FIND_ALL_USERS;
import static com.erls.innlevering1.auth.User.FIND_USER_BY_IDS;
import javax.validation.constraints.Email;


/**
 * Represents an user in the Fant webstore
 *
 */
@Entity @Table(name = "AUSER")
@NamedQuery(name = FIND_ALL_USERS, query = "select u from User u order by u.firstName")
@NamedQuery(name = FIND_USER_BY_IDS, query = "select u from User u where u.userid in :ids")
public class User implements Serializable {
    public static final String FIND_USER_BY_IDS = "User.findUserByIds";
    public static final String FIND_ALL_USERS = "User.findAllUsers";
    
    @Id
    String userid;

    @JsonbTransient
    String password;
    
    @Email
    String email;

    @Version
    Timestamp version;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date created;

    @ManyToMany
    @JoinTable(name="AUSERGROUP",
            joinColumns = @JoinColumn(name="userid", referencedColumnName = "userid"),
            inverseJoinColumns = @JoinColumn(name="name",referencedColumnName = "name"))
            
    String firstName;
    String middleName;
    String lastName;
    String phoneNumber;
    String mail;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "auser_properties", joinColumns=@JoinColumn(name="userid"))
    @MapKeyColumn(name="key")
    @Column(name = "value")
    Map<String,String> properties = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }
    
}
