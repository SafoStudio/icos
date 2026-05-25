package com.safo.icos.modules.talent.recruitment.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Candidate {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String source;
    private final String resumeUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Candidate create(String firstName, String lastName, String email, String phone, String source, String resumeUrl) {
        return Candidate.builder()
                .id(UUID.randomUUID())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .source(source)
                .resumeUrl(resumeUrl)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
