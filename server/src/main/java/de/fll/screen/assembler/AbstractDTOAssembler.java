package de.fll.screen.assembler;

public interface AbstractDTOAssembler<T, D> {
    /**
     * Convert an entity to a DTO.
     * Should handle null input gracefully.
     * @param entity the entity to convert
     * @return the DTO
     */
    D toDTO(T entity);

    /**
     * Convert a DTO to an entity.
     * Should handle null input gracefully.
     * @param dto the DTO to convert
     * @return the entity
     */
    T fromDTO(D dto);
}
