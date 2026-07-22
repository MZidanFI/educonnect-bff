package com.edu.auth_service.service;

import com.edu.auth_service.dto.ResponseLoginDTO;
import com.edu.auth_service.model.ModelAccount;
import com.edu.auth_service.repository.RepositoryAccount;
import com.edu.auth_service.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceAuth {

    @Autowired
    private RepositoryAccount repositoryAccount;

    @Autowired
    private JwtUtil jwtUtil;

    public ModelAccount register(ModelAccount account) {
        // NOTE: untuk studi kasus, password disimpan plain text dulu untuk
        // kesederhanaan.
        // Untuk produksi wajib pakai BCryptPasswordEncoder.
        return repositoryAccount.save(account);
    }

    public Optional<ResponseLoginDTO> login(String username, String password) {
        Optional<ModelAccount> accountOpt = repositoryAccount.findByUsername(username);

        if (accountOpt.isPresent() && accountOpt.get().getPassword().equals(password)) {
            ModelAccount account = accountOpt.get();
            String token = jwtUtil.generateToken(account.getUserId(), account.getRole());
            return Optional.of(new ResponseLoginDTO(token, account.getUserId(), account.getRole()));
        }
        return Optional.empty();
    }
}