package mx.uttt.cafeuttt.data.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Hash con sal (PBKDF2-HMAC-SHA256). Formato guardado: iteraciones:sal:hash (Base64). */
object PasswordHasher {
    private const val ITER = 120_000
    private fun derive(pw: CharArray, salt: ByteArray, iter: Int): ByteArray =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(PBEKeySpec(pw, salt, iter, 256)).encoded

    fun hash(password: String): String {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val h = derive(password.toCharArray(), salt, ITER)
        return "$ITER:${Base64.encodeToString(salt, Base64.NO_WRAP)}:${Base64.encodeToString(h, Base64.NO_WRAP)}"
    }

    fun verify(password: String, stored: String): Boolean {
        val p = stored.split(":"); if (p.size != 3) return false
        val h = derive(password.toCharArray(), Base64.decode(p[1], Base64.NO_WRAP), p[0].toInt())
        return java.security.MessageDigest.isEqual(h, Base64.decode(p[2], Base64.NO_WRAP))
    }
}
