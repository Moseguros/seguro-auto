package mz.co.moseguros.catalog.domain;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Produtos visíveis no âmbito da transacção (Row Level Security por {@code insurer_id}). */
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    boolean existsByInsurerIdAndCode(UUID insurerId, String code);

    List<Product> findByIdIn(Collection<UUID> ids);
}
