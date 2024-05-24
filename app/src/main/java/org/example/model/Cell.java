package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Cell {
  private String value;

  public Cell() {
    this.value = "";
  }

  public Cell(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<String> tokenize() {
    ArrayList<String> tokens = new ArrayList<>();
    String s = this.getValue();
    StringBuilder currentToken = new StringBuilder();

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (Character.isWhitespace(c)) {
        continue; // Skip whitespace
      }

      if (Character.isDigit(c) || c == '.') {
        currentToken.append(c);
        while (i + 1 < s.length() && (Character.isDigit(s.charAt(i + 1)) || s.charAt(i + 1) == '.')) {
          currentToken.append(s.charAt(++i));
        }
        tokens.add(currentToken.toString());
        currentToken.setLength(0);
      } else if (c == '$') {
        currentToken.append(c);
        while (i + 1 < s.length() && Character.isDigit(s.charAt(i + 1))) {
          currentToken.append(s.charAt(++i));
        }
        tokens.add(currentToken.toString());
        currentToken.setLength(0);
      } else if (c == '<' || c == '>' || c == '=' || c == '!' || c == '&' || c == '|') {
        currentToken.append(c);
        if (i + 1 < s.length() && s.charAt(i + 1) == '=') {
          currentToken.append(s.charAt(++i));
        } else if (i + 1 < s.length() && c == '<' && s.charAt(i + 1) == '>') {
          currentToken.append(s.charAt(++i));
        }
        tokens.add(currentToken.toString());
        currentToken.setLength(0);
      } else {
        tokens.add(String.valueOf(c));
      }
    }

    return tokens;
  }

  public void parseValue() {
    List<String> tokens = tokenize();
    for (int i = 0; i < tokens.size(); i++) {
      String c = tokens.get(i);

      if (i != 0 && i != tokens.size() - 1) {
        Double leftArg;
        Double rightArg;
        switch (c) {
          case "+":
          case "-":
          case "*":
          case "/":
          case "<":
          case ">":
          case "=":
          case "<>":
          case "&":
          case "|":
          case ":":
            try {
              leftArg = Double.parseDouble(tokens.get(i - 1));
              rightArg = Double.parseDouble(tokens.get(i + 1));
              String valAtToken = "";
              switch (c) {
                case "+":
                  valAtToken = String.valueOf(leftArg + rightArg);
                  break;
                case "-":
                  valAtToken = String.valueOf(leftArg - rightArg);
                  break;
                case "*":
                  valAtToken = String.valueOf(leftArg * rightArg);
                  break;
                case "/":
                  valAtToken = String.valueOf(leftArg / rightArg);
                  break;
                case "<":
                  valAtToken = String.valueOf(leftArg < rightArg ? 1 : 0);
                  break;
                case ">":
                  valAtToken = String.valueOf(leftArg > rightArg ? 1 : 0);
                  break;
                case "=":
                  valAtToken = String.valueOf(leftArg.equals(rightArg) ? 1 : 0);
                  break;
                case "<>":
                  valAtToken = String.valueOf(!leftArg.equals(rightArg) ? 1 : 0);
                  break;
                case "&":
                  valAtToken = String.valueOf((leftArg != 0 && rightArg != 0) ? 1 : 0);
                  break;
                case "|":
                  valAtToken = String.valueOf((leftArg != 0 || rightArg != 0) ? 1 : 0);
                  break;
                case ":":
                  valAtToken = String.valueOf(rightArg - leftArg);
                  break;
              }
              tokens.set(i + 1, valAtToken);
              tokens.remove(i - 1);
              tokens.remove(i);
              i--;
            } catch (Exception e) {
              System.out.println("One or more of the arguments is not a number.");
            }
            break;
        }
      }
    }

    String newVal = tokens.size() > 0 ? tokens.get(0) : "";
    this.setValue(newVal);
  }

  public boolean isFormula() {
    return this.value.startsWith("=");
  }
}
