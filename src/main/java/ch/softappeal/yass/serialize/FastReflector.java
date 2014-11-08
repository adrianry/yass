package ch.softappeal.yass.serialize;

import ch.softappeal.yass.util.Check;
import ch.softappeal.yass.util.Exceptions;
import ch.softappeal.yass.util.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Even works if classes don't have a default constructor; uses {@link Unsafe}.
 */
public final class FastReflector implements Reflector {

    private static Unsafe getUnsafe() {
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe)field.get(null);
        } catch (final Exception e) {
            throw Exceptions.wrap(e);
        }
    }

    private static final Unsafe UNSAFE = getUnsafe();

    public static final Factory FACTORY = FastReflector::new;

    private FastReflector(final Class<?> type) {
        this.type = Check.notNull(type);
    }

    private final Class<?> type;

    @Override public Object newInstance() throws InstantiationException {
        return UNSAFE.allocateInstance(type);
    }

    @Override public Accessor accessor(final Field field) {
        final Class<?> type = field.getType();
        final long offset = UNSAFE.objectFieldOffset(field);
        if (!type.isPrimitive()) {
            return new Accessor() {
                @Override public Object get(final Object object) {
                    return UNSAFE.getObject(object, offset);
                }
                @Override public void set(final Object object, @Nullable final Object value) {
                    UNSAFE.putObject(object, offset, value);
                }
            };
        }
        if (type == Boolean.TYPE) {
            return new Accessor() {
                @Override public Object get(final Object object) {
                    return UNSAFE.getBoolean(object, offset);
                }
                @Override public void set(final Object object, @Nullable final Object value) {
                    UNSAFE.putBoolean(object, offset, (Boolean)value);
                }
            };
        }
        if (type == Character.TYPE) {
            return new Accessor() {
                @Override public Object get(final Object object) {
                    return UNSAFE.getChar(object, offset);
                }
                @Override public void set(final Object object, @Nullable final Object value) {
                    UNSAFE.putChar(object, offset, (Character)value);
                }
            };
        }
        if (type == Byte.TYPE) {
            return new Accessor() {
                @Override public Object get(final Object object) {
                    return UNSAFE.getByte(object, offset);
                }
                @Override public void set(final Object object, @Nullable final Object value) {
                    UNSAFE.putByte(object, offset, (Byte)value);
                }
            };
        }
        if (type == Short.TYPE) {
            return new Accessor() {
                @Override public Object get(final Object object) {
                    return UNSAFE.getShort(object, offset);
                }
                @Override public void set(final Object object, @Nullable final Object value) {
                    UNSAFE.putShort(object, offset, (Short)value);
                }
            };
        }
        if (type == Integer.TYPE) {
            return new Accessor() {
                @Override public Object get(final Object object) {
                    return UNSAFE.getInt(object, offset);
                }
                @Override public void set(final Object object, @Nullable final Object value) {
                    UNSAFE.putInt(object, offset, (Integer)value);
                }
            };
        }
        if (type == Long.TYPE) {
            return new Accessor() {
                @Override public Object get(final Object object) {
                    return UNSAFE.getLong(object, offset);
                }
                @Override public void set(final Object object, @Nullable final Object value) {
                    UNSAFE.putLong(object, offset, (Long)value);
                }
            };
        }
        if (type == Float.TYPE) {
            return new Accessor() {
                @Override public Object get(final Object object) {
                    return UNSAFE.getFloat(object, offset);
                }
                @Override public void set(final Object object, @Nullable final Object value) {
                    UNSAFE.putFloat(object, offset, (Float)value);
                }
            };
        }
        if (type == Double.TYPE) {
            return new Accessor() {
                @Override public Object get(final Object object) {
                    return UNSAFE.getDouble(object, offset);
                }
                @Override public void set(final Object object, @Nullable final Object value) {
                    UNSAFE.putDouble(object, offset, (Double)value);
                }
            };
        }
        throw new RuntimeException("unexpected type " + type);
    }

}
