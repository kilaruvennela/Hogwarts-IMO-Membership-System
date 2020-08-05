package com.hogwarts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hogwarts.model.BackgroundData;
import com.hogwarts.model.BackgroundDataId;

public interface BackgroundDataRepository extends JpaRepository<BackgroundData, BackgroundDataId> {

}
