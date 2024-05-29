package com.finnect.user.application.service;

import com.finnect.user.application.password.PasswordGenerator;
import com.finnect.user.application.port.in.ResetPasswordUseCase;
import com.finnect.user.application.port.in.command.ResetPasswordCommand;
import com.finnect.user.application.port.in.command.VerifyEmailCodeCommand;
import com.finnect.user.application.port.in.exception.EmailCodeNotVerifiedException;
import com.finnect.user.application.port.out.LoadEmailCodePort;
import com.finnect.user.application.port.out.LoadUserPort;
import com.finnect.user.application.port.out.UpdateUserPort;
import com.finnect.user.domain.EmailCode;
import com.finnect.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordService implements ResetPasswordUseCase {

    private final LoadUserPort loadUserPort;
    private final UpdateUserPort updateUserPort;

    private final LoadEmailCodePort loadEmailCodePort;

    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ResetPasswordService(
            LoadUserPort loadUserPort,
            UpdateUserPort updateUserPort,
            LoadEmailCodePort loadEmailCodePort,
            PasswordGenerator passwordGenerator,
            PasswordEncoder passwordEncoder
    ) {
        this.loadUserPort = loadUserPort;
        this.updateUserPort = updateUserPort;

        this.loadEmailCodePort = loadEmailCodePort;

        this.passwordGenerator = passwordGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean verifyEmailCode(VerifyEmailCodeCommand command) {
        EmailCode emailCode = EmailCode.from(loadEmailCodePort.loadEmailCode(command.getEmail()));
        emailCode.verify(command.getCodeNumber());

        return emailCode.isVerified();
    }

    @Override
    public String resetPassword(ResetPasswordCommand command) throws EmailCodeNotVerifiedException {
        EmailCode emailCode = EmailCode.from(loadEmailCodePort.loadEmailCode(command.getEmail()));

        if (!emailCode.isVerified()) {
            throw new EmailCodeNotVerifiedException(emailCode.getEmail());
        }

        User user = User.from(loadUserPort.loadUserByEmail(emailCode.getEmail()));
        String password = passwordGenerator.generateRandomPassword();
        user.changePassword(passwordEncoder.encode(password));
        updateUserPort.updateUser(user);

        return password;
    }
}