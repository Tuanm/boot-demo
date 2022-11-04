package dev.tuanm.demo.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @CreatedDate
    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    protected LocalDateTime modifiedAt;
}
