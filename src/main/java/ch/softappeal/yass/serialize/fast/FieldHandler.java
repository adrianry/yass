package ch.softappeal.yass.serialize.fast;

import ch.softappeal.yass.serialize.Reflector;
import ch.softappeal.yass.util.Check;
import ch.softappeal.yass.util.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

public final class FieldHandler {

  static final int END_OF_FIELDS = 0;
  public static final int FIRST_FIELD = END_OF_FIELDS + 1;

  public final Field field;
  public final int id;
  private final Reflector.Accessor accessor;

  public FieldHandler(final Field field, final int id, final Reflector.Accessor accessor) {
    this.field = Check.notNull(field);
    if (id <= END_OF_FIELDS) {
      // note: due to call to writeVarInt below
      throw new IllegalArgumentException("id " + id + " for field '" + field + "' must be > " + END_OF_FIELDS);
    }
    this.id = id;
    this.accessor = Check.notNull(accessor);
  }

  @SuppressWarnings("InstanceVariableMayNotBeInitialized") @Nullable private TypeHandler typeHandler;

  /**
   * Note: null if {@link ClassTypeHandler} or type not in class2typeHandler (Object, Throwable, abstract classes, ...).
   */
  public @Nullable TypeHandler typeHandler() {
    return typeHandler;
  }

  void fixup(final Map<Class<?>, TypeHandler> class2typeHandler) {
    typeHandler = class2typeHandler.get(
      primitiveWrapperType(field.getType()) // note: prevents that primitive types are written with type id
    );
    if (typeHandler instanceof ClassTypeHandler) {
      typeHandler = null;
    }
  }

  void readNoId(final Object object, final Input input) throws Exception {
    accessor.set(object, (typeHandler == null) ? input.readWithId() : typeHandler.readNoId(input));
  }

  void writeWithId(final Object object, final Output output) throws Exception {
    @Nullable final Object value = accessor.get(object);
    if (value != null) {
      output.writer.writeVarInt(id);
      if (typeHandler == null) {
        output.writeWithId(value);
      } else {
        typeHandler.writeNoId(value, output);
      }
    }
  }

  private static Class<?> primitiveWrapperType(final Class<?> type) {
    if (type == boolean.class) {
      return Boolean.class;
    } else if (type == byte.class) {
      return Byte.class;
    } else if (type == short.class) {
      return Short.class;
    } else if (type == int.class) {
      return Integer.class;
    } else if (type == long.class) {
      return Long.class;
    } else if (type == char.class) {
      return Character.class;
    } else if (type == float.class) {
      return Float.class;
    } else if (type == double.class) {
      return Double.class;
    }
    return type;
  }

}
