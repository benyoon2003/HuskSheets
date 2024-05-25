package org.example.service;

import org.example.model.AppUser;
import org.example.model.Spreadsheet;
import org.example.repository.SheetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SheetService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private SheetRepository sheetRepository;


  public Spreadsheet createSheet(AppUser user, String name) throws Exception {
    Spreadsheet newSheet = new Spreadsheet(name);

    List<Spreadsheet> userSheets = user.getPublished();

    for (Spreadsheet sheet : userSheets) {
      if (sheet.getName().equals(name)) {
        logger.error("Spreadhseet already exists: {}", name);
        throw new Exception("Sheet already exists!");
      }
    }
    user.addPublished(newSheet);
    sheetRepository.save(newSheet);

    return newSheet;
  }

  public boolean deleteSheet(AppUser user, Spreadsheet sheet) throws Exception {
    List<Spreadsheet> userSheets = new UserService().getSheets(user);

    boolean hasSheet = false;
    for (Spreadsheet s : userSheets) {
      if (sheet.getName().equals(s.getName())) {
        hasSheet = true;
      }
    }

    if (hasSheet) {
      sheetRepository.delete(sheet);
      return true;
    } else {
      return false;
    }
  }
}
