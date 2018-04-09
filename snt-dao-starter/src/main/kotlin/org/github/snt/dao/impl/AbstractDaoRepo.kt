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

package org.github.snt.dao.impl

import org.github.snt.dao.api.Dao
import org.github.snt.dao.api.DaoRepo
import org.github.snt.dao.api.entity.AbstractEntity
import org.github.snt.dao.api.filter.Filter
import org.github.snt.lib.err.SntResult.ITEM_NOT_FOUND
import org.github.snt.lib.err.SntException
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractDaoRepo<T : AbstractEntity, in F : Filter> : DaoRepo<T, F> {

    @Autowired
    lateinit var internalDao: Dao

    override fun loadById(id: Long): T {
        return getSpringDataRepo().findOne(id)
    }

    override fun loadOne(filter: F): T {
        return tryLoadOne(filter)
                ?: throw SntException(ITEM_NOT_FOUND, "Can't find ${getEntityName()} by filter $filter")
    }

    override fun tryLoadOne(filter: F): T? {
        val items = loadList(filter)
        if (items.size > 1) {
            throw SntException(ITEM_NOT_FOUND, "Too many ${getEntityName()} items returned for loadOne operation with filter $filter")
        }
        return if (items.isEmpty()) null else items[0]
    }

    override fun save(item: T): T {
        return getSpringDataRepo().save(item)
    }

    override fun remove(filter: F) {
        fillFilterId(filter, mandatory = false)
        if (filter.id != null) {
            getSpringDataRepo().delete(filter.id)
        }
    }

    override fun fillFilterId(filter: F, mandatory: Boolean) {
        if (filter.id != null) {
            return
        }

        val item = if (mandatory) loadOne(filter) else tryLoadOne(filter)
        if (item != null) {
            filter.id = loadOne(filter).id
        }
    }

    override fun getDao(): Dao {
        return internalDao
    }

}
