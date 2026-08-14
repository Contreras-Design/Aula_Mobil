package com.itespf.aulamovil.data.model

// ---------- AUTH ----------

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val name: String,
    val username: String
)

data class ApiError(
    val error: String
)

data class GradesResponse(
    val assignments: List<Assignment> = emptyList(),
    val submissions: List<Submission> = emptyList(),
    val exams: List<Exam> = emptyList(),
    val examResults: List<ExamResult> = emptyList(),
    val attendances: List<Attendance> = emptyList(),
    val lessons: List<Lesson> = emptyList(),
    val customGrades: List<CustomGrade> = emptyList()
)

data class Assignment(
    val id: String,
    val title: String,
    val description: String?,
    val dueDate: String?,
    val type: String,
    val weight: Double?
)

data class Submission(
    val assignmentId: String,
    val grade: Double?,
    val feedback: String?,
    val assignment: AssignmentRef?
)

data class AssignmentRef(
    val id: String,
    val title: String,
    val type: String
)

data class Exam(
    val id: String,
    val title: String,
    val minutes: Int,
    val maxViolations: Int
)

data class ExamResult(
    val id: String,
    val examId: String,
    val score: Double?,
    val correctCount: Int?,
    val total: Int?,
    val violations: Int?,
    val autoSubmitted: Boolean?,
    val createdAt: String?,
    val exam: ExamRef?
)

data class ExamRef(
    val id: String,
    val title: String
)

data class Attendance(
    val id: String,
    val lessonId: String,
    val status: String, // PRESENT · ABSENT · LATE · EXCUSED
    val excuseReason: String?,
    val excuseStatus: String?,
    val lesson: LessonRef?
)

data class LessonRef(
    val id: String,
    val number: Int,
    val title: String,
    val unit: String?
)

data class Lesson(
    val id: String,
    val number: Int,
    val title: String
)

data class CustomGrade(
    val id: String,
    val title: String,
    val score: Double
)
