package com.safo.icos.modules.recruitment.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.constant.ErrorCode;
import com.safo.icos.modules.recruitment.domain.model.Candidate;
import com.safo.icos.modules.recruitment.domain.repository.CandidateRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public Candidate createCandidate(String firstName, String lastName, String email, String phone, String source, String resumeUrl) {
        Candidate candidate = Candidate.create(firstName, lastName, email, phone, source, resumeUrl);
        return candidateRepository.save(candidate);
    }

    public Candidate getCandidate(UUID id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Candidate not found", ErrorCode.NOT_FOUND));
    }
}
