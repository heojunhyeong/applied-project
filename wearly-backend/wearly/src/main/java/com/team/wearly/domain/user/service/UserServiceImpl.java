package com.team.wearly.domain.user.service;

import com.team.wearly.domain.user.dto.request.SignupRequest;
import com.team.wearly.domain.user.dto.response.SignupResponse;
import com.team.wearly.domain.seller.entity.Seller;
import com.team.wearly.domain.user.entity.User;
import com.team.wearly.domain.user.entity.enums.UserRole;
import com.team.wearly.domain.user.repository.AdminRepository;
import com.team.wearly.domain.seller.repository.SellerRepository;
import com.team.wearly.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 메인 로직
     * - USER: user 테이블에 저장
     * - SELLER: seller 테이블에 저장
     * - ADMIN 단어 차단: 이메일, 닉네임, 비밀번호 등 모든 필드에서 검증
     */
    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. ADMIN 단어 차단 검증 (추가 검증 - 이중 안전장치)
        validateNoAdminWord(request);

        // 2. 비밀번호 재확인 검증
        if (!request.getUserPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 재확인이 일치하지 않습니다");
        }

        // 3. 역할 검증
        if (request.getRoleType() == UserRole.ADMIN) {
            throw new IllegalArgumentException("ADMIN으로는 회원가입할 수 없습니다");
        }

        if (request.getRoleType() != UserRole.USER && request.getRoleType() != UserRole.SELLER) {
            throw new IllegalArgumentException("USER 또는 SELLER 역할만 선택할 수 있습니다");
        }

        // 4. 모든 테이블에서 아이디, 이메일, 닉네임 중복 체크
        checkDuplicates(request);

        // 5. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getUserPassword());

        // 6. 역할에 따라 다른 테이블에 저장
        if (request.getRoleType() == UserRole.USER) {
            return saveUser(request, encodedPassword);
        } else if (request.getRoleType() == UserRole.SELLER) {
            return saveSeller(request, encodedPassword);
        } else {
            throw new IllegalArgumentException("유효하지 않은 역할입니다");
        }
    }

    /**
     * USER 회원가입 - user 테이블에 저장
     */
    private SignupResponse saveUser(SignupRequest request, String encodedPassword) {
        User user = User.builder()
                .userName(request.getUserId())
                .userPassword(encodedPassword)
                .userEmail(request.getUserEmail())
                .userNickname(request.getNickName())
                .build();

        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getUserName(),
                savedUser.getUserEmail(),
                savedUser.getUserNickname(),
                UserRole.USER,
                "회원가입이 완료되었습니다.",
                savedUser.getCreatedDate(),
                savedUser.getUpdatedDate()
        );
    }

    /**
     * SELLER 회원가입 - seller 테이블에 저장
     */
    private SignupResponse saveSeller(SignupRequest request, String encodedPassword) {
        Seller seller = Seller.builder()
                .userName(request.getUserId())
                .userPassword(encodedPassword)
                .userEmail(request.getUserEmail())
                .userNickname(request.getNickName())
                .build();

        Seller savedSeller = sellerRepository.save(seller);

        return new SignupResponse(
                savedSeller.getId(),
                savedSeller.getUserName(),
                savedSeller.getUserEmail(),
                savedSeller.getUserNickname(),
                UserRole.SELLER,
                "회원가입이 완료되었습니다.",
                savedSeller.getCreatedDate(),
                savedSeller.getUpdatedDate()
        );
    }

    /**
     * 모든 테이블에서 중복 체크 (user, seller, admin)
     */
    private void checkDuplicates(SignupRequest request) {
        // 아이디 중복 체크 (모든 테이블)
        if (userRepository.existsByUserName(request.getUserId()) ||
                sellerRepository.existsByUserName(request.getUserId()) ||
                adminRepository.existsByUserName(request.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다");
        }

        // 이메일 중복 체크 (모든 테이블)
        if (userRepository.existsByUserEmail(request.getUserEmail()) ||
                sellerRepository.existsByUserEmail(request.getUserEmail()) ||
                adminRepository.existsByUserEmail(request.getUserEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        // 닉네임 중복 체크 (모든 테이블)
        if (userRepository.existsByUserNickname(request.getNickName()) ||
                sellerRepository.existsByUserNickname(request.getNickName()) ||
                adminRepository.existsByUserNickname(request.getNickName())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다");
        }
    }

    /**
     * 모든 입력 필드에서 "admin" 단어 차단 검증
     */
    private void validateNoAdminWord(SignupRequest request) {
        String adminPattern = "admin";

        // 아이디 체크
        if (request.getUserId() != null) {
            String userIdStr = request.getUserId().toLowerCase();
            if (userIdStr.contains(adminPattern)) {
                throw new IllegalArgumentException("아이디에 'admin'이라는 단어는 사용할 수 없습니다");
            }
        }

        // 비밀번호 체크
        if (request.getUserPassword() != null) {
            String password = request.getUserPassword().toLowerCase();
            if (password.contains(adminPattern)) {
                throw new IllegalArgumentException("비밀번호에 'admin'이라는 단어는 사용할 수 없습니다");
            }
        }

        // 비밀번호 재확인 체크
        if (request.getConfirmPassword() != null) {
            String confirmPassword = request.getConfirmPassword().toLowerCase();
            if (confirmPassword.contains(adminPattern)) {
                throw new IllegalArgumentException("비밀번호 재확인에 'admin'이라는 단어는 사용할 수 없습니다");
            }
        }

        // 이메일 체크
        if (request.getUserEmail() != null) {
            String email = request.getUserEmail().toLowerCase();
            if (email.contains(adminPattern)) {
                throw new IllegalArgumentException("이메일에는 'admin'이라는 단어는 사용할 수 없습니다");
            }
        }

        // 닉네임 체크
        if (request.getNickName() != null) {
            String nickName = request.getNickName().toLowerCase();
            if (nickName.contains(adminPattern)) {
                throw new IllegalArgumentException("닉네임에는 'admin'이라는 단어는 사용할 수 없습니다");
            }
        }
    }
}