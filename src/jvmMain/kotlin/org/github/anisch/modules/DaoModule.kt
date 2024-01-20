package org.github.anisch.modules

import org.github.anisch.repos.DefaultPersonRepository
import org.github.anisch.repos.PersonRepository
import org.github.anisch.serial.Person
import org.koin.dsl.module

val daoModule = module {
    single<PersonRepository> { DefaultPersonRepository() }
}
