package com.nexusloan.service;

import com.nexusloan.dto.CreateUserRequest;
import com.nexusloan.dto.PageResponse;
import com.nexusloan.dto.UserResponse;
import com.nexusloan.entity.User;
import com.nexusloan.exception.ConflictException;
import com.nexusloan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        String email = EmailNormalizer.normalize(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Já existe um usuário com o e-mail '" + email + "' neste tenant");
        }
        User user = userRepository.save(new User(
                request.name(), email, passwordEncoder.encode(request.password()), request.role()));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> list(Pageable pageable) {
        return PageResponse.from(userRepository.findAll(pageable).map(UserResponse::from));
    }
}
