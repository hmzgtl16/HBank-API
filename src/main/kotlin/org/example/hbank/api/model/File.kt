package org.example.hbank.api.model

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.util.*

@Entity
@Table(name = "table_file")
data class File(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "file_id") val id: UUID? = null,
    @Column(name = "file_name", nullable = false) val name: String,
    @Column(name = "file_type", nullable = false) val type: String,
    @Lob @Column(name = "file_data", nullable = false) val data: ByteArray
) {


    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as File

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   name = $name   ,   type = $type   ,   data = $data )"
    }

}