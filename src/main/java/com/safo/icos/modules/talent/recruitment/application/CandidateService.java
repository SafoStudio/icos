package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.modules.talent.recruitment.domain.model.Candidate;
import com.safo.icos.modules.talent.recruitment.domain.repository.CandidateRepository;
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
}
