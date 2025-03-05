package com.github.zoltar238.PrintStainServer.service;

import com.github.zoltar238.PrintStainServer.dto.RegisterDto;
import com.github.zoltar238.PrintStainServer.exceptions.EmailAlreadyExistsException;
import com.github.zoltar238.PrintStainServer.exceptions.UnexpectedException;
import com.github.zoltar238.PrintStainServer.exceptions.UsernameAlreadyExistsException;
import com.github.zoltar238.PrintStainServer.persistence.entity.PersonEntity;
import com.github.zoltar238.PrintStainServer.persistence.entity.RoleEntity;
import com.github.zoltar238.PrintStainServer.persistence.entity.RoleEnum;
import com.github.zoltar238.PrintStainServer.persistence.repository.PersonRepository;
import com.github.zoltar238.PrintStainServer.persistence.repository.RoleRepository;
import com.github.zoltar238.PrintStainServer.utils.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class PersonServiceImp implements PersonService {

    private final PersonRepository personRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public PersonServiceImp(PasswordEncoder passwordEncoder, PersonRepository personRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public ResponseEntity<?> registerPerson(RegisterDto registerDTO) {
        try {
            Set<RoleEntity> roles = new HashSet<>();

            // look for the role
            for (String roleName : registerDTO.getRoles()) {
                Optional<RoleEntity> roleOpt = roleRepository.findByName(RoleEnum.valueOf(roleName));
                RoleEntity role;

                if (roleOpt.isPresent()) {
                    // if the role exists, use it
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

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseBuilder.buildResponse(true, "User registered successfully", null));

            // todo: improve error registration error handling
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Key (email)")) {
                throw new EmailAlreadyExistsException("The email \"" + registerDTO.getEmail() + "\" is already registered.");
            } else if (e.getMessage().contains("Key (username)")) {
                throw new UsernameAlreadyExistsException("The username \"" + registerDTO.getUsername() + "\" is already registered.");
            } else {
                throw new UnexpectedException("Unexpected error: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new UnexpectedException("Unexpected error: " + e.getMessage(), e);
        }
    }
}
