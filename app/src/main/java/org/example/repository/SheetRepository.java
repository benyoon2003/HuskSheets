package org.example.repository;

import org.example.model.Sheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetRepository extends JpaRepository<Sheet, String> {
}
