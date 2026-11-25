package mx.uttt.cafeuttt.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import mx.uttt.cafeuttt.ui.CafeUiState

/** Pantalla 1. Inicio de sesión. */
@Composable
fun LoginScreen(state: CafeUiState, onLogin: (String, String) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val fieldShape = RoundedCornerShape(14.dp)

    fun submit() {
        emailError = when {
            email.isBlank() -> "Ingresa tu correo institucional"
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "El correo no tiene un formato válido"
            else -> null
        }
        passwordError = if (password.isBlank()) "Ingresa tu contraseña" else null
        // Solo se envía el evento cuando los campos cumplen la validación local.
        if (emailError == null && passwordError == null) onLogin(email.trim(), password)
    }

    fun soon(text: String) {
        scope.launch { snackbar.showSnackbar(text) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Image(
                painter = painterResource(id = mx.uttt.cafeuttt.R.drawable.logo_ut),
                contentDescription = "Logotipo de la Universidad Tecnológica de Tula-Tepeji",
                modifier = Modifier.size(110.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "CafeUTTT",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Pide en la cafetería sin hacer fila",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            // Correo
            Text("Correo institucional", Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("matricula@uttt.edu.mx") },
                singleLine = true,
                shape = fieldShape,
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(8.dp))

            // Contraseña
            Text("Contraseña", Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••") },
                singleLine = true,
                shape = fieldShape,
                isError = passwordError != null || state.loginError != null,
                supportingText = {
                    when {
                        passwordError != null -> Text(passwordError!!)
                        state.loginError != null -> Text(state.loginError!!)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { submit() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !state.isSigningIn
            ) {
                Text(if (state.isSigningIn) "Validando…" else "Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            TextButton(onClick = { soon("La recuperación de contraseña estará disponible pronto") }) {
                Text("¿Olvidaste tu contraseña?")
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(Modifier.weight(1f))
                Text(
                    "¿Aún no tienes cuenta?",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(Modifier.weight(1f))
            }

            OutlinedButton(
                onClick = { soon("El registro estará disponible pronto") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Registrarme", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
