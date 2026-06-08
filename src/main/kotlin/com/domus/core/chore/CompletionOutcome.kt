package com.domus.core.chore

sealed interface CompletionOutcome {
    data object Finished : CompletionOutcome
    data class Continued(val chore: Chore) : CompletionOutcome
}
