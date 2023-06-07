package com.cchunduri.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordUtils {

    fun checkPassword(input: String, saved: String): Boolean {
        return BCrypt.checkpw(input, saved)
    }

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}