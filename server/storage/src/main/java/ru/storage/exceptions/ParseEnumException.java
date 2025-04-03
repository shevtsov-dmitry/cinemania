package ru.storage.exceptions;

import java.util.Arrays;
import java.util.StringJoiner;

public class ParseEnumException extends IllegalArgumentException {
  private final Class<? extends Enum<?>> enumClass;

  public ParseEnumException(Class<? extends Enum<?>> enumClass) {
    this.enumClass = enumClass;
  }

  public static Enum<?>[] getSupportedValues(Class<? extends Enum<?>> enumClass) {
    return enumClass.getEnumConstants();
  }

  /**
   * Retrieve predefined enum values as a string.
   * 
   * @param enumClass The class of the enum to retrieve supported values for.
   * @return A string containing the supported values of the enum.
   */
  public static String getSupportedValuesAsString(Class<? extends Enum<?>> enumClass) {
    var sj = new StringJoiner(", ", "[", "]");
    Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).forEach(sj::add);
    return sj.toString();
  }

  @Override
  public String getMessage() {
    if (enumClass == null) {
      return "Используется неверный тип в перечислении(enum).";
    } else {
      return "Используется неверный тип в перечислении(enum). Список поддерживаемых: %s".formatted(getSupportedValuesAsString(enumClass));
    }
  }

}
