package ai.tech.core.presentation.component.textfield.model

public sealed class TextField {
    public data object Text : TextField()
    public data object LocalTime : TextField()
    public data object LocalDate : TextField()
    public data object LocalDateTime : TextField()
    public data class Enum(val values: List<String>) : TextField()
}
