package routing

import exceptions.InsufficientParametersException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.collect
import model.StudentPerformance
import org.litote.kmongo.json
import service.getByStudent
import service.getById
import service.save

@FlowPreview
@ExperimentalCoroutinesApi
fun Application.studentsPerformanceRoutes() {
    val channel = produce<StudentPerformance> {
        while (true) {} //polling to keep channel open
    }.broadcast()

    routing {
        get("/{id}") {
            call.respond(HttpStatusCode.OK, getById(call.parameters["id"]!!))
        }

        get {
            call.respondTextWriter(ContentType.Text.Plain) {
                (call.request.queryParameters["studentId"] ?: throw InsufficientParametersException()).let { studentId ->
                    getByStudent(studentId).collect { studentPerformance ->
                        this.write("data: ${studentPerformance.json}\n")
                        this.flush()
                    }
                }
            }
        }

        get("/subscribe") {
            val events = channel.openSubscription()
            val studentId = call.request.queryParameters["studentId"] ?: throw InsufficientParametersException()
            try {
                call.respondTextWriter(contentType = ContentType.Text.EventStream) {
                    events.consumeAsFlow().filter {
                        it.studentId == studentId
                    }.collect { performance ->
                        write("data: ${performance.json}\n")
                        flush()
                    }
                }
            } finally {
                events.cancel()
            }
        }

        post {
            call.response.let { response ->
                save(call.receive()).let { performance ->
                    response.headers.append("Location", "/${performance._id}", true)
                    channel.send(performance)
                }
                response.status(HttpStatusCode.Created)
            }
        }
    }
}