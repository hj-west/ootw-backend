package com.responseor.ootw.repository;

import com.responseor.ootw.entity.ClothesByTemp;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClothesByTempRepository extends JpaRepository<ClothesByTemp, Integer> {
    Optional<ClothesByTemp> findById(Long id);
    List<ClothesByTemp> findAllByUuid(Long uuid);
    @Query(value = "select c from ClothesByTemp c where c.uuid = :uuid and :temp between c.startTemp and c.endTemp")
    List<ClothesByTemp> findByUuidAndStartTempLessThanEqualOrEndTempGreaterThanEqual(@Param("uuid") Long uuid, @Param("temp") Integer temp);
    @Query(value = "select c from ClothesByTemp c where c.uuid is null and :temp between c.startTemp and c.endTemp")
    List<ClothesByTemp> findByUuidIsNullAndStartTempLessThanEqualOrEndTempGreaterThanEqual(@Param("temp") Integer temp);

}
