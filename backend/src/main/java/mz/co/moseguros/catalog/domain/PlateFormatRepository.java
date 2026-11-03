package mz.co.moseguros.catalog.domain;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/** Formatos de matrícula (sem RLS). */
public interface PlateFormatRepository extends JpaRepository<PlateFormatEntity, UUID> {

    /** Todos, pela ordem de teste (empate: código). */
    List<PlateFormatEntity> findAllByOrderBySortOrderAscCodeAsc();

    /** Os activos, pela ordem de teste (empate: código). */
    List<PlateFormatEntity> findByActiveTrueOrderBySortOrderAscCodeAsc();

    boolean existsByCode(String code);

    /**
     * Os activos, bloqueados até ao fim da transacção: duas desactivações simultâneas não podem
     * deixar a plataforma sem nenhum formato activo.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from PlateFormatEntity f where f.active = true")
    List<PlateFormatEntity> lockActive();
}
