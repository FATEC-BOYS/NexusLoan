package com.nexusloan.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, UUID id) {
        super(resource + " não encontrado: " + id);
    }
}
