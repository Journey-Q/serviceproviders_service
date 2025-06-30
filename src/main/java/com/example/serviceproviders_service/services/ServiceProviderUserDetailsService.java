package com.example.serviceproviders_service.services;


import com.example.serviceproviders_service.repository.ServiceProviderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceProviderUserDetailsService implements UserDetailsService {

    private final ServiceProviderRepo serviceProviderRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return serviceProviderRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Service provider not found: " + username));
    }
}