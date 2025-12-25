package ninja.jetstream

import kotlinx.serialization.Serializable

enum class SubmissionStatus {
    NOTHING,
    COMPILER_ERROR,
    EVALUATED,
}

enum class TestStatus {
    UNEVALUATED,
    ERROR,
    WRONG_ANSWER,
    ACCEPTED,
}