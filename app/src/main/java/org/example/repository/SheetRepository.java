package org.example.repository;

import org.example.model.AppUser;
import org.example.model.Spreadsheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetRepository extends JpaRepository<Spreadsheet, String> {

}
