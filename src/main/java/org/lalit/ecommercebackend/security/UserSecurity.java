package org.lalit.ecommercebackend.security;



import org.lalit.ecommercebackend.model.User;
import org.lalit.ecommercebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity") // 👈 This name must match what's in @PreAuthorize
public class UserSecurity {

    private final UserRepository userRepository;

    @Autowired
    public UserSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isCurrentUser(Long id, Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findById(id)
                .map(user -> user.getUsername().equals(username))
                .orElse(false);
    }
}

