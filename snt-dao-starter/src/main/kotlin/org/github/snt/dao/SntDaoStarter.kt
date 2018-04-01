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

package org.github.snt.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.github.snt.dao.config.SntDaoConfig
import org.github.snt.lib.config.SntCoreConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource

@Configuration
@ComponentScan
@EnableJpaRepositories(basePackages = ["org.github.snt.dao.api.repo.spring"])
@EntityScan(basePackages = ["org.github.snt.dao.api.entity"])
@EnableConfigurationProperties(value = [SntCoreConfig::class, SntDaoConfig::class])
class SntDaoStarter {
    @Autowired
    lateinit var daoConfig: SntDaoConfig

    @Bean
    @Primary
    fun getDatasource(): DataSource {
        val dsConfig = daoConfig.ds
        val hikariConfig = HikariConfig()
        hikariConfig.driverClassName = dsConfig.driverClass
        hikariConfig.jdbcUrl = dsConfig.url
        hikariConfig.username = dsConfig.user
        hikariConfig.password = dsConfig.password

        hikariConfig.dataSourceProperties = mapOf(
                "cachePrepStmts" to dsConfig.cachePrepStmts.toString(),
                "prepStmtCacheSize" to dsConfig.prepStmtCacheSize.toString(),
                "prepStmtCacheSqlLimit" to dsConfig.prepStmtCacheSqlLimit.toString()
        ).toProperties()

        return HikariDataSource(hikariConfig)
    }
//
//    @Bean
//    fun getLiquidbase(): SpringLiquibase {
//        val liquibase = SpringLiquibase()
//        liquibase.dataSource = getDatasource()
//        liquibase.changeLog = "classpath:db/changelog/db.changelog-master.yaml"
//        liquibase.afterPropertiesSet()
//        return liquibase
//    }
}
