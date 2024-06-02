package org.example.repository;

import org.example.model.Sheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SheetRepository extends JpaRepository<Sheet, String> {
    List<Sheet> findAllByPublisher(String publisher); // Add this method
}

