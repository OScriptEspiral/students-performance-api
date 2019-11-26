package repository

import arrow.data.Reader
import arrow.data.ReaderApi.ask
import arrow.data.map
import com.mongodb.client.model.Filters
import configuration.server.RepositoryContext
import kotlinx.coroutines.runBlocking
import model.StudentPerformance

fun save(studentPerformance: StudentPerformance): Reader<RepositoryContext, StudentPerformance> =
    ask<RepositoryContext>().map { ctx ->
        runBlocking {
            studentPerformance.also {
                ctx.coroutineCollection.insertOne(studentPerformance)
            }
        }
    }

fun getById(id: String) =
    ask<RepositoryContext>().map { ctx ->
        runBlocking {
            ctx.coroutineCollection.findOneById(id)
        }
    }

fun getByStudent(studentId: String) =
    ask<RepositoryContext>().map { ctx ->
        runBlocking {
            ctx.coroutineCollection.find(Filters.eq("studentId", studentId))
        }
    }
