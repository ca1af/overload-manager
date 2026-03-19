package com.calaf.overloadmanager.user.service

import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.user.domain.User
import com.calaf.overloadmanager.user.dto.UpdateProfileRequest
import com.calaf.overloadmanager.user.dto.UserProfileResponse
import com.calaf.overloadmanager.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) {

    fun getProfile(userId: Long): UserProfileResponse {
        val user = findActiveUser(userId)
        return user.toProfileResponse()
    }

    @Transactional
    fun updateProfile(userId: Long, request: UpdateProfileRequest): UserProfileResponse {
        val user = findActiveUser(userId)
        request.nickname?.let { user.nickname = it }
        request.weightUnit?.let { user.weightUnit = it }
        request.weeklyGoalSessions?.let { user.weeklyGoalSessions = it }
        return userRepository.save(user).toProfileResponse()
    }

    @Transactional
    fun softDelete(userId: Long) {
        val user = findActiveUser(userId)
        user.deletedAt = LocalDateTime.now()
        userRepository.save(user)
    }

    private fun findActiveUser(userId: Long): User {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "User not found")
    }

    private fun User.toProfileResponse() = UserProfileResponse(
        id = id,
        email = email,
        nickname = nickname,
        weightUnit = weightUnit,
        weeklyGoalSessions = weeklyGoalSessions,
        emailVerified = emailVerified,
        createdAt = createdAt,
    )
}
