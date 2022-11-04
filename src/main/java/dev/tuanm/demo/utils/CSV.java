package dev.tuanm.demo.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Used for CSV-related manipulations.
 *
 * @author Tuanm
 */
public final class CSV {
    private static final String COMMA_DELIMITER = ",";
    private static final String EMPTY_VALUE = "";

    private boolean hasHeader = true;
    private String delimiter = COMMA_DELIMITER;
    /**
     * Stores all columns of the loading process.
     */
    private Map<String, List<String>> properties;
    /**
     * The number of entity associated with this instance.
     */
    private int total;

    private CSV() {
        this.initialize();
    }

    /**
     * Initializes the parameters.
     */
    private void initialize() {
        this.properties = new LinkedHashMap<>(); // to keep the keys in order
        this.total = 0;
    }

    private CSV hasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
        return this;
    }

    private CSV delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     * An applier for header-value pair used in CSV conversion.
     */
    public static interface HeaderValueApplier extends BiConsumer<String, String> {
    }

    /**
     * A mapping function for entity modification
     * that accepts an entity as the parameter,
     * returns a {@link HeaderValueApplier} taking a header-value pair
     * for the entity's modification.
     *
     * Demo:
     *
     * <pre>
     *   &#64;NoArgsConstructor
     *   &#64;Setter
     *   public class Entity {
     *       private String foo;
     *       private int bar;
     *   }
     *
     *
     *   public class MyEntityMapper implements CSV.HeaderValueEntityMapper&#60;Entity&#62; {
     *       &#64;Override
     *       public CSV.HeaderValueApplier apply(Entity entity) {
     *           CSV.HeaderValueApplier applier = (header, value) => {
     *               if (header.equals("foo")) {
     *                   entity.setFoo(value);
     *               } else if (header.equals("bar")) {
     *                   entity.setBar(Integer.valueOf(value));
     *               }
     *           };
     *           return applier;
     *       }
     *   }
     *
     *
     *   String text = "foo,bar\nFoo,3";
     *   CSV.HeaderValueEntityMapper&#60;Entity&#62; myEntityMapper = new MyEntityMapper();
     *   Collection&#60;Entity&#62; entities = CSV.builder().build()
     *           .fromText(text)
     *           .as(Entity::new, myEntityMapper);
     * </pre>
     */
    public static interface HeaderValueEntityMapper<T> extends Function<T, HeaderValueApplier> {
    }

    /**
     * Returns the number of records being held.
     */
    public int totalRecords() {
        return this.total;
    }

    /**
     * Converts the {@link CSV} to a collection of entities {@link T}.
     *
     * @param <T>         the type of the entity.
     * @param constructor the constructor of {@link T} for entity initialization.
     * @param mapper      the mapping function for entity modification with each
     *                    header-value pair appearing in the CSV conversion.
     */
    public <T> Collection<T> as(Supplier<T> constructor, HeaderValueEntityMapper<T> mapper) {
        List<T> entities = new ArrayList<>();
        for (int index = 0; index < total; index++) {
            T entity = constructor.get();
            for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
                String header = entry.getKey();
                String value = Optional.ofNullable(properties.get(header).get(index))
                        .orElse(EMPTY_VALUE).trim();
                Optional.ofNullable(mapper.apply(entity))
                        .ifPresent(applier -> applier.accept(header, value));
            }
            entities.add(entity);
        }
        return entities;
    }

    /**
     * Loads from a text to initialize CSV-structural properties.
     *
     * @param text the text.
     */
    public CSV fromText(String text) {
        initialize();

        String[] lines = text.split(System.lineSeparator());
        if (lines.length > 0) {
            return hasHeader
                    ? loadWithHeader(lines)
                    : loadWithoutHeader(lines);
        }

        return this;
    }

    private CSV loadWithHeader(String[] lines) {
        for (String word : lines[0].split(delimiter)) {
            properties.put(
                    word.trim().toLowerCase(),
                    new ArrayList<>());
        }
        for (int index = 1; index < lines.length; index++, total++) {
            String[] values = lines[index].split(delimiter);
            int header = 0;
            for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
                entry.getValue().add(values[header++].trim());
            }
        }
        return this;
    }

    private CSV loadWithoutHeader(String[] lines) {
        Integer header = 0;
        for (int index = 0; index < lines.length; index++, total++) {
            String[] values = lines[index].split(delimiter);
            for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
                List<String> column = entry.getValue();
                if (column == null) {
                    column = new ArrayList<>();
                    properties.put((++header).toString(), column); // increase header counter when new column found
                }
                column.add(values[index].trim());
            }
        }
        return this;
    }

    /**
     * Creates a builder for the {@link CSV} creation.
     */
    public static CSVBuilder builder() {
        return new CSVBuilder();
    }

    /**
     * A builder.
     */
    public static class CSVBuilder {
        private final CSV csv;

        private CSVBuilder() {
            this.csv = new CSV();
        }

        /**
         * Eventually initalizes a {@link CSV} instance.
         */
        public CSV build() {
            return this.csv;
        }

        /**
         * Indicates that the {@link CSV} instance has the header row or not.
         */
        public CSVBuilder hasHeader(boolean hasHeader) {
            this.csv.hasHeader(hasHeader);
            return this;
        }

        /**
         * Indicates the separator for the text-parsing process.
         */
        public CSVBuilder delimiter(String delimiter) {
            this.csv.delimiter(delimiter);
            return this;
        }
    }
}