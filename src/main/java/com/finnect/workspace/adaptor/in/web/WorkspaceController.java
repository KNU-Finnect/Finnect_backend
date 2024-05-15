package com.finnect.workspace.adaptor.in.web;

import com.finnect.common.ApiUtils;
import com.finnect.common.ApiUtils.ApiResult;
import com.finnect.workspace.WorkspaceState;
import com.finnect.workspace.adaptor.in.web.req.CreateWorkspaceRequest;
import com.finnect.workspace.adaptor.in.web.req.InviteMembersRequest;
import com.finnect.workspace.adaptor.in.web.req.RenameWorkspaceRequest;
import com.finnect.workspace.adaptor.in.web.res.CreateWorkspaceResponse;
import com.finnect.workspace.adaptor.in.web.res.GetWorkspacesResponse;
import com.finnect.workspace.adaptor.in.web.res.InviteMembersResponse;
import com.finnect.workspace.adaptor.in.web.res.RenameWorkspaceResponse;
import com.finnect.workspace.adaptor.in.web.res.dto.InvitationDto;
import com.finnect.workspace.adaptor.in.web.res.dto.WorkspaceDto;
import com.finnect.workspace.adaptor.in.web.res.dto.WorkspaceWithoutIdDto;
import com.finnect.workspace.application.port.in.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WorkspaceController {

    private final CreateWorkspaceUsecase createWorkspaceUsecase;
    private final RenameWorkspaceUsecase renameWorkspaceUsecase;
    private final InviteMembersUsecase inviteMembersUsecase;
    private final GetWorkspacesQuery getWorkspacesQuery;

    @PostMapping("/workspaces")
    public ResponseEntity<ApiResult<CreateWorkspaceResponse>> createWorkspace(@RequestBody CreateWorkspaceRequest request) {
        CreateWorkspaceCommand workspaceCommand = CreateWorkspaceCommand.builder()
                .workspaceName(request.getWorkspaceName())
                .userId(1L)
                .build();

        WorkspaceState state = createWorkspaceUsecase.createWorkspace(workspaceCommand);

        WorkspaceWithoutIdDto workspaceWithoutIdDto = new WorkspaceWithoutIdDto(state.getWorkspaceName());

        CreateWorkspaceResponse createWorkspaceResponse = new CreateWorkspaceResponse(workspaceWithoutIdDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(HttpStatus.CREATED, createWorkspaceResponse));
    }

    @PutMapping("/workspaces")
    public ResponseEntity<ApiResult<RenameWorkspaceResponse>> renameWorkspace(@RequestBody RenameWorkspaceRequest request) {
        RenameWorkspaceCommand renameCommand = RenameWorkspaceCommand.builder()
                .workspaceId(1L)
                .newName(request.getWorkspaceName())
                .build();

        WorkspaceState state = renameWorkspaceUsecase.renameWorkspace(renameCommand);

        WorkspaceWithoutIdDto workspaceWithoutIdDto = new WorkspaceWithoutIdDto(state.getWorkspaceName());
        RenameWorkspaceResponse renameWorkspaceResponse = new RenameWorkspaceResponse(workspaceWithoutIdDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiUtils.success(HttpStatus.OK, renameWorkspaceResponse));
    }

    @GetMapping("/workspaces")
    public ResponseEntity<ApiResult<GetWorkspacesResponse>> getWorkspaces() {
        Long userId = 1L;

        List<WorkspaceDto> workspaceStates = getWorkspacesQuery.getWorkspaces(userId)
                .stream()
                .map(WorkspaceDto::new)
                .collect(Collectors.toList());
        GetWorkspacesResponse getWorkspacesResponse = new GetWorkspacesResponse(workspaceStates);
        return ResponseEntity.status(HttpStatus.OK).body(ApiUtils.success(HttpStatus.OK, getWorkspacesResponse));
    }

    @PostMapping("/workspaces/invitation")
    public ResponseEntity<ApiResult<InviteMembersResponse>> inviteMembers(@RequestBody InviteMembersRequest request) {
        List<InviteMembersCommand> cmds = request.getEmails()
                .stream()
                .map(InviteMembersCommand::new)
                .collect(Collectors.toList());

        List<InvitationDto> invitations = inviteMembersUsecase.inviteMembers(cmds)
                .stream()
                .map(InvitationDto::new)
                .collect(Collectors.toList());
        InviteMembersResponse inviteMembersResponse = new InviteMembersResponse(invitations);
        return ResponseEntity.status(HttpStatus.OK).body(
               ApiUtils.success(HttpStatus.OK, inviteMembersResponse)
        );
    }
}
