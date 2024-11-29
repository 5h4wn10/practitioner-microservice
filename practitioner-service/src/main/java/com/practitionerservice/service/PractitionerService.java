package com.practitionerservice.service;

import com.practitionerservice.dto.PractitionerDTO;
import com.practitionerservice.dto.UserDTO;
import com.practitionerservice.model.Practitioner;
import com.practitionerservice.repository.PractitionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${user.service.url}")
    private String userServiceUrl;

    public List<PractitionerDTO> getAllPractitioners() {
        return practitionerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PractitionerDTO addPractitioner(PractitionerDTO practitionerDTO) {
        if(practitionerRepository.existsById(practitionerDTO.getUserId()))
            throw new IllegalArgumentException("Practitioner with this userId already exists.");

        Practitioner practitioner = new Practitioner();
        practitioner.setUserId(practitionerDTO.getUserId());
        practitioner.setName(practitionerDTO.getName());
        practitioner.setSpecialty(practitionerDTO.getSpecialty());
        practitioner.setRole(practitionerDTO.getRole());

        Practitioner savedPractitioner = practitionerRepository.save(practitioner);
        return toDTO(savedPractitioner);
    }

    public PractitionerDTO getPractitionerById(Long id) {
        return practitionerRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }


    public PractitionerDTO getPractitionerByUsername(String username) {
        // Steg 1: Hämta användare från User-tjänsten baserat på username
        UserDTO user = getUserByUsername(username);

        // Steg 2: Hämta practitioner baserat på användarens ID
        Practitioner practitioner = practitionerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Practitioner not found for userId " + user.getId()));

        // Steg 3: Konvertera Practitioner till PractitionerDTO och returnera
        return new PractitionerDTO(
                practitioner.getUserId(),
                practitioner.getName(),
                practitioner.getSpecialty(),
                practitioner.getRole()
        );
    }

    private UserDTO getUserByUsername(String username) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/by-username/" + username)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user details for username: " + username + ", error: " + e.getMessage());
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

    private UserDTO getUserById(Long userId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/" + userId)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user details: " + e.getMessage());
        }
    }
}
