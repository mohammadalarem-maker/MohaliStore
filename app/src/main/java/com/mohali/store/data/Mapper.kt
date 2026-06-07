package com.mohali.store.data

import com.mohali.store.domain.User

fun UserDto.toDomain(): User {
    return User(
        uid = uid,
        username = username,
        email = email,
        role = role,
        isActive = isActive,
        createdAt = createdAt
    )
}
