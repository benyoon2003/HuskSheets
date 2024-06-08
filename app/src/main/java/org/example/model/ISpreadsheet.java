package org.example.model;

import java.util.List;

/**
 * Represents the interface for a spreadsheet.
 */
public interface ISpreadsheet extends IReadOnlySpreadSheet {

  /**
   * Gets a 2D ArrayList of Cell objects representing the cells in the
   * spreadsheet.
   *
   * @return a 2D ArrayList of Cell objects representing the cells in the
   *         spreadsheet.
   */
  List<List<Cell>> getCells();

  /**
   * Evaluates the given formula and returns the result.
   *
   * @param formula the formula to evaluate.
   * @return the result of evaluating the formula.
   */
  String evaluateFormula(String formula);

  /**
   * Sets the value of the cell at the specified row and column.
   *
   * @param row   the row index of the cell.
   * @param col   the column index of the cell.
   * @param value the value to set.
   */
  void setCellValue(int row, int col, String value);

  /**
   * Gets the value of the cell at the specified row and column.
   *
   * @param row the row index of the cell.
   * @param col the column index of the cell.
   * @return the value of the cell.
   */
  String getCellValue(int row, int col);

  /**
   * Gets the formula of the cell at the specified row and column.
   *
   * @param row the row index of the cell.
   * @param col the column index of the cell.
   * @return the formula of the cell.
   */
  String getCellFormula(int row, int col);

  /**
   * Sets the raw data of the cell at the specified row and column.
   *
   * @param selRow the row index of the cell.
   * @param selCol the column index of the cell.
   * @param val    the raw data to set.
   */
  void setCellRawdata(int selRow, int selCol, String val);

  /**
   * Gets the raw data of the cell at the specified row and column.
   *
   * @param row the row index of the cell.
   * @param col the column index of the cell.
   * @return the raw data of the cell.
   */
  String getCellRawdata(int row, int col);

  /**
   * Adds a published version of the spreadsheet.
   *
   * @param sheet the published spreadsheet to add.
   */
  public void addPublished(ISpreadsheet sheet);

  /**
   * Adds a subscribed version of the spreadsheet.
   *
   * @param sheet the subscribed spreadsheet to add.
   */
  public void addSubscribed(ISpreadsheet sheet);

  /**
   * Gets the list of published versions of the spreadsheet.
   *
   * @return a list of published versions of the spreadsheet.
   */
  public List<ISpreadsheet> getPublishedVersions();

  /**
   * Gets the list of subscribed modified versions of the spreadsheet
   * 
   * @return a list of subscribed modified versions of the spreadsheet
   */
  public List<ISpreadsheet> getSubscribedVersions();

  void setGrid(List<List<Cell>> updatedGrid);
}
