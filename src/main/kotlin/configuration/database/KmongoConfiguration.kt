package configuration.database

import model.StudentPerformance
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun mongoConnection() = KMongo.createClient("mongodb://localhost:27017")
    .coroutine
    .getDatabase("students-performance-api")
    .getCollection<StudentPerformance>("students-performance")