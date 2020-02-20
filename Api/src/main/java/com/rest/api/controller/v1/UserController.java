package com.rest.api.controller.v1;

import com.rest.api.advice.exception.CUserNotFoundException;
import com.rest.api.entity.User;
import com.rest.api.model.response.CommonResult;
import com.rest.api.model.response.ListResult;
import com.rest.api.model.response.SingleResult;
import com.rest.api.repo.UserJpaRepo;
import com.rest.api.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"2. User"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class UserController {

    private final UserJpaRepo userJpaRepo;
    private final ResponseService responseService;      // 결과 처리용 서비스

    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 Access_Token", required = true, dataType = "String", paramType = "header")
            }
    )
    @ApiOperation(value = "회원 조회", notes = "모든 회원 조회")
    @GetMapping(value = "/users")
    public ListResult<User> findAllUser() {
        return responseService.getListResult(userJpaRepo.findAll());
    }

    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 Access_Token", required = true, dataType = "String", paramType = "header")
            }
    )
    @ApiOperation(value = "회원 조회", notes = "단일 회원 조회")
    @GetMapping(value = "/user/{msrl}")
    public SingleResult<User> findUserById(
            @ApiParam(value = "회원 ID", required = true) @PathVariable long msrl,
            @ApiParam(value = "언어", defaultValue = "en") @RequestParam String lang
    ) throws Exception {
        // SecurityContext의 인증 회원 정보 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        // 결과 단건 리턴
        return responseService.getSingleResult(userJpaRepo.findById(msrl).orElseThrow(CUserNotFoundException::new));
    }

    @ApiOperation(value = "회원 입력", notes = "회원을 입력")
    @PostMapping(value = "/user")
    public SingleResult<User> save(
            @ApiParam(value = "회원 ID", required = true) @RequestParam String uid,
            @ApiParam(value = "회원 명", required = true) @RequestParam String name
    ) {
        User user = User.builder()
                .uid(uid)
                .name(name)
                .build();
        return responseService.getSingleResult(userJpaRepo.save(user));
    }

    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 Access_Token", required = true, dataType = "String", paramType = "header")
            }
    )
    @ApiOperation(value = "회원 수정", notes = "회원을 수정")
    @PutMapping(value = "/user")
    public SingleResult<User> save(
            @ApiParam(value = "회원 Sequence", required = true) @RequestParam Long msrl,
            @ApiParam(value = "회원 ID", required = true) @RequestParam String uid,
            @ApiParam(value = "회원 명", required = true) @RequestParam String name
    ) {
        User user = User.builder()
                .msrl(msrl)
                .uid(uid)
                .name(name)
                .build();
        return responseService.getSingleResult(userJpaRepo.save(user));
    }

    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 Access_Token", required = true, dataType = "String", paramType = "header")
            }
    )
    @ApiOperation(value = "회원 삭제", notes = "회원 정보 삭제")
    @DeleteMapping(value = "/user/{msrl}")
    public CommonResult delete(
            @ApiParam(value = "회원 Sequence", required = true) @RequestParam Long msrl
    ) {
        userJpaRepo.deleteById(msrl);
        return responseService.getSuccessResult();
    }

}
