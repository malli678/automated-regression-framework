package com.regression.framework.api.repository;

import com.regression.framework.api.entity.TestRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRunRepository extends JpaRepository<TestRunEntity, String> {
    List<TestRunEntity> findByStatusOrderByStartTimeDesc(String status);
    List<TestRunEntity> findAllByOrderByStartTimeDesc();
}