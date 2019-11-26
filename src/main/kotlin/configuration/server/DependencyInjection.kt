package configuration.server

import configuration.database.mongoConnection
import model.StudentPerformance
import org.litote.kmongo.coroutine.CoroutineCollection

object RepositoryContext {
    val coroutineCollection: CoroutineCollection<StudentPerformance> = mongoConnection()
}