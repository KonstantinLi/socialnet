package ru.skillbox.socialnet.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class CorrectResponse<T> extends ApiResponse {

    private Collection<T> data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;


    public CorrectResponse() {
        super();
    }

}
