package ru.skillbox.socialnet.data.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class CorrectResponse<T> extends ApiResponse {

    private Collection<T> data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private BigInteger total;


    public CorrectResponse() {
        super();
    }

}
