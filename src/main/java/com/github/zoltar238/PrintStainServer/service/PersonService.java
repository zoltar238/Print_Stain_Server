package com.github.zoltar238.PrintStainServer.service;

import com.github.zoltar238.PrintStainServer.dto.RegisterDto;
import org.springframework.http.ResponseEntity;

public interface PersonService {
    public ResponseEntity<?> registerPerson(RegisterDto registerDTO);
}
