package org.example.hbank.api.repository

import org.example.hbank.api.model.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach

@ExtendWith(SpringExtension::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:postgresql://localhost:5432/hbank_db",
    "spring.datasource.username=hbank_root",
    "spring.datasource.password=hbank_pass",
    "spring.datasource.driver-class-name=org.postgresql.Driver"
])
class FileRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var fileRepository: FileRepository

    @Test
    fun `should find file by id`() {
        // Arrange
        val file = createFile("test-file.txt", "text/plain")
        entityManager.persist(file)
        entityManager.flush()

        // Act
        val found = fileRepository.findFileById(file.id!!)

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(file.id)
        assertThat(found?.name).isEqualTo("test-file.txt")
        assertThat(found?.type).isEqualTo("text/plain")
    }

    @Test
    fun `should return null when file id does not exist`() {
        // Act
        val found = fileRepository.findFileById(UUID.randomUUID())

        // Assert
        assertThat(found).isNull()
    }

    private fun createFile(name: String, type: String): File {
        return File().apply {
            this.name = name
            this.type = type
            this.data = "Test file content".toByteArray()
        }
    }
}