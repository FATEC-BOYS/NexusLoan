package com.nexusloan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class TransactionConfig {

    /**
     * Transação programática para os fluxos em que o tenant precisa ser definido antes de a
     * transação (e, portanto, a sessão do Hibernate) começar, como o onboarding.
     */
    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
