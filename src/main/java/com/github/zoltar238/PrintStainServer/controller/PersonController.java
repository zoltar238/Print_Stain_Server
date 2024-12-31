package com.github.zoltar238.PrintStainServer.controller;

import com.github.zoltar238.PrintStainServer.dto.RegisterDto;
import com.github.zoltar238.PrintStainServer.dto.ResponsesEnum;
import com.github.zoltar238.PrintStainServer.persistence.entity.PersonEntity;
import com.github.zoltar238.PrintStainServer.persistence.entity.RoleEntity;
import com.github.zoltar238.PrintStainServer.persistence.entity.RoleEnum;
import com.github.zoltar238.PrintStainServer.persistence.repository.RoleRepository;
import com.github.zoltar238.PrintStainServer.persistence.repository.PersonRepository;
import com.github.zoltar238.PrintStainServer.utils.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/person")
@Slf4j
public class PersonController {

    private final PersonRepository personRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public PersonController(PersonRepository personRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPerson(@Valid @RequestBody RegisterDto registerDTO) {
        try {
            Set<RoleEntity> roles = new HashSet<>();

            // look for role
            for (String roleName : registerDTO.getRoles()) {
                Optional<RoleEntity> roleOpt = roleRepository.findByName(RoleEnum.valueOf(roleName));
                RoleEntity role;

                if (roleOpt.isPresent()) {
                    // if the role exist, use it
                    role = roleOpt.get();
                } else {
                    // else, create it
                    role = RoleEntity.builder()
                            .name(RoleEnum.valueOf(roleName))
                            .build();
                    // save the new role
                    roleRepository.save(role);
                }
                roles.add(role);
            }

            PersonEntity person = PersonEntity.builder()
                    .name(registerDTO.getName())
                    .surname(registerDTO.getSurname())
                    .username(registerDTO.getUsername())
                    .password(passwordEncoder.encode(registerDTO.getPassword()))
                    .email(registerDTO.getEmail())
                    .create_date(new Timestamp(System.currentTimeMillis()))
                    .roles(roles)
                    .build();

            personRepository.save(person);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseBuilder.buildResponse(true, ResponsesEnum.OK.toString(), "User registered successfully"));

            // todo: improve error registration error handling
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() != null && e.getCause().getMessage().contains("person_email_key")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseBuilder.buildResponse(false, ResponsesEnum.EMAIL_ALREADY_REGISTERED.toString(), "Email already registered"));
            } else if (e.getCause() != null && e.getCause().getMessage().contains("person_nickname_key")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseBuilder.buildResponse(false, ResponsesEnum.USERNAME_ALREADY_REGISTERED.toString(), "Nickname already registered"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseBuilder.buildResponse(false, ResponsesEnum.UNEXPECTED_ERROR.toString(), "Unexpected error"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBuilder.buildResponse(false, ResponsesEnum.UNEXPECTED_ERROR.toString(), "Unexpected error"));
        }
    }
}   