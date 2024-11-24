package ai.tech.core.misc.plugin.applicationmonitoring

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.applicationmonitoring.model.config.ApplicationMonitoringConfig
import io.ktor.events.EventDefinition
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*

private val ApplicationMonitoringPlugin = createApplicationPlugin(name = "ApplicationMonitoringPlugin") {
    on(MonitoringEvent(ApplicationStarted)) { application ->
        application.log.info("Server is started")
    }
    on(MonitoringEvent(ApplicationStopped)) { application ->
        application.log.info("Server is stopped")
        // Release resources and unsubscribe from events
        application.monitor.unsubscribe(ApplicationStarted) {}
        application.monitor.unsubscribe(ApplicationStopped) {}
    }
    on(ResponseSent) { call ->
        if (call.response.status() == HttpStatusCode.NotFound) {
            this@createApplicationPlugin.application.monitor.raise(NotFoundEvent, call)
        }
    }
}

private val NotFoundEvent: EventDefinition<ApplicationCall> = EventDefinition()

public fun Application.configureApplicationMonitoring(config: ApplicationMonitoringConfig?) = config?.takeIf(EnabledConfig::enabled)?.let {
    install(ApplicationMonitoringPlugin)
}
