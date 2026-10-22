package com.nexusloan.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import com.nexusloan.dto.EmprestimoRequest;
import com.nexusloan.dto.EmprestimoResponse;
import com.nexusloan.entity.Cliente;
import com.nexusloan.entity.Emprestimo;
import com.nexusloan.enums.LoanStatus;
import com.nexusloan.exception.ResourceNotFoundException;
import com.nexusloan.repository.EmprestimoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private LoanApprovalPolicy approvalPolicy;

    @InjectMocks
    private EmprestimoService service;

    @Test
    void appliesPolicyDecisionAndPersists() {
        UUID clienteId = UUID.randomUUID();
        BigDecimal income = new BigDecimal("4000.00");
        BigDecimal amount = new BigDecimal("12000.00");
        Cliente cliente = new Cliente("Maria", "52998224725", "maria@example.com", income);
        when(clienteService.getEntity(clienteId)).thenReturn(cliente);
        when(approvalPolicy.evaluate(amount, income)).thenReturn(new LoanDecision(LoanStatus.APPROVED, "ok"));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(inv -> inv.getArgument(0));

        EmprestimoResponse response = service.request(new EmprestimoRequest(clienteId, amount));

        assertThat(response.status()).isEqualTo(LoanStatus.APPROVED);
        assertThat(response.reason()).isEqualTo("ok");
        assertThat(response.amount()).isEqualByComparingTo(amount);
    }

    @Test
    void failsWhenClienteIsNotVisibleInTenant() {
        UUID clienteId = UUID.randomUUID();
        when(clienteService.getEntity(clienteId)).thenThrow(new ResourceNotFoundException("Cliente", clienteId));

        assertThatThrownBy(() -> service.request(new EmprestimoRequest(clienteId, BigDecimal.ONE)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(emprestimoRepository, never()).save(any());
    }
}
