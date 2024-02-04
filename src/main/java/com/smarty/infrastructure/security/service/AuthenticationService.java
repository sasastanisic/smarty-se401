package com.smarty.infrastructure.security.service;

import com.smarty.domain.account.service.AccountService;
import com.smarty.infrastructure.exception.exceptions.ForbiddenException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.security.model.AuthenticatedUser;
import com.smarty.infrastructure.security.model.LoginRequestDTO;
import com.smarty.infrastructure.security.model.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    public AuthenticationService(AccountService accountService, JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var optionalAccount = accountService.getAccountByEmail(username);

        if (optionalAccount.isEmpty()) {
            throw new NotFoundException("Email doesn't exist");
        }

        return new AuthenticatedUser(optionalAccount.get());
    }

    public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
        var authenticatedUser = (AuthenticatedUser) loadUserByUsername(loginRequestDTO.email());

        if (!passwordEncoder.matches(loginRequestDTO.password(), authenticatedUser.getPassword())) {
            throw new NotFoundException("Password isn't valid");
        }

        return new LoginResponseDTO(jwtUtil.createToken(authenticatedUser));
    }

    public void canUpdatePassword(String accountEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String loggedInAccount = authentication.getName();
            if (!loggedInAccount.equals(accountEmail)) {
                throw new ForbiddenException("You don't have permission to update password");
            }
        }
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

}
