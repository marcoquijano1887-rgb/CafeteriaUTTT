package mx.uttt.cafeuttt.domain.model

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Sesión en memoria: permite que las pantallas consulten datos del usuario autenticado. */
@Singleton
class UserSession @Inject constructor() {
    private val mutableUser = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = mutableUser.asStateFlow()
    fun setUser(value: User?) { mutableUser.value = value }
}
