package com.practitionerservice.controller;

import com.practitionerservice.dto.PractitionerDTO;
import com.practitionerservice.dto.UserDTO;
import com.practitionerservice.model.Practitioner;
import com.practitionerservice.service.PractitionerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practitioners")
public class PractitionerController {

    @Autowired
    private PractitionerService practitionerService;

    //@PreAuthorize("hasAnyRole('PATIENT')")
    @GetMapping
    public ResponseEntity<List<PractitionerDTO>> getAllPractitioners() {
        if(practitionerService.getAllPractitioners().isEmpty()) {
            return (ResponseEntity<List<PractitionerDTO>>) ResponseEntity.EMPTY;
        }
        return ResponseEntity.ok(practitionerService.getAllPractitioners());
    }




    @PostMapping
    public ResponseEntity<PractitionerDTO> addPractitioner(@RequestBody PractitionerDTO practitionerDTO) {
        System.out.println("Received PractitionerDTO: " + practitionerDTO);
        PractitionerDTO savedPractitioner = practitionerService.addPractitioner(practitionerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPractitioner);
    }

    //TESTETSTESTSET

    @GetMapping("/{id}")
    public ResponseEntity<PractitionerDTO> getPractitionerById(@PathVariable Long id) {
        PractitionerDTO practitioner = practitionerService.getPractitionerById(id);
        if (practitioner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(practitioner);
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<PractitionerDTO> getPractitionerByUsername(@PathVariable String username) {
        PractitionerDTO practitioner = practitionerService.getPractitionerByUsername(username);
        if (practitioner == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(practitioner);
    }
}
