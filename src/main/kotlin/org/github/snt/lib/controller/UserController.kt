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

package org.github.snt.lib.controller

import org.github.snt.api.User
import org.github.snt.api.dao.repo.crud.UserCrudRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * User controller for SNT tests.
 */
@Controller
@RequestMapping("/user")
class UserController {
    @Autowired
    lateinit var userRepo: UserCrudRepo

    @RequestMapping("/create")
    @ResponseBody
    fun create(code: String, description: String?): String {
        return try {
            userRepo.save(User(code, description))
            "OK"
        } catch (e: Exception) {
            "Failed: ${e.message}"
        }

    }

    @RequestMapping("/list", produces = ["application/json"])
    @ResponseBody
    fun list(code: String?): List<User> {
        return if (code != null) {
            val result = userRepo.findByCode(code)
            if (result == null) emptyList() else listOf(result)
        } else {
            userRepo.findAll().toList()
        }
    }
}