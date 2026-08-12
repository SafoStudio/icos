package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.modules.talent.recruitment.api.dto.CandidateDto;
import com.safo.icos.modules.talent.recruitment.domain.model.Candidate;
import com.safo.icos.modules.talent.recruitment.domain.repository.CandidateRepository;
import com.safo.icos.modules.talent.recruitment.infrastructure.mapper.RecruitmentMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for candidate management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CandidateService {

  private final CandidateRepository candidateRepository;
  private final RecruitmentMapper recruitmentMapper;

  /**
   * Creates a new candidate.
   *
   * @param firstName candidate's first name
   * @param lastName  candidate's last name
   * @param email     candidate's email
   * @param phone     candidate's phone number
   * @param source    where the candidate came from (LinkedIn, referral, etc.)
   * @param resumeUrl URL to the uploaded resume
   * @return the created candidate
   */
  public Candidate createCandidate(String firstName, String lastName, String email,
                                   String phone, String source, String resumeUrl) {
    Candidate candidate = Candidate.create(firstName, lastName, email, phone, source, resumeUrl);
    return candidateRepository.save(candidate);
  }

  public CandidateDto getCandidate(UUID id) {
    Candidate candidate = candidateRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
    return recruitmentMapper.toDto(candidate);
  }
}
