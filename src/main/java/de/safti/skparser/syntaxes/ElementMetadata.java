package de.safti.skparser.syntaxes;

import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.runtime.arguments.Argument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ElementMetadata {

    private final Map<String, Object> data = new HashMap<>();
    private final List<Argument<?>> arguments;
    private final String syntaxElementParent;

    public ElementMetadata(@NotNull List<Argument<?>> arguments, String syntaxElementParent) {
        this.arguments = List.copyOf(arguments); // immutable copy
        this.syntaxElementParent = syntaxElementParent;
    }

    public String getSyntaxElementParent() {
        return syntaxElementParent;
    }

    // ------------------------
    // Generic access
    // ------------------------

    @Nullable
    @Contract(pure = true)
    public Object get(@NotNull String key) {
        return data.get(key);
    }

    @Nullable
    @Contract(pure = true)
    public <T> T get(@NotNull String key, @NotNull Class<T> type) {
        Object value = data.get(key);
        if (value == null) return null;
        return type.cast(value);
    }

    public void set(@NotNull String key, @Nullable Object value) {
        data.put(key, value);
    }

    @NotNull
    @Contract(pure = true)
    public Map<String, Object> getAllData() {
        return Collections.unmodifiableMap(data);
    }

    @NotNull
    @Contract(pure = true)
    public Optional<Object> getOpt(@NotNull String key) {
        return Optional.ofNullable(data.get(key));
    }

    @NotNull
    @Contract(pure = true)
    public <T> Optional<T> getOpt(@NotNull String key, @NotNull Class<T> type) {
        Object value = data.get(key);
        if (value == null) return Optional.empty();
        return Optional.of(type.cast(value));
    }

    // ------------------------
    // Primitive nullable getters
    // ------------------------

    @Nullable
    @Contract(pure = true)
    public Integer getInt(@NotNull String key) {
        return (Integer) data.get(key);
    }

    @Nullable
    @Contract(pure = true)
    public Long getLong(@NotNull String key) {
        return (Long) data.get(key);
    }

    @Nullable
    @Contract(pure = true)
    public Double getDouble(@NotNull String key) {
        return (Double) data.get(key);
    }

    @Nullable
    @Contract(pure = true)
    public Boolean getBoolean(@NotNull String key) {
        return (Boolean) data.get(key);
    }

    @Nullable
    @Contract(pure = true)
    public String getString(@NotNull String key) {
        return (String) data.get(key);
    }

    // ------------------------
    // Primitive getters with defaults
    // ------------------------

    @Contract(pure = true)
    public int getInt(@NotNull String key, int defaultValue) {
        Integer value = getInt(key);
        return value != null ? value : defaultValue;
    }

    @Contract(pure = true)
    public long getLong(@NotNull String key, long defaultValue) {
        Long value = getLong(key);
        return value != null ? value : defaultValue;
    }

    @Contract(pure = true)
    public double getDouble(@NotNull String key, double defaultValue) {
        Double value = getDouble(key);
        return value != null ? value : defaultValue;
    }

    @Contract(pure = true)
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        Boolean value = getBoolean(key);
        return value != null ? value : defaultValue;
    }

    @Contract(pure = true)
    public String getString(@NotNull String key, @NotNull String defaultValue) {
        String value = getString(key);
        return value != null ? value : defaultValue;
    }

    // ------------------------
    // Arguments
    // ------------------------

    @Nullable
    @Contract(pure = true)
    public Argument<?> getArgument(int index) {
        return arguments.get(index);
    }

    @NotNull
    @Contract(pure = true)
    public List<Argument<?>> getAllArguments() {
        return arguments;
    }
    @Nullable
    public <T> T evaluateArgument(int index, @NotNull TriggerContext context) {
        Argument<?> arg = getArgumentOrThrow(index);
        //noinspection unchecked
        return (T) arg.evaluate(context, arg.getExpression().getMetadata());
    }

    @Nullable
    public <T> T[] evaluateAllArgument(int index, @NotNull TriggerContext context) {
        Argument<?> arg = getArgumentOrThrow(index);
        //noinspection unchecked
        return (T[]) arg.evaluateAll(context, arg.getExpression().getMetadata());
    }

    @Nullable
    public <T> T evaluateArgument(int index, @NotNull TriggerContext context, Class<T> ignored) {
        Argument<?> arg = getArgumentOrThrow(index);
        //noinspection unchecked
        return (T) arg.evaluate(context, arg.getExpression().getMetadata());
    }

    @Nullable
    public <T> T[] evaluateAllArgument(int index, @NotNull TriggerContext context, Class<T> ignored) {
        Argument<?> arg = getArgumentOrThrow(index);
        //noinspection unchecked
        return (T[]) arg.evaluateAll(context, arg.getExpression().getMetadata());
    }

    private Argument<?> getArgumentOrThrow(int index) {
        if (index < 0 || index >= arguments.size()) {
            String message = "Invalid argument index " + index + "! Syntax: " + syntaxElementParent +
                    ", arguments count: " + arguments.size();
            throw new IndexOutOfBoundsException(message);
        }
        return arguments.get(index);
    }


    @Override
    public String toString() {
        return "ElementMetadata{" +
                "data=" + data +
                ", arguments=" + arguments +
                ", syntaxElementParent='" + syntaxElementParent + '\'' +
                '}';
    }
}
