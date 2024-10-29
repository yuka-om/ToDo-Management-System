package com.dmm.task.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dmm.task.data.entity.Tasks;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, Integer> {

}