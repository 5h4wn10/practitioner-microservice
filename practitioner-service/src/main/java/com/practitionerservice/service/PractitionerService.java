package com.practitionerservice.service;

import com.practitionerservice.config.JwtTokenUtil;
import com.practitionerservice.dto.PractitionerDTO;
import com.practitionerservice.dto.UserDTO;
import com.practitionerservice.model.Practitioner;
import com.practitionerservice.model.Role;
import com.practitionerservice.repository.PractitionerRepository;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PractitionerService {

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private HttpServletRequest httpServletRequest; // För att hämta det inkommande tokenet

    @Value("${user.service.url}")
    private String userServiceUrl;

    private String getBearerToken() {
        String authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Missing Bearer Token in Header");
    }


    public List<PractitionerDTO> getAllPractitioners() {
        return practitionerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PractitionerDTO addPractitioner(PractitionerDTO practitionerDTO) {
        System.out.println("Received PractitionerDTO: " + practitionerDTO);
        if (practitionerRepository.existsById(practitionerDTO.getUserId())) {
            System.out.println("Practitioner already exists with userId: " + practitionerDTO.getUserId());
            throw new IllegalArgumentException("Practitioner with this userId already exists.");
        }

        Practitioner practitioner = new Practitioner();
        practitioner.setUserId(practitionerDTO.getUserId());
        practitioner.setName(practitionerDTO.getName());
        practitioner.setSpecialty(practitionerDTO.getSpecialty());
        practitioner.setRole(practitionerDTO.getRole());

        Practitioner savedPractitioner = practitionerRepository.save(practitioner);
        System.out.println("Saved Practitioner: " + savedPractitioner);
        return toDTO(savedPractitioner);
    }


    public PractitionerDTO getPractitionerById(Long id) {
        return practitionerRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public PractitionerDTO getPractitionerByUsername(String username) {
        // Steg 1: Hämta användare från User-tjänsten med inkommande token
        UserDTO user = getUserByUsername(username);

        // Steg 2: Hämta practitioner baserat på userId
        Practitioner practitioner = practitionerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Practitioner not found for userId " + user.getId()));

        return new PractitionerDTO(
                practitioner.getUserId(),
                practitioner.getName(),
                practitioner.getSpecialty(),
                practitioner.getRole()
        );
    }

    private UserDTO getUserByUsername(String username) {
        try {
            String token = getBearerToken();
            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/by-username/" + username)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // Skicka vidare token
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user details: " + e.getMessage());
        }
    }

    private UserDTO getUserById(Long userId) {
        try {
            String token = getBearerToken();
            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/" + userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // Skicka vidare token
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user details: " + e.getMessage());
        }
    }

    private PractitionerDTO toDTO(Practitioner practitioner) {
        UserDTO user = getUserById(practitioner.getUserId());
        return new PractitionerDTO(
                practitioner.getUserId(),
                user.getFullName(),
                practitioner.getSpecialty(),
                practitioner.getRole()
        );
    }
}