//package com.safo.icos.modules.user.domain.service;
//
//import com.safo.icos.modules.user.domain.exception.EmailAlreadyExistsException;
//import com.safo.icos.modules.user.domain.exception.UserNotFoundException;
//import com.safo.icos.modules.user.domain.model.Email;
//import com.safo.icos.modules.user.domain.model.User;
//import com.safo.icos.modules.user.domain.model.UserId;
//import com.safo.icos.modules.user.domain.model.UserName;
//import com.safo.icos.modules.user.domain.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import java.util.UUID;
//
//@RequiredArgsConstructor
//public class UserDomainService {
//    private final UserRepository userRepository;
//
//    public User createUser(UserName name, Email email, UUID companyId, UUID createdBy) {
//        if (userRepository.existsByEmailAndCompanyId(email, companyId)) {
//            throw new EmailAlreadyExistsException(email, companyId);
//        }
//        return User.invite(name, email, companyId, createdBy);
//    }
//
//    public User updateUserEmail(UserId userId, Email newEmail, UUID companyId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//
//        if (userRepository.existsByEmailAndCompanyId(newEmail, companyId)) {
//            throw new EmailAlreadyExistsException(newEmail, companyId);
//        }
//
//        user.changeEmail(newEmail);
//        return user;
//    }
//
//    public User activateUser(UserId userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//        user.activate();
//        return user;
//    }
//
//    public User suspendUser(UserId userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//        user.suspend();
//        return user;
//    }
//
//    public User terminateUser(UserId userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException(userId));
//        user.terminate();
//        return user;
//    }
//}
