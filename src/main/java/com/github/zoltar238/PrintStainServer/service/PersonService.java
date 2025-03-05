package com.github.zoltar238.PrintStainServer.service;

import com.github.zoltar238.PrintStainServer.dto.RegisterDto;
import com.github.zoltar238.PrintStainServer.dto.ResponseApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface PersonService {

    ResponseEntity<ResponseApi<String>> registerPerson(RegisterDto registerDTO);

    ResponseEntity<ResponseApi<String>> deleteUser();

    ResponseEntity<ResponseApi<String>> updatePassword();
}
