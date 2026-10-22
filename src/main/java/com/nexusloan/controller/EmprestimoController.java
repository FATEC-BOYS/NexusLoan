package com.nexusloan.controller;

import java.net.URI;
import java.util.UUID;

import com.nexusloan.dto.EmprestimoRequest;
import com.nexusloan.dto.EmprestimoResponse;
import com.nexusloan.dto.PageResponse;
import com.nexusloan.service.EmprestimoService;
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
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoResponse> request(@Valid @RequestBody EmprestimoRequest request) {
        EmprestimoResponse created = emprestimoService.request(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/emprestimos/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public PageResponse<EmprestimoResponse> list(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return emprestimoService.list(pageable);
    }

    @GetMapping("/{id}")
    public EmprestimoResponse findById(@PathVariable UUID id) {
        return emprestimoService.findById(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public PageResponse<EmprestimoResponse> listByCliente(
            @PathVariable UUID clienteId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return emprestimoService.listByCliente(clienteId, pageable);
    }
}
