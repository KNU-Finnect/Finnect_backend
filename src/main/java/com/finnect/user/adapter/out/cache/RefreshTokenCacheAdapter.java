package com.finnect.user.adapter.out.cache;

import com.finnect.user.adapter.out.cache.entity.RefreshTokenEntity;
import com.finnect.user.adapter.out.cache.entity.RefreshTokenRepository;
import com.finnect.user.application.port.out.DeleteRefreshTokenPort;
import com.finnect.user.application.port.out.LoadRefreshTokenPort;
import com.finnect.user.application.port.out.SaveRefreshTokenPort;
import com.finnect.user.application.port.out.error.RefreshTokenNotFoundException;
import com.finnect.user.domain.state.RefreshTokenState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCacheAdapter implements LoadRefreshTokenPort, SaveRefreshTokenPort, DeleteRefreshTokenPort {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshTokenState loadToken(String token) {
        return refreshTokenRepository.findById(token)
                .orElseThrow(() -> new RefreshTokenNotFoundException(token));
    }

    @Override
    public void saveToken(RefreshTokenState refreshTokenState) {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.from(refreshTokenState);

        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }
}
