package dev.tuanm.demo.repository;

import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractWriteRepository<E> {
    protected final JdbcTemplate jdbcTemplate;

    protected AbstractWriteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Saves a collection of entities with multi-row insertion mechanism.
     *
     * @param entities the collection of entities.
     */
    public abstract void saveAll(Collection<E> entities);
}
