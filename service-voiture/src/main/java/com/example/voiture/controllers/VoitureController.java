package com.example.voiture.controllers;

import com.example.voiture.clients.ClientService;
import com.example.voiture.entities.Voiture;
import com.example.voiture.model.Client;
import com.example.voiture.repositories.VoitureRepository;
import com.example.voiture.services.VoitureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VoitureController {

    @Autowired
    VoitureRepository voitureRepository;

    @Autowired
    VoitureService voitureService;

    @Autowired
    ClientService clientService;

    @GetMapping(value = "/voitures", produces = {"application/json"})
    public ResponseEntity<Object> findAll() {
        try {
            List<Voiture> voitures = voitureRepository.findAll();
            return ResponseEntity.ok(voitures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching voitures: " + e.getMessage());
        }
    }

    @GetMapping("/voitures/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        try {
            Voiture voiture = voitureRepository.findById(id)
                .orElseThrow(() -> new Exception("Voiture Introuvable"));
            voiture.setClient(clientService.clientById(voiture.getClientId()));
            return ResponseEntity.ok(voiture);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Voiture not found with ID: " + id);
        }
    }

    @GetMapping("/voitures/client/{id}")
    public ResponseEntity<List<Voiture>> findByClient(@PathVariable Long id) {
        try {
            Client client = clientService.clientById(id);
            if (client != null) {
                return ResponseEntity.ok(voitureRepository.findByClientId(id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/voitures/{clientId}")
    public ResponseEntity<Object> save(@PathVariable Long clientId, @RequestBody Voiture voiture) {
        try {
            Client client = clientService.clientById(clientId);
            if (client != null) {
                voiture.setClient(client);
                voiture.setClientId(clientId);
                return ResponseEntity.ok(voitureService.enregistrerVoiture(voiture));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Client not found with ID: " + clientId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error saving voiture: " + e.getMessage());
        }
    }

    @PutMapping("/voitures/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody Voiture updatedVoiture) {
        try {
            Voiture existing = voitureRepository.findById(id)
                .orElseThrow(() -> new Exception("Voiture not found with ID: " + id));
            if (updatedVoiture.getMatricule() != null && !updatedVoiture.getMatricule().isEmpty()) {
                existing.setMatricule(updatedVoiture.getMatricule());
            }
            if (updatedVoiture.getMarque() != null && !updatedVoiture.getMarque().isEmpty()) {
                existing.setMarque(updatedVoiture.getMarque());
            }
            if (updatedVoiture.getModel() != null && !updatedVoiture.getModel().isEmpty()) {
                existing.setModel(updatedVoiture.getModel());
            }
            return ResponseEntity.ok(voitureRepository.save(existing));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating voiture: " + e.getMessage());
        }
    }
}
