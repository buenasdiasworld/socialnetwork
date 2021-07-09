package main.core;

import main.data.PersonPrincipal;
import main.model.Person;
import org.springframework.security.core.context.SecurityContextHolder;

public class ContextUtilities {
    private ContextUtilities() {
        throw new IllegalStateException("Utility class");
    }

    public static int getCurrentUserId() {
        return ((PersonPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getPerson().getId();
    }

    public static Person getCurrentPerson() {
        return ((PersonPrincipal) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal()).getPerson();
    }
}
