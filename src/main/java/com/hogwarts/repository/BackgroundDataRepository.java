package com.hogwarts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hogwarts.model.BackgroundData;
import com.hogwarts.model.BackgroundDataId;

public interface BackgroundDataRepository extends JpaRepository<BackgroundData, BackgroundDataId> {

	@Query("SELECT b.backgroundDataId.section, COUNT(b) FROM BackgroundData b GROUP BY b.backgroundDataId.section ORDER BY b.backgroundDataId.section")
	public List<Object[]> countBySection();
}
