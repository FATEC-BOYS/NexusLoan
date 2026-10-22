package com.nexusloan.controller;

import java.net.URI;
import java.util.UUID;

import com.nexusloan.dto.ClienteRequest;
import com.nexusloan.dto.ClienteResponse;
import com.nexusloan.dto.PageResponse;
import com.nexusloan.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse created = clienteService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public PageResponse<ClienteResponse> list(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return clienteService.list(pageable);
    }

    @GetMapping("/{id}")
    public ClienteResponse findById(@PathVariable UUID id) {
        return clienteService.findById(id);
    }
}
