package com.dmm.task.data.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dmm.task.data.entity.Tasks;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, Integer> ,JpaSpecificationExecutor<Tasks> {

	
	@Query("select a from Tasks a where a.date between :from and :to and name = :name")
	List<Tasks> findByDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("name") String name);

	@Query("select a from Tasks a where a.date between :from and :to")
	List<Tasks> findAllByDateBetween(@Param("from") LocalDate start, @Param("to") LocalDate day);

}