package com.finnect.workspace.domain;

import com.finnect.workspace.domain.state.InvitationState;
import jakarta.mail.internet.MimeMessage;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE) @Builder(access = PRIVATE)
@Getter
@Slf4j
public class Invitation implements InvitationState {
    private String receiver;
    private Boolean succeed;
    private final String sender;
    private final String workspaceName;

    private static String invitaionUrl = "https://localhost:8008";

    public static Invitation of(String receiverEmail, String senderName, String workspaceName) {
        return Invitation.builder()
                .receiver(receiverEmail)
                .succeed(Boolean.FALSE)
                .sender(senderName)
                .workspaceName(workspaceName)
                .build();
    }

    private void updateResult(Boolean result) {
        this.succeed = result;
    }

    public void sendEmail(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        MimeMessagePreparator perparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setTo(receiver);
                helper.setSubject("[Finnect] 귀하를 워크스페이스에 초대합니다.");

                Context context = new Context();
                context.setVariable("username", sender);
                context.setVariable("workspace_name", workspaceName);
                context.setVariable("invite_url", invitaionUrl);
                helper.setText(templateEngine.process("email", context), true);
            }
        };

        try {
            javaMailSender.send(perparator);
        } catch (MailException e) {
            log.info(receiver + "에 대한 초대를 실패했습니다.");
            return;
        }
        updateResult(Boolean.TRUE);
    }
}
