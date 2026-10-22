package com.nexusloan.dto;

import com.nexusloan.enums.Role;
import com.nexusloan.dto.validation.MaxUtf8Bytes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 8) @MaxUtf8Bytes(72) String password,
        @NotNull Role role) {
}
