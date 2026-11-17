package com.paulohenrique.poketeam.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.paulohenrique.poketeam.util.safeCall
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun login(email: String, password: String): Result<Unit> = safeCall {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    suspend fun register(email: String, password: String): Result<Unit> = safeCall {
        auth.createUserWithEmailAndPassword(email, password).await()
        Unit
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = safeCall {
        auth.sendPasswordResetEmail(email).await()
        Unit
    }

    fun logout() = auth.signOut()
}

fun friendlyAuthError(error: Throwable): String = when (error) {
    is FirebaseAuthWeakPasswordException -> "Senha fraca. Use pelo menos 6 caracteres."
    is FirebaseAuthUserCollisionException -> "Este e-mail já está cadastrado."
    is FirebaseAuthInvalidCredentialsException,
    is FirebaseAuthInvalidUserException -> "E-mail ou senha inválidos."
    is FirebaseNetworkException -> "Sem conexão com a internet."
    else -> "Algo deu errado. Tente novamente."
}
