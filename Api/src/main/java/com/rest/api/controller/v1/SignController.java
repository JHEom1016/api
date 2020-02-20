package com.rest.api.controller.v1;

import com.rest.api.advice.exception.CEmailSigninFailedException;
import com.rest.api.advice.exception.CUserExistException;
import com.rest.api.advice.exception.CUserNotFoundException;
import com.rest.api.config.security.JwtTokenProvider;
import com.rest.api.entity.User;
import com.rest.api.model.response.CommonResult;
import com.rest.api.model.response.SingleResult;
import com.rest.api.model.social.KakapProfile;
import com.rest.api.repo.UserJpaRepo;
import com.rest.api.service.ResponseService;
import com.rest.api.service.social.KakaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class SignController {

    private final UserJpaRepo userJpaRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;
    private final KakaoService kakaoService;

    @ApiOperation(value = "로그인", notes = "회원 이메일 로그인")
    @GetMapping(value = "/signin")
    public SingleResult<String> signin(
        @ApiParam(value = "회원 ID (eMail)", required = true) @RequestParam String id,
        @ApiParam(value = "회원 password", required = true) @RequestParam String password
    ) {
        User user = userJpaRepo.findByUid(id).orElseThrow(CEmailSigninFailedException::new);
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new CEmailSigninFailedException();

        return responseService.getSingleResult(jwtTokenProvider.createToken(user.getUsername(), user.getRoles()));
    }

    @ApiOperation(value = "가입", notes = "회원 신규 가입")
    @GetMapping(value = "/signup")
    public CommonResult signin(
            @ApiParam(value = "회원 ID (eMail)", required = true) @RequestParam String id,
            @ApiParam(value = "회원 password", required = true) @RequestParam String password,
            @ApiParam(value = "회원 이름", required = true) @RequestParam String name
    ) {
        userJpaRepo.save(User.builder()
                .uid(id)
                .name(name)
                .password(passwordEncoder.encode(password))
                .roles(Collections.singletonList("ROLE_USER"))
                .build());
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "Kakao 가입", notes = "Kakao 회원 신규 가입")
    @GetMapping(value = "/signin/{provider}")
    public SingleResult<String> signinByProvider(
            @ApiParam(value = "소셜 서비스 제공자", required = true, defaultValue = "kakao") @RequestParam String provider,
            @ApiParam(value = "소셜 서비스 Access_Token", required = true) @RequestParam String accessToken
    ) {
        KakapProfile kakapProfile = kakaoService.getKakaoProfile(accessToken);
        User user = userJpaRepo.findByUidAndProvider(String.valueOf(kakapProfile.getId()), provider).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles()));
    }

    @ApiOperation(value = "Kakao 가입 등록", notes = "Kakao 회원 신규 emdfhr")
    @GetMapping(value = "/signup/{provider}")
    public CommonResult signupByProvider(
            @ApiParam(value = "소셜 서비스 제공자", required = true, defaultValue = "kakao") @RequestParam String provider,
            @ApiParam(value = "소셜 서비스 Access_Token", required = true) @RequestParam String accessToken,
            @ApiParam(value = "사용자 이름", required = true) @RequestParam String name
    ) {
        KakapProfile kakapProfile = kakaoService.getKakaoProfile(accessToken);
        Optional<User> user = userJpaRepo.findByUidAndProvider(String.valueOf(kakapProfile.getId()), provider);
        if (user.isPresent())
            throw new CUserExistException();

        userJpaRepo.save(User.builder()
                .uid(String.valueOf(kakapProfile.getId()))
                .provider(provider)
                .name(name)
                .roles(Collections.singletonList("ROLE_USER"))
                .build()
        );
        return responseService.getSuccessResult();
    }
}
