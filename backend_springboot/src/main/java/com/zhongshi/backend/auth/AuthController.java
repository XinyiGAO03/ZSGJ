package com.zhongshi.backend.auth;

import com.zhongshi.backend.common.ApiResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        if (request.getEmail() == null || !request.getEmail().matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.ac\\.nz$")) {
            return ApiResponse.fail("请使用 .ac.nz 学校邮箱注册");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ApiResponse.fail("密码至少 6 位");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.fail("该邮箱已注册");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() == null ? "学生用户" : request.getNickname());
        user.setVerified(false);
        userRepository.save(user);

        return ApiResponse.success(toLoginResult(user));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
                .map(user -> ApiResponse.success(toLoginResult(user)))
                .orElse(ApiResponse.fail("邮箱或密码错误"));
    }

    private Map<String, Object> toLoginResult(User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("email", user.getEmail());
        result.put("nickname", user.getNickname());
        result.put("verified", user.isVerified());
        // 演示用 token；生产环境请改成 JWT。
        result.put("token", UUID.randomUUID().toString());
        return result;
    }
}
