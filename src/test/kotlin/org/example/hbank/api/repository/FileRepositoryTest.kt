package org.example.hbank.api.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.hbank.api.config.TestContainersConfig
import org.example.hbank.api.model.File
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TestContainersConfig::class)
@Sql("/database/schema.sql")
class FileRepositoryTest {

    @Autowired
    private lateinit var fileRepository: FileRepository

    @Test
    fun `should find file by id`() {
       val foundFile = fileRepository.findFileById(UUID.fromString("1b3d1d84-a234-4418-9cb5-82e9d721976c"))

        assertThat(foundFile).isNotNull
        assertThat(foundFile!!).matches {
            it.id == UUID.fromString("1b3d1d84-a234-4418-9cb5-82e9d721976c") &&
                    it.name == "profile_pic.jpeg" &&
                    it.type == "image/jpeg"
        }
    }
}

