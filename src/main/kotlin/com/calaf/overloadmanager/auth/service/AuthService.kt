package com.calaf.overloadmanager.auth.service

import com.calaf.overloadmanager.auth.domain.RefreshToken
import com.calaf.overloadmanager.auth.dto.*
import com.calaf.overloadmanager.auth.repository.RefreshTokenRepository
import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.infrastructure.jwt.JwtTokenProvider
import com.calaf.overloadmanager.user.domain.User
import com.calaf.overloadmanager.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) {

    fun register(request: RegisterRequest): LoginResponse {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.email)) {
            throw AppException(ErrorCode.EMAIL_ALREADY_EXISTS)
        }

        val user = userRepository.save(
            User(
                email = request.email,
                passwordHash = passwordEncoder.encode(request.password)!!,
                nickname = request.nickname,
            )
        )

        return generateLoginResponse(user)
    }

    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmailAndDeletedAtIsNull(request.email)
            ?: throw AppException(ErrorCode.INVALID_CREDENTIALS)

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw AppException(ErrorCode.INVALID_CREDENTIALS)
        }

        return generateLoginResponse(user)
    }

    fun refresh(request: RefreshRequest): TokenResponse {
        val storedToken = refreshTokenRepository.findByToken(request.refreshToken)
            ?: throw AppException(ErrorCode.INVALID_REFRESH_TOKEN)

        if (storedToken.expiresAt.isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken)
            throw AppException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val user = storedToken.user
        refreshTokenRepository.delete(storedToken)

        val accessToken = jwtTokenProvider.generateAccessToken(user.id, user.email)
        val newRefreshToken = createRefreshToken(user)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = newRefreshToken.token,
            expiresIn = 3600,
        )
    }

    fun logout(refreshToken: String) {
        refreshTokenRepository.deleteByToken(refreshToken)
    }

    private fun generateLoginResponse(user: User): LoginResponse {
        val accessToken = jwtTokenProvider.generateAccessToken(user.id, user.email)
        val refreshToken = createRefreshToken(user)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken.token,
            expiresIn = 3600,
            user = UserInfo(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
            ),
        )
    }

    private fun createRefreshToken(user: User): RefreshToken {
        val token = jwtTokenProvider.generateRefreshToken()
        val expiresAt = LocalDateTime.now().plusDays(jwtTokenProvider.getRefreshTokenExpiryDays())
        return refreshTokenRepository.save(
            RefreshToken(
                user = user,
                token = token,
                expiresAt = expiresAt,
            )
        )
    }
}
