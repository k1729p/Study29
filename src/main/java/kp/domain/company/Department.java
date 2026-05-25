package kp.domain.company;

import java.util.List;

/**
 * Represents a department.
 *
 * @param id        the id
 * @param name      the name
 * @param employees the list of {@link Employee}s
 */
public record Department(int id, String name, List<Employee> employees) {
}