package com.erls.innlevering1.domain;

import com.erls.innlevering1.auth.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An item to be sold in the Fant webstore
 * @author Erlend
 */
@Entity
@Data @EqualsAndHashCode(callSuper = false)
@Table(name = "items")
@NamedQuery(name = Item.FIND_BY_SELLER, query = "SELECT i FROM Item i WHERE i.seller = :seller")
@NamedQuery(name = Item.DELETE_BY_ID, query = "DELETE FROM Item i WHERE i.id = :id AND i.seller = :seller")
@NamedQuery(name = Item.GET_ALL_DESC, query = "SELECT i FROM Item i ORDER BY i.id DESC")
@NamedQuery(name = Item.COUNT_TOTAL_ITEMS, query = "SELECT count(i.id) from Item i")

public class Item extends AbstractDomain{
    
    public static final String FIND_BY_SELLER = "Item.FindBySeller";
    public static final String DELETE_BY_ID = "Item.DeleteById";
    public static final String GET_ALL_DESC = "Item.GetPaginatedItems";
    public static final String COUNT_TOTAL_ITEMS = "Item.CountTotalItems";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @PositiveOrZero
    private float price;
    
    @NotNull
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters.")
    private String name;
    
    private String description;
    
    @JsonbTypeAdapter(MediaObjectAdapter.class)
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Photo> photo;
    
    private boolean sold;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private LocalDateTime created;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private LocalDateTime updated;
    
    @OneToOne
    @JoinColumn(name = "seller", referencedColumnName = "id", nullable = true)
    private User seller;
    
    @OneToOne
    @JoinColumn(name = "buyer", referencedColumnName = "id", nullable = true)
    private User buyer;
    
    protected Item() {
    }
    
    public Item(String name, String description, float price, User seller) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.seller = seller;
    }
    
    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
    
}
