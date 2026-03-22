package edu.chobotar.sandbox.model;

/*
    @author  User
    @project  sandbox
    @class  AuditMetadata
    @version  1.0.0
    @since  22.03.2026 - 21.18
*/

import lombok.Data;
import org.springframework.data.annotation.*;

import java.time.LocalDateTime;

@Data
public class AuditMetadata {

    @CreatedDate
    private LocalDateTime createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @LastModifiedBy
    private String lastModifiedBy;

}
