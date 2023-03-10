package org.github.anisch.modules

import org.github.anisch.dao.DefaultPersonDao
import org.github.anisch.dao.PersonDao
import org.koin.dsl.module

val daoModule = module {
    single<PersonDao> { DefaultPersonDao() }
}
