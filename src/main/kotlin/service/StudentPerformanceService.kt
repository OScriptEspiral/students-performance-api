package service

import arrow.core.fix
import configuration.server.RepositoryContext
import exceptions.EntityNotFoundException
import io.reactivex.Flowable
import model.StudentPerformance

fun save(studentPerformance: StudentPerformance) =
    repository.save(studentPerformance).run(RepositoryContext).fix().extract()

fun getById(id: String) = repository.getById(id).run(RepositoryContext).fix().extract() ?: throw EntityNotFoundException()

fun getByStudent(studentId: String) =
    Flowable.fromPublisher(repository.getByStudent(studentId).run(RepositoryContext).fix().extract().publisher)
