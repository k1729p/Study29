package kp.domain.company;

import java.util.Optional;

/**
 * The job title of the employee.
 */
@SuppressWarnings({"UnusedDeclaration"})
public enum Title {
    /**
     * The analyst
     */
    ANALYST("Analyst"),
    /**
     * The developer
     */
    DEVELOPER("Developer"),
    /**
     * The manager
     */
    MANAGER("Manager");

    private final String name;

    /**
     * Parameterized constructor.
     *
     * @param name the name
     */
    Title(String name) {
        this.name = name;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Create title from its name.
     *
     * @param name the title name
     * @return the title
     */
    public static Title fromString(String name) {
        return Optional.ofNullable(name).map(String::toUpperCase)
                .map(Title::valueOf).orElse(null);
    }
}