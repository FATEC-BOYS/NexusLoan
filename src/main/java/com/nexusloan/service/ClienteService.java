package com.nexusloan.service;

import java.util.UUID;

import com.nexusloan.dto.ClienteRequest;
import com.nexusloan.dto.ClienteResponse;
import com.nexusloan.dto.PageResponse;
import com.nexusloan.dto.validation.CpfCnpjValidator;
import com.nexusloan.entity.Cliente;
import com.nexusloan.exception.ConflictException;
import com.nexusloan.exception.ResourceNotFoundException;
import com.nexusloan.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponse create(ClienteRequest request) {
        String document = CpfCnpjValidator.onlyDigits(request.document());
        if (clienteRepository.existsByDocument(document)) {
            throw new ConflictException("Já existe um cliente com o documento '" + document + "' neste tenant");
        }
        Cliente cliente = clienteRepository.save(new Cliente(
                request.name(),
                document,
                EmailNormalizer.normalize(request.email()),
                request.monthlyIncome()));
        return ClienteResponse.from(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponse findById(UUID id) {
        return ClienteResponse.from(getEntity(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ClienteResponse> list(Pageable pageable) {
        return PageResponse.from(clienteRepository.findAll(pageable).map(ClienteResponse::from));
    }

    /** Cliente de outro tenant é tratado como inexistente (404), sem revelar que ele existe. */
    Cliente getEntity(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }
}
