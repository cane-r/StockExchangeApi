package com.inghubs.stockexchangeapi.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@JsonIgnore
    private Long id;

    @Column(unique = true)
    private String name;
    private String description;

    @Column(name = "CREATED_DATE")
    private ZonedDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    private ZonedDateTime modifiedDate;

    @Version
    private long version;

    @PrePersist
    public void preCreated( ) {
        this.createdDate = ZonedDateTime.now();
    }

    @PreUpdate
    public void preModified( ) {
        this.modifiedDate = ZonedDateTime.now();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BaseEntity))
            return false;

        BaseEntity other = (BaseEntity) o;

        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
