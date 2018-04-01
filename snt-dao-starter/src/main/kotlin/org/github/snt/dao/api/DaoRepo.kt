/*
 * Copyright 2018 Kislitsyn Ilya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.github.snt.dao.api

import org.github.snt.dao.api.entity.AbstractEntity
import org.github.snt.dao.api.filter.Filter
import org.springframework.data.repository.CrudRepository

/**
 * DAO repository common operations.
 * @param <T> Item type.
 * @param <F> Item filter.
 */
interface DaoRepo<T : AbstractEntity, in F : Filter> : DaoProvider {
    fun getSpringDataRepo(): CrudRepository<T, Long>

    fun loadById(id: Long): T

    fun tryLoadOne(filter: F): T?

    fun loadOne(filter: F): T

    fun loadList(filter: F): List<T>

    fun save(item: T) : T

    fun remove(filter: F)

    fun fillFilterId(filter: F, mandatory: Boolean = true)

    fun getEntityName() :String
}