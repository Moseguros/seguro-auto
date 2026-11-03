package mz.co.moseguros.vehicle.domain;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Condutores de cada veículo. Corre no âmbito de quem chama (RLS pelo dono do veículo). */
public interface VehicleDriverRepository extends JpaRepository<VehicleDriver, UUID> {

    List<VehicleDriver> findByVehicleIdIn(Collection<UUID> vehicleIds);

    /** Ligações dos condutores indicados a veículos com o estado indicado. */
    @Query("""
            select vd from VehicleDriver vd, Vehicle v
             where v.id = vd.vehicleId and vd.driverId in :driverIds and v.status = :vehicleStatus""")
    List<VehicleDriver> findByDriverIdsAndVehicleStatus(@Param("driverIds") Collection<UUID> driverIds,
                                                        @Param("vehicleStatus") RecordStatus vehicleStatus);

    /** Apaga já (sem esperar pelo flush) a lista de condutores do veículo. */
    @Modifying(flushAutomatically = true)
    @Query("delete from VehicleDriver vd where vd.vehicleId = :vehicleId")
    int deleteByVehicle(@Param("vehicleId") UUID vehicleId);

    /** Apaga as ligações do condutor a veículos com o estado indicado. */
    @Modifying(flushAutomatically = true)
    @Query("""
            delete from VehicleDriver vd
             where vd.driverId = :driverId
               and vd.vehicleId in (select v.id from Vehicle v where v.status = :vehicleStatus)""")
    int deleteByDriverAndVehicleStatus(@Param("driverId") UUID driverId,
                                       @Param("vehicleStatus") RecordStatus vehicleStatus);
}
