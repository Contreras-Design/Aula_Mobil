package com.itespf.aulamovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itespf.aulamovil.ViewModelFactory
import com.itespf.aulamovil.data.model.GradesResponse
import com.itespf.aulamovil.ui.state.UiState
import com.itespf.aulamovil.ui.viewmodel.GradesViewModel
import kotlin.math.round

@Composable
fun GradesScreen(
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
            "Mi boleta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(24.dp, 24.dp, 24.dp, 8.dp)
        )

        when (val current = state) {
            is UiState.Loading, UiState.Idle -> LoadingBlock()
            is UiState.Error -> if (!current.expiredSession) ErrorBlock(current.message) { viewModel.load() }
            is UiState.Success -> GradesContent(current.data, viewModel.calcularPromedio(current.data))
        }
    }
}

@Composable
private fun GradesContent(data: GradesResponse, promedio: Double?) {
    LazyColumn(
        contentPadding = PaddingValues(24.dp, 0.dp, 24.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SummaryCard(promedio, data) }

        item { SectionTitle("Tareas y proyectos") }
        if (data.assignments.isEmpty()) {
            item { EmptyRow("No hay tareas registradas todavía.") }
        }
        items(data.assignments) { assignment ->
            val submission = data.submissions.firstOrNull { it.assignmentId == assignment.id }
            AssignmentRowCard(assignment.title, assignment.type, submission?.grade, submission?.feedback)
        }

        item { SectionTitle("Exámenes") }
        if (data.examResults.isEmpty()) {
            item { EmptyRow("No hay resultados de exámenes todavía.") }
        }
        items(data.examResults) { result ->
            ExamRowCard(
                title = result.exam?.title ?: "Examen",
                score = result.score,
                correct = result.correctCount,
                total = result.total
            )
        }

        if (data.customGrades.isNotEmpty()) {
            item { SectionTitle("Otras calificaciones") }
            items(data.customGrades) { custom ->
                SimpleGradeRow(custom.title, custom.score)
            }
        }
    }
}

@Composable
private fun SummaryCard(promedio: Double?, data: GradesResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Resumen general", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatBlock("Promedio", promedio?.let { redondear(it).toString() } ?: "—")
                StatBlock("Tareas", data.assignments.size.toString())
                StatBlock("Exámenes", data.examResults.size.toString())
            }
        }
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun AssignmentRowCard(title: String, type: String, grade: Double?, feedback: String?) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(type, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                if (!feedback.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "\"$feedback\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            Text(
                grade?.let { redondear(it).toString() } ?: "Pendiente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (grade != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun ExamRowCard(title: String, score: Double?, correct: Int?, total: Int?) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                if (correct != null && total != null) {
                    Text(
                        "$correct de $total correctas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Text(
                score?.let { redondear(it).toString() } ?: "—",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SimpleGradeRow(title: String, score: Double) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(redondear(score).toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun redondear(v: Double): Double = round(v * 100) / 100
