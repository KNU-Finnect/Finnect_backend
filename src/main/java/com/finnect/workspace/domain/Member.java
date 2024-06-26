package com.finnect.workspace.domain;

import com.finnect.workspace.application.port.in.CreateMemberCommand;
import com.finnect.workspace.domain.state.MemberState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder
public class Member implements MemberState {
    private Long userId;
    private Long workspaceId;

    private String nickname;
    private String role;
    private String phone;

    public static Member from(MemberState state) {
        return Member.builder()
                .userId(state.getUserId())
                .workspaceId(state.getWorkspaceId())
                .nickname(state.getNickname())
                .role(state.getRole())
                .phone(state.getPhone())
                .build();
    }

    public static Member toDamin(CreateMemberCommand command) {
        return Member.builder()
                .userId(command.getUserId())
                .workspaceId(command.getWorkspaceId())
                .nickname(command.getNickname())
                .role("")
                .phone("")
                .build();
    }

    public void update(String nickname, String role, String phone) {
        if (nickname != null)
            this.nickname = nickname;
        if (role != null)
            this.role = role;
        if (phone != null)
            this.phone = phone;
    }
}
