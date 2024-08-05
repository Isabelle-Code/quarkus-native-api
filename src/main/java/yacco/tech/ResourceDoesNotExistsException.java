package yacco.tech;

import jakarta.ws.rs.NotFoundException;

public class ResourceDoesNotExistsException extends NotFoundException {
    public ResourceDoesNotExistsException(String message) {
        super(message);
    }
}
