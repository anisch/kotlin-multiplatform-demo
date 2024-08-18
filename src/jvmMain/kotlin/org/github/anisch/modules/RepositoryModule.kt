package org.github.anisch.modules

import org.github.anisch.repos.DefaultPersonRepository
import org.github.anisch.repos.PersonRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<PersonRepository> { DefaultPersonRepository() }
}
