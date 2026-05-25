package kp.domain.company;

/**
 * Represents an employee.
 *
 * @param id        the id
 * @param firstName the first name
 * @param lastName  the last name
 * @param title     the title
 */
public record Employee(int id, String firstName, String lastName, Title title) {
}
