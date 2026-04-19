package edu.chobotar.sandbox.response;

/*
    @author  User
    @project  sandbox
    @class  PaginationMetaData
    @version  1.0.0
    @since  19.04.2026 - 16.12
*/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PaginationMetaData extends BaseMetaData {
    private int number;
    private int size;
    private int totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
}
