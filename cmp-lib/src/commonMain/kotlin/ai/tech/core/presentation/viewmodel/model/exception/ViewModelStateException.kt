package ai.tech.core.presentation.viewmodel.model.exception

public class ViewModelStateException(message: String, cause: Throwable) : Throwable(message, cause) {
    public constructor(throwable: Throwable) : this(throwable.toString(), throwable)
}
