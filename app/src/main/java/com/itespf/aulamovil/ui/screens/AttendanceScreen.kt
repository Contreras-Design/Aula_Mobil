package com.itespf.aulamovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itespf.aulamovil.ViewModelFactory
import com.itespf.aulamovil.data.model.Attendance
import com.itespf.aulamovil.ui.state.UiState
import com.itespf.aulamovil.ui.theme.AccentAmber
import com.itespf.aulamovil.ui.theme.AccentGreen
import com.itespf.aulamovil.ui.theme.AccentRed
import com.itespf.aulamovil.ui.viewmodel.GradesViewModel

@Composable
fun AttendanceScreen(
    factory: ViewModelFactory,
    onSessionExpired: () -> Unit
) {
    val viewModel: GradesViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    LaunchedEffect(state) {
        val current = state
        if (current is UiState.Error && current.expiredSession) {
            onSessionExpired()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Mi asistencia",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(24.dp, 24.dp, 24.dp, 8.dp)
        )

        when (val current = state) {
            is UiState.Loading, UiState.Idle -> LoadingBlock()
            is UiState.Error -> if (!current.expiredSession) ErrorBlock(current.message) { viewModel.load() }
            is UiState.Success -> {
                val attendances = current.data.attendances
                if (attendances.isEmpty()) {
                    EmptyRow("No hay registros de asistencia todavía.")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(24.dp, 0.dp, 24.dp, 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(attendances) { attendance -> AttendanceRow(attendance) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceRow(attendance: Attendance) {
    val (label, color) = statusStyle(attendance.status)
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    attendance.lesson?.title ?: "Clase #${attendance.lesson?.number ?: attendance.lessonId}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (!attendance.excuseReason.isNullOrBlank()) {
                    Text(
                        "Justificación: ${attendance.excuseReason}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Surface(
                color = color.copy(alpha = 0.15f),
                contentColor = color,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

private fun statusStyle(status: String): Pair<String, Color> = when (status) {
    "PRESENT" -> "Presente" to AccentGreen
    "ABSENT" -> "Ausente" to AccentRed
    "LATE" -> "Tardanza" to AccentAmber
    "EXCUSED" -> "Justificado" to AccentAmber
    else -> status to Color.Gray
}
