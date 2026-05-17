package com.safo.icos.modules.user.service;

import com.safo.icos.modules.user.entity.User;
import com.safo.icos.modules.user.entity.UserProfile;
import com.safo.icos.modules.user.repository.UserProfileRepository;
import com.safo.icos.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;

    public UserProfile getProfileByUserId(UUID userId) {
        return profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
    }

    @Transactional
    public UserProfile updateProfile(UUID userId, UserProfile input) {
        UserProfile profile = profileRepository.findById(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    return UserProfile.builder().user(user).id(userId).build();
                });

        profile.setPhoneNumber(input.getPhoneNumber());
        profile.setAddress(input.getAddress());
        profile.setBio(input.getBio());
        profile.setBirthDate(input.getBirthDate());

        return profileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(UUID userId) {
        if (!profileRepository.existsById(userId)) {
            throw new RuntimeException("Profile not found");
        }
        profileRepository.deleteById(userId);
    }
}
