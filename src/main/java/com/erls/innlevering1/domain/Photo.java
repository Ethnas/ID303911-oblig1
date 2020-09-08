package com.erls.innlevering1.domain;

import com.erls.innlevering1.auth.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a photo or an image of an item for sale
 * @author Erlend
 */

@Entity
@Table(name = "photos")
@Data @EqualsAndHashCode(callSuper = false)
public class Photo extends AbstractDomain{
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String name;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @PositiveOrZero
    private long fileSize;
    
    @JoinColumn(name = "owner", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Item owner;
    
    public Photo() {
    }
    
}
