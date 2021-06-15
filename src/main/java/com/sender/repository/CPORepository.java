package com.sender.repository;

import com.sender.dao.CPODAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CPORepository extends JpaRepository<CPODAO, Long> {
    Optional<CPODAO> findCPODAOByName(String name);

    @Query("select distinct cpo.id from CPODAO cpo where cpo.name in ?1")
    List<Long> getCpoIds(String[] names);

    @Query("select distinct cpo.name from CPODAO cpo where not cpo.name='Нет' and not cpo.name='' order by cpo.name asc")
    List<String> getAllCpoNames();
}
