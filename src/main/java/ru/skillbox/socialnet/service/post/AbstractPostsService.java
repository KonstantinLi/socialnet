package ru.skillbox.socialnet.service.post;

import org.springframework.security.core.AuthenticationException;

public abstract class AbstractPostsService {
    public static final String ERROR_NO_RECORD_FOUND = "No record found";

    protected long getMyId(String authorization) throws AuthenticationException {
        // TODO: AbstractPostsService getMyId
        return 123l;
    }
}
