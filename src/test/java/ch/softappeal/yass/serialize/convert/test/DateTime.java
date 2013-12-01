package ch.softappeal.yass.serialize.convert.test;

import ch.softappeal.yass.serialize.convert.StringValue;

public final class DateTime extends StringValue {

  private static final long serialVersionUID = 1L;

  public DateTime(final String value) {
    super(value);
  }

  public static final TypeConverter TYPE_CONVERTER = new TypeConverter(DateTime.class) {
    @Override public Object fromString(final String value) {
      return new DateTime(value);
    }
  };

}
