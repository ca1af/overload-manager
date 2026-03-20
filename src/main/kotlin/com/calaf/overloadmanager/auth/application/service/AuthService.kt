package com.calaf.overloadmanager.auth.application.service

import com.calaf.overloadmanager.auth.domain.model.RefreshToken
import com.calaf.overloadmanager.auth.domain.port.`in`.*
import com.calaf.overloadmanager.auth.domain.port.out.RefreshTokenRepository
import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.infrastructure.jwt.JwtTokenProvider
import com.calaf.overloadmanager.user.domain.model.User
import com.calaf.overloadmanager.user.domain.port.out.UserRepository
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
) : RegisterUseCase, LoginUseCase, RefreshTokenUseCase, LogoutUseCase {

    override fun register(command: RegisterCommand): LoginResult {
        if (userRepository.existsByEmail(command.email)) {
            throw AppException(ErrorCode.EMAIL_ALREADY_EXISTS)
        }

        val user = userRepository.save(
            User(
                email = command.email,
                passwordHash = passwordEncoder.encode(command.password)!!,
                nickname = command.nickname,
            )
        )

        return generateLoginResult(user)
    }

    override fun login(command: LoginCommand): LoginResult {
        val user = userRepository.findByEmail(command.email)
            ?: throw AppException(ErrorCode.INVALID_CREDENTIALS)

        if (!passwordEncoder.matches(command.password, user.passwordHash)) {
            throw AppException(ErrorCode.INVALID_CREDENTIALS)
        }

        return generateLoginResult(user)
    }

    override fun refresh(refreshToken: String): TokenResult {
        val storedToken = refreshTokenRepository.findByToken(refreshToken)
            ?: throw AppException(ErrorCode.INVALID_REFRESH_TOKEN)

        if (storedToken.expiresAt.isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken)
            throw AppException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val user = userRepository.findById(storedToken.userId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "User not found")

        refreshTokenRepository.delete(storedToken)

        val accessToken = jwtTokenProvider.generateAccessToken(user.id, user.email)
        val newRefreshToken = createRefreshToken(user.id)

        return TokenResult(
            accessToken = accessToken,
            refreshToken = newRefreshToken.token,
            expiresIn = 3600,
        )
    }

    override fun logout(refreshToken: String) {
        refreshTokenRepository.deleteByToken(refreshToken)
    }

    private fun generateLoginResult(user: User): LoginResult {
        val accessToken = jwtTokenProvider.generateAccessToken(user.id, user.email)
        val refreshToken = createRefreshToken(user.id)

        return LoginResult(
            accessToken = accessToken,
            refreshToken = refreshToken.token,
            expiresIn = 3600,
            user = UserInfoResult(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
            ),
        )
    }

    private fun createRefreshToken(userId: Long): RefreshToken {
        val token = jwtTokenProvider.generateRefreshToken()
        val expiresAt = LocalDateTime.now().plusDays(jwtTokenProvider.getRefreshTokenExpiryDays())
        return refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                token = token,
                expiresAt = expiresAt,
            )
        )
    }
}
