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


    private PractitionerDTO toDTO(Practitioner practitioner) {
        UserDTO user = getUserById(practitioner.getUserId());
        return new PractitionerDTO(
                practitioner.getUserId(),
                user.getFullName(),
                practitioner.getSpecialty(),
                user.getRoles()
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
