package com.nexusloan.service;

import java.util.UUID;

import com.nexusloan.dto.EmprestimoRequest;
import com.nexusloan.dto.EmprestimoResponse;
import com.nexusloan.dto.PageResponse;
import com.nexusloan.entity.Cliente;
import com.nexusloan.entity.Emprestimo;
import com.nexusloan.exception.ResourceNotFoundException;
import com.nexusloan.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final ClienteService clienteService;
    private final LoanApprovalPolicy approvalPolicy;

    @Transactional
    public EmprestimoResponse request(EmprestimoRequest request) {
        Cliente cliente = clienteService.getEntity(request.clienteId());

        Emprestimo emprestimo = new Emprestimo(cliente, request.amount());
        LoanDecision decision = approvalPolicy.evaluate(request.amount(), cliente.getMonthlyIncome());
        emprestimo.decide(decision.status(), decision.reason());

        return EmprestimoResponse.from(emprestimoRepository.save(emprestimo));
    }

    @Transactional(readOnly = true)
    public EmprestimoResponse findById(UUID id) {
        return emprestimoRepository.findById(id)
                .map(EmprestimoResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo", id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmprestimoResponse> list(Pageable pageable) {
        return PageResponse.from(emprestimoRepository.findAll(pageable).map(EmprestimoResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmprestimoResponse> listByCliente(UUID clienteId, Pageable pageable) {
        clienteService.getEntity(clienteId);
        return PageResponse.from(emprestimoRepository.findByClienteId(clienteId, pageable).map(EmprestimoResponse::from));
    }
}
