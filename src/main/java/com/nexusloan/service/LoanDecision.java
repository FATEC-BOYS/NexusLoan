package com.nexusloan.service;

import com.nexusloan.enums.LoanStatus;

public record LoanDecision(LoanStatus status, String reason) {
}
