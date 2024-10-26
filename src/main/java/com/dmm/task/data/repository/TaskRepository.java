package com.dmm.task.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmm.task.data.entity.Tasks;


public interface TaskRepository extends JpaRepository<Tasks, Integer> {

}