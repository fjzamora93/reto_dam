package com.unir.roleapp.service;

import com.unir.roleapp.dto.*;
import com.unir.roleapp.model.*;
import com.unir.roleapp.repository.*;
import com.unir.roleapp.mapper.EntityToDtoMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CharacterService {
    /** ModelMapper toma las mismos campos comunes entre CharacterRequestDTO y CharacterENtity y los mapea.
     * El resto de relaciones deben ser manejadas manualmente, ya que son relaciones complejas.
     * */
    @Autowired private ModelMapper modelMapper;

    @Autowired private CharacterRepository characterRepository;
    @Autowired private EntityToDtoMapper entityToDtoMapper;
    @Autowired private RoleClassRepository roleClassRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private GameSessionRepository gameSessionRepository;


    /** LISTA COMPLETA DE PERSONAJES
     * - Stream: convierte una colección a un flujo de datos stream (para procesarlos de forma más eficiente).
     * - Map: transforma cada objeto del flujo... es como una arrow function pero de Java.
     * - Collect: después d ehacer el map, coge los elementos procesados y los devuelve como una lsita.
     * */
    public List<CharacterResponseDTO> getAllCharacters() {
        List<CharacterEntity> characters = characterRepository.findAll();
        return characters.stream()
                .map(characterEntity -> entityToDtoMapper.mapToCharacterResponseDTO(characterEntity))
                .collect(Collectors.toList());
    }


    /** LISTA DE PERSONAJES POR USUARIO */
    public List<CharacterResponseDTO> getCharactersByUser(Long userId) {
        List<CharacterEntity> characters = characterRepository.findByUser_Id(userId);
        return characters.stream()
                .map(entityToDtoMapper::mapToCharacterResponseDTO)
                .collect(Collectors.toList());
    }


    /** BUSCAR POR ID */
    public CharacterResponseDTO getCharacterById(Long id) {
        return characterRepository.findById(id)
                .map(entityToDtoMapper::mapToCharacterResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHARACTER NOT FOUND"));

    }

    /** DELETE */
    public void deleteCharacter(Long id) {
        if (!characterRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Personaje no encontrado");
        }
        characterRepository.deleteById(id);
    }

    /** CREA UN NUEVO PERSONAJE O SI EN EL REQUEST SE INCLUYE EL ID ACTUALIZA UNO YA EXISTENTE */
    public CharacterResponseDTO saveOrUpdateCharacter(CharacterRequestDTO characterDto) {
        System.out.println("Guardando/actualizando personaje...");

        // Mapear campos básicos del DTO a la entidad (ignorando gameSession, roleClass y user)
        CharacterEntity characterEntity = modelMapper.map(characterDto, CharacterEntity.class);

        System.out.println("characterEntity ___________________= " + characterEntity);

        // Asignar campos complejos MANUALMENTE
        // 1. RoleClass
        RoleClass roleClass = roleClassRepository.findByName(characterDto.getRoleClass())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ROLCLASS NOT FOUND"));
        characterEntity.setRoleClass(roleClass);

        // 2. GameSession
        GameSession gameSession = gameSessionRepository.findById(characterDto.getGameSessionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GAMESESSION NOT FOUND"));
        characterEntity.setGameSession(gameSession);

        // 3. User
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND"));
        characterEntity.setUser(user);

        // Guardar la entidad
        CharacterEntity savedCharacter = characterRepository.save(characterEntity);

        System.out.println("GUARDAAAAAAAAAAAAAADOOOOO ___________________= " + savedCharacter);

        return entityToDtoMapper.mapToCharacterResponseDTO(savedCharacter);
    }




}
