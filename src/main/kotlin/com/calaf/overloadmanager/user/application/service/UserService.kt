package com.calaf.overloadmanager.user.application.service

import com.calaf.overloadmanager.common.AppException
import com.calaf.overloadmanager.common.ErrorCode
import com.calaf.overloadmanager.user.domain.model.User
import com.calaf.overloadmanager.user.domain.port.`in`.*
import com.calaf.overloadmanager.user.domain.port.out.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) : GetProfileUseCase, UpdateProfileUseCase, DeleteAccountUseCase {

    override fun getProfile(userId: Long): UserProfileResult {
        val user = findActiveUser(userId)
        return user.toProfileResult()
    }

    @Transactional
    override fun updateProfile(userId: Long, command: UpdateProfileCommand): UserProfileResult {
        val user = findActiveUser(userId)
        val updated = user.copy(
            nickname = command.nickname ?: user.nickname,
            weightUnit = command.weightUnit ?: user.weightUnit,
            weeklyGoalSessions = command.weeklyGoalSessions ?: user.weeklyGoalSessions,
        )
        return userRepository.save(updated).toProfileResult()
    }

    @Transactional
    override fun deleteAccount(userId: Long) {
        val user = findActiveUser(userId)
        userRepository.save(user.copy(deletedAt = LocalDateTime.now()))
    }

    private fun findActiveUser(userId: Long): User {
        return userRepository.findById(userId)
            ?: throw AppException(ErrorCode.NOT_FOUND, "User not found")
    }

    private fun User.toProfileResult() = UserProfileResult(
        id = id,
        email = email,
        nickname = nickname,
        weightUnit = weightUnit,
        weeklyGoalSessions = weeklyGoalSessions,
        emailVerified = emailVerified,
        createdAt = createdAt,
    )
}
