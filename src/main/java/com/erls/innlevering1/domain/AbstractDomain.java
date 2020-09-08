package com.erls.innlevering1.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * An abstract base class for domain objects with support for created and version fields
 * 
 * @author mikael
 */
@MappedSuperclass
public abstract class AbstractDomain implements Serializable {
    
    @Column(nullable = false)
    @Version
    Timestamp version;
    
    @Column(nullable = false, updatable = false)
    final private LocalDateTime created;
    
    public AbstractDomain() {
        this.created = LocalDateTime.now();
        this.version = new Timestamp(System.currentTimeMillis());
    }

    /**
     * @Version allows the JPA engine to use optimistic locking in the database.
     * JPA will update the timestamp on insert and update requests
     * @return 
     */
    public Timestamp getVersion() {
        return version;
    }

    public LocalDateTime getCreated() {
        return created;
    }
}
