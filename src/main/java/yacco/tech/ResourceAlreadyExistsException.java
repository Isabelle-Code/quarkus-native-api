package yacco.tech;

import jakarta.ws.rs.BadRequestException;

public class ResourceAlreadyExistsException extends BadRequestException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
