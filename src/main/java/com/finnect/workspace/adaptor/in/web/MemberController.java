package com.finnect.workspace.adaptor.in.web;

import com.finnect.common.ApiUtils;
import com.finnect.common.ApiUtils.ApiResult;
import com.finnect.user.application.port.in.GetPersonalNameQuery;
import com.finnect.user.vo.UserId;
import com.finnect.user.vo.WorkspaceAuthority;
import com.finnect.workspace.adaptor.in.web.req.UpdateMemberRequest;
import com.finnect.workspace.adaptor.in.web.res.UpdateMemberResponse;
import com.finnect.workspace.application.port.in.*;
import com.finnect.workspace.domain.state.MemberState;
import com.finnect.workspace.adaptor.in.web.req.CreateMemberRequest;
import com.finnect.workspace.adaptor.in.web.res.CreateMemberResponse;
import com.finnect.workspace.adaptor.in.web.res.FindMembersResponse;
import com.finnect.workspace.adaptor.in.web.res.dto.MemberDto;
import com.finnect.workspace.adaptor.in.web.res.dto.MemberWithoutIdDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/workspaces/members")
public class MemberController {

    private final CreateMemberUsecase createMemberUsecase;
    private final FindMembersUsecase findMembersUsecase;
    private final GetPersonalNameQuery getPersonalNameQuery;
    private final UpdateMemberUsecase updateMemberUsecase;

    @Operation(
            summary = "Member 생성 API",
            description = """
                    주어진 workpspace id와 access token의 user id로 Member를 생성합니다.
                    이는 해당 사용자가 해당 워크스페이스에 속하게 됨을 의미합니다."""
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ApiResult<CreateMemberResponse>> createWorkspace(@RequestBody CreateMemberRequest request) {
        Long userId;
        try {
            userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());
        } catch (Exception e) {
            throw new RuntimeException("토큰에 사용자 ID가 누락되었습니다.");
        }

        CreateMemberCommand memberCommand = CreateMemberCommand.builder()
                .userId(userId)
                .workspaceId(request.getWorkspaceId())
                .nickname(getPersonalNameQuery.getPersonalName(UserId.parseOrNull(userId.toString())))
                .build();

        MemberState state = createMemberUsecase.createMember(memberCommand);

        MemberDto memberDto = MemberDto.from(state);
        CreateMemberResponse createMemberResponse = new CreateMemberResponse(memberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(HttpStatus.OK, createMemberResponse));
    }

    @Operation(
            summary = "Member 배열 조회 API",
            description = "주어진 access token의 workpspace id로 속한 Member의 배열을 조회합니다."
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<ApiResult<FindMembersResponse>> findMembers() {
        Long workspaceId;
        try {
            workspaceId = WorkspaceAuthority.from(
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            ).workspaceId().value();
        } catch (Exception e) {
            throw new RuntimeException("워크스페이스 ID가 누락되었습니다.");
        }

        List<MemberWithoutIdDto> members = findMembersUsecase.loadMembersByWorkspace(workspaceId)
                .stream()
                .map(MemberWithoutIdDto::new)
                .collect(Collectors.toList());
        FindMembersResponse findMembersResponse = new FindMembersResponse(members);
        return ResponseEntity.status(HttpStatus.OK).body(ApiUtils.success(HttpStatus.OK, findMembersResponse));
    }

    @Operation(
            summary = "Member 정보 변경 API",
            description = """
                    주어진 Member에 대한 정보와 access token의 user id와 workpspace id로 Member의 정보를 변경합니다.
                    Member에 대한 정보는 변경된 값만 포함해 요청합니다."""
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping
    ResponseEntity<ApiResult<UpdateMemberResponse>> updateMember(@RequestBody UpdateMemberRequest request) {
        Long userId;
        try {
            userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());
        } catch (Exception e) {
            throw new RuntimeException("토큰에 사용자 ID가 누락되었습니다.");
        }
        Long workspaceId;
        try {
            workspaceId = WorkspaceAuthority.from(
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            ).workspaceId().value();
        } catch (Exception e) {
            throw new RuntimeException("워크스페이스 ID가 누락되었습니다.");
        }

        MemberState updatedState = updateMemberUsecase.updateMember(
                UpdateMemberCommand.builder()
                        .userId(userId)
                        .workspaceId(workspaceId)
                        .nickname(request.getNickname())
                        .role(request.getRole())
                        .phone(request.getPhone())
                        .build()
        );
        UpdateMemberResponse updateMemberResponse = UpdateMemberResponse.of(
                MemberDto.from(updatedState)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiUtils.success(HttpStatus.OK, updateMemberResponse));

    }
}
