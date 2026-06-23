package kp.utils;

import kp.domain.company.Department;
import kp.domain.company.Title;
import org.neo4j.driver.Driver;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The dataset tools.
 */
public class DatasetTools {
    private static final JsonMapper JSON_MAPPER = new JsonMapper();
    static final String DELETE_EMPLOYEES_QUERY = "MATCH (employee:Employee) DETACH DELETE employee";
    static final String DELETE_DEPARTMENTS_QUERY = "MATCH (department:Department) DETACH DELETE department";
    static final String ADD_DEPARTMENTS_AND_EMPLOYEES_QUERY = """
            UNWIND $departments AS dep
            MERGE (department:Department {id: dep.id})
            SET department.name = dep.name
            FOREACH (emp IN dep.employees | MERGE (employee:Employee {id: emp.id})
                SET employee.firstName = emp.firstName,
                    employee.lastName = emp.lastName,
                    employee.title = emp.title
                MERGE (employee)-[:WORKS_IN]->(department)
            )
            """;

    /**
     * Deletes and adds departments with employees.
     *
     * @param driver the Neo4j driver
     */
    public static void recreateDepartmentsDatasetInNeo4j(Driver driver) {

        try {
            driver.executableQuery(DELETE_EMPLOYEES_QUERY).execute();
            driver.executableQuery(DELETE_DEPARTMENTS_QUERY).execute();
            driver.executableQuery(ADD_DEPARTMENTS_AND_EMPLOYEES_QUERY)
                    .withParameters(createParametersMap()).execute();
        } catch (Exception e) {
            System.out.printf("recreateDepartmentsDatasetInNeo4j(): exception[%s]%n", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the departments parameters.
     *
     * @return the departments parameters
     */
    private static Map<String, Object> createParametersMap() {

        final List<Department> departmentList = JSON_MAPPER.readValue(getJsonWithDepartments(), new TypeReference<>() {
        });
        final List<Map<String, Object>> departmentsParam = departmentList.stream().map(dept -> {
            final List<Map<String, Object>> employeesParam = dept.employees().stream().map(emp ->
                    Map.<String, Object>of(
                            "id", (long) emp.id(),
                            "firstName", emp.firstName(),
                            "lastName", emp.lastName(),
                            "title", Optional.ofNullable(emp.title()).map(Title::getName).orElse("")
                    )
            ).toList();
            return Map.of(
                    "id", (long) dept.id(),
                    "name", dept.name(),
                    "employees", employeesParam
            );
        }).toList();
        return Map.of("departments", departmentsParam);
    }

    /**
     * Gets JSON with Departments.
     *
     * @return the JSON data
     */
    private static String getJsonWithDepartments() {
        return """
                [{
                  "id": 1,
                  "name": "1st Front Office",
                  "keywords": [
                	"Banking"
                  ],
                  "notes": "This office includes:\\n - corporate finance\\n - sales personnel",
                  "startDate": "2020-01-20T00:00:00.000Z",
                  "endDate": "2020-02-14T00:00:00.000Z",
                  "image": "images/CommercialBuilding01.jpg",
                  "employees": [
                	{
                	  "id": 1,
                	  "firstName": "John",
                	  "lastName": "Doe",
                	  "title": "Manager",
                	  "phone": "+1 202-555-0121",
                	  "mail": "John.Doe@example.com",
                	  "streetName": "Pennsylvania Ave NW",
                	  "houseNumber": "1600",
                	  "postalCode": "20500",
                	  "locality": "Washington",
                	  "province": "DC",
                	  "country": "United States"
                	},
                	{
                	  "id": 2,
                	  "firstName": "Brett",
                	  "lastName": "Boe",
                	  "title": "Analyst",
                	  "phone": "+1 212-555-0132",
                	  "mail": "Brett.Boe@example.com",
                	  "streetName": "5th Avenue",
                	  "houseNumber": "350",
                	  "postalCode": "10118",
                	  "locality": "New York",
                	  "province": "NY",
                	  "country": "United States"
                	},
                	{
                	  "id": 3,
                	  "firstName": "Carla",
                	  "lastName": "Coe",
                	  "title": "Analyst",
                	  "phone": "+1 213-555-0173",
                	  "mail": "Carla.Coe@example.com",
                	  "streetName": "Sunset Blvd",
                	  "houseNumber": "7000",
                	  "postalCode": "90028",
                	  "locality": "Los Angeles",
                	  "province": "CA",
                	  "country": "United States"
                	},
                	{
                	  "id": 4,
                	  "firstName": "Donna",
                	  "lastName": "Doe",
                	  "title": "Developer",
                	  "phone": "+1 312-555-0194",
                	  "mail": "Donna.Doe@example.com",
                	  "streetName": "Wacker Dr",
                	  "houseNumber": "233",
                	  "postalCode": "60606",
                	  "locality": "Chicago",
                	  "province": "IL",
                	  "country": "United States"
                	},
                	{
                	  "id": 5,
                	  "firstName": "Kriste",
                	  "lastName": "Etue",
                	  "title": "Developer",
                	  "phone": "+1 713-555-0215",
                	  "mail": "Kriste.Etue@example.com",
                	  "streetName": "Main St",
                	  "houseNumber": "910",
                	  "postalCode": "77002",
                	  "locality": "Houston",
                	  "province": "TX",
                	  "country": "United States"
                	}
                  ]
                },
                {
                  "id": 2,
                  "name": "2nd Front Office",
                  "keywords": [
                	"Insurance"
                  ],
                  "notes": "This office includes:\\n - corporate finance\\n - sales personnel",
                  "startDate": "2020-01-20T00:00:00.000Z",
                  "endDate": "2020-02-14T00:00:00.000Z",
                  "image": "images/CommercialBuilding02.jpg",
                  "employees": [
                	{
                	  "id": 6,
                	  "firstName": "Frank",
                	  "lastName": "Foe",
                	  "title": "Manager",
                	  "phone": "+44 20 7946 0123",
                	  "mail": "Frank.Foe@example.com",
                	  "streetName": "Baker Street",
                	  "houseNumber": "221B",
                	  "postalCode": "NW1 6XE",
                	  "locality": "London",
                	  "province": "",
                	  "country": "United Kingdom"
                	},
                	{
                	  "id": 7,
                	  "firstName": "Grace",
                	  "lastName": "Goe",
                	  "title": "Analyst",
                	  "phone": "+44 161 555 0144",
                	  "mail": "Grace.Goe@example.com",
                	  "streetName": "Deansgate",
                	  "houseNumber": "1",
                	  "postalCode": "M3 2BW",
                	  "locality": "Manchester",
                	  "province": "",
                	  "country": "United Kingdom"
                	},
                	{
                	  "id": 8,
                	  "firstName": "Harry",
                	  "lastName": "Hoe",
                	  "title": "Analyst",
                	  "phone": "+44 121 555 0165",
                	  "mail": "Harry.Hoe@example.com",
                	  "streetName": "New Street",
                	  "houseNumber": "100",
                	  "postalCode": "B2 4HQ",
                	  "locality": "Birmingham",
                	  "province": "",
                	  "country": "United Kingdom"
                	},
                	{
                	  "id": 9,
                	  "firstName": "Larry",
                	  "lastName": "Loe",
                	  "title": "Developer",
                	  "phone": "+44 141 555 0186",
                	  "mail": "Larry.Loe@example.com",
                	  "streetName": "George Square",
                	  "houseNumber": "50",
                	  "postalCode": "G2 1DU",
                	  "locality": "Glasgow",
                	  "province": "",
                	  "country": "United Kingdom"
                	},
                	{
                	  "id": 10,
                	  "firstName": "Mark",
                	  "lastName": "Moe",
                	  "title": "Developer",
                	  "phone": "+44 131 555 0207",
                	  "mail": "Mark.Moe@example.com",
                	  "streetName": "Princes Street",
                	  "houseNumber": "10",
                	  "postalCode": "EH2 2AN",
                	  "locality": "Edinburgh",
                	  "province": "",
                	  "country": "United Kingdom"
                	}
                  ]
                },
                {
                  "id": 3,
                  "name": "1st Middle Office",
                  "keywords": [
                	"Banking"
                  ],
                  "notes": "This office includes:\\n - risk management\\n - information technology",
                  "startDate": "2020-01-20T00:00:00.000Z",
                  "endDate": "2020-02-14T00:00:00.000Z",
                  "image": "images/CommercialBuilding03.jpg",
                  "employees": [
                	{
                	  "id": 11,
                	  "firstName": "Norma",
                	  "lastName": "Noe",
                	  "title": "Manager",
                	  "phone": "+33 1 45 67 89 01",
                	  "mail": "Norma.Noe@example.com",
                	  "streetName": "Avenue des Champs-Élysées",
                	  "houseNumber": "50",
                	  "postalCode": "75008",
                	  "locality": "Paris",
                	  "province": "",
                	  "country": "France"
                	},
                	{
                	  "id": 12,
                	  "firstName": "Paula",
                	  "lastName": "Poe",
                	  "title": "Analyst",
                	  "phone": "+33 4 91 12 34 56",
                	  "mail": "Paula.Poe@example.com",
                	  "streetName": "La Canebière",
                	  "houseNumber": "10",
                	  "postalCode": "13001",
                	  "locality": "Marseille",
                	  "province": "",
                	  "country": "France"
                	},
                	{
                	  "id": 13,
                	  "firstName": "Richard",
                	  "lastName": "Roe",
                	  "title": "Analyst",
                	  "phone": "+33 4 78 12 34 56",
                	  "mail": "Richard.Roe@example.com",
                	  "streetName": "Rue de la République",
                	  "houseNumber": "20",
                	  "postalCode": "69002",
                	  "locality": "Lyon",
                	  "province": "",
                	  "country": "France"
                	},
                	{
                	  "id": 14,
                	  "firstName": "Sammy",
                	  "lastName": "Soe",
                	  "title": "Developer",
                	  "phone": "+33 5 61 23 45 67",
                	  "mail": "Sammy.Soe@example.com",
                	  "streetName": "Allées Jean Jaurès",
                	  "houseNumber": "8",
                	  "postalCode": "31000",
                	  "locality": "Toulouse",
                	  "province": "",
                	  "country": "France"
                	},
                	{
                	  "id": 15,
                	  "firstName": "Tommy",
                	  "lastName": "Toe",
                	  "title": "Developer",
                	  "phone": "+33 4 93 12 34 56",
                	  "mail": "Tommy.Toe@example.com",
                	  "streetName": "Promenade des Anglais",
                	  "houseNumber": "60",
                	  "postalCode": "06000",
                	  "locality": "Nice",
                	  "province": "",
                	  "country": "France"
                	}
                  ]
                },
                {
                  "id": 4,
                  "name": "2nd Middle Office",
                  "keywords": [
                	"Insurance"
                  ],
                  "notes": "This office includes:\\n - risk management\\n - information technology",
                  "startDate": "2020-01-20T00:00:00.000Z",
                  "endDate": "2020-02-14T00:00:00.000Z",
                  "image": "images/CommercialBuilding04.jpg",
                  "employees": [
                	{
                	  "id": 16,
                	  "firstName": "Vince",
                	  "lastName": "Voe",
                	  "title": "Manager",
                	  "phone": "+49 30 1234567",
                	  "mail": "Vince.Voe@example.com",
                	  "streetName": "Unter den Linden",
                	  "houseNumber": "5",
                	  "postalCode": "10117",
                	  "locality": "Berlin",
                	  "province": "",
                	  "country": "Germany"
                	},
                	{
                	  "id": 17,
                	  "firstName": "William",
                	  "lastName": "Woe",
                	  "title": "Analyst",
                	  "phone": "+49 40 2345678",
                	  "mail": "William.Woe@example.com",
                	  "streetName": "Reeperbahn",
                	  "houseNumber": "1",
                	  "postalCode": "20359",
                	  "locality": "Hamburg",
                	  "province": "",
                	  "country": "Germany"
                	},
                	{
                	  "id": 18,
                	  "firstName": "Xerxes",
                	  "lastName": "Xoe",
                	  "title": "Analyst",
                	  "phone": "+49 89 3456789",
                	  "mail": "Xerxes.Xoe@example.com",
                	  "streetName": "Leopoldstraße",
                	  "houseNumber": "10",
                	  "postalCode": "80802",
                	  "locality": "Munich",
                	  "province": "",
                	  "country": "Germany"
                	},
                	{
                	  "id": 19,
                	  "firstName": "Richard",
                	  "lastName": "Miles",
                	  "title": "Developer",
                	  "phone": "+49 69 4567890",
                	  "mail": "Richard.Miles@example.com",
                	  "streetName": "Zeil",
                	  "houseNumber": "100",
                	  "postalCode": "60313",
                	  "locality": "Frankfurt",
                	  "province": "",
                	  "country": "Germany"
                	},
                	{
                	  "id": 20,
                	  "firstName": "John",
                	  "lastName": "Stiles",
                	  "title": "Developer",
                	  "phone": "+49 711 5678901",
                	  "mail": "John.Stiles@example.com",
                	  "streetName": "Königstraße",
                	  "houseNumber": "20",
                	  "postalCode": "70173",
                	  "locality": "Stuttgart",
                	  "province": "",
                	  "country": "Germany"
                	}
                  ]
                },
                {
                  "id": 5,
                  "name": "1st Back Office",
                  "keywords": [
                	"Banking"
                  ],
                  "notes": "This office includes:\\n - administrative services\\n - support services",
                  "startDate": "2020-01-20T00:00:00.000Z",
                  "endDate": "2020-02-14T00:00:00.000Z",
                  "image": "images/CommercialBuilding05.jpg",
                  "employees": [
                	{
                	  "id": 21,
                	  "firstName": "John",
                	  "lastName": "Noakes",
                	  "title": "Manager",
                	  "phone": "+34 91 123 4567",
                	  "mail": "John.Noakes@example.com",
                	  "streetName": "Gran Via",
                	  "houseNumber": "28",
                	  "postalCode": "28013",
                	  "locality": "Madrid",
                	  "province": "",
                	  "country": "Spain"
                	},
                	{
                	  "id": 22,
                	  "firstName": "Mary",
                	  "lastName": "Major",
                	  "title": "Analyst",
                	  "phone": "+34 93 234 5678",
                	  "mail": "Mary.Major@example.com",
                	  "streetName": "La Rambla",
                	  "houseNumber": "100",
                	  "postalCode": "08002",
                	  "locality": "Barcelona",
                	  "province": "",
                	  "country": "Spain"
                	},
                	{
                	  "id": 23,
                	  "firstName": "Jane",
                	  "lastName": "Smith",
                	  "title": "Analyst",
                	  "phone": "+34 96 345 6789",
                	  "mail": "Jane.Smith@example.com",
                	  "streetName": "Avenida del Puerto",
                	  "houseNumber": "12",
                	  "postalCode": "46021",
                	  "locality": "Valencia",
                	  "province": "",
                	  "country": "Spain"
                	},
                	{
                	  "id": 24,
                	  "firstName": "John",
                	  "lastName": "Bloggs",
                	  "title": "Developer",
                	  "phone": "+34 95 456 7890",
                	  "mail": "John.Bloggs@example.com",
                	  "streetName": "Calle Sierpes",
                	  "houseNumber": "25",
                	  "postalCode": "41004",
                	  "locality": "Seville",
                	  "province": "",
                	  "country": "Spain"
                	},
                	{
                	  "id": 25,
                	  "firstName": "Roger",
                	  "lastName": "Galvin",
                	  "title": "Developer",
                	  "phone": "+34 976 567 890",
                	  "mail": "Roger.Galvin@example.com",
                	  "streetName": "Paseo Independencia",
                	  "houseNumber": "14",
                	  "postalCode": "50004",
                	  "locality": "Zaragoza",
                	  "province": "",
                	  "country": "Spain"
                	}
                  ]
                },
                {
                  "id": 6,
                  "name": "2nd Back Office",
                  "keywords": [
                	"Insurance"
                  ],
                  "notes": "This office includes:\\n - administrative services\\n - support services",
                  "startDate": "2020-01-20T00:00:00.000Z",
                  "endDate": "2020-02-14T00:00:00.000Z",
                  "image": "images/CommercialBuilding06.jpg",
                  "employees": [
                	{
                	  "id": 26,
                	  "firstName": "Rick",
                	  "lastName": "Snyder",
                	  "title": "Manager",
                	  "phone": "+41 44 668 18 00",
                	  "mail": "Rick.Snyder@example.com",
                	  "streetName": "Bahnhofstrasse",
                	  "houseNumber": "10",
                	  "postalCode": "8001",
                	  "locality": "Zurich",
                	  "province": "",
                	  "country": "Switzerland"
                	},
                	{
                	  "id": 27,
                	  "firstName": "Bill",
                	  "lastName": "Schuette",
                	  "title": "Analyst",
                	  "phone": "+41 22 908 20 00",
                	  "mail": "Bill.Schuette@example.com",
                	  "streetName": "Rue du Rhône",
                	  "houseNumber": "25",
                	  "postalCode": "1204",
                	  "locality": "Geneva",
                	  "province": "",
                	  "country": "Switzerland"
                	},
                	{
                	  "id": 28,
                	  "firstName": "William",
                	  "lastName": "Forsyth",
                	  "title": "Analyst",
                	  "phone": "+41 61 685 11 11",
                	  "mail": "William.Forsyth@example.com",
                	  "streetName": "Freie Strasse",
                	  "houseNumber": "50",
                	  "postalCode": "4051",
                	  "locality": "Basel",
                	  "province": "",
                	  "country": "Switzerland"
                	},
                	{
                	  "id": 29,
                	  "firstName": "Wilbur",
                	  "lastName": "Friedman",
                	  "title": "Developer",
                	  "phone": "+41 31 321 21 21",
                	  "mail": "Wilbur.Friedman@example.com",
                	  "streetName": "Kramgasse",
                	  "houseNumber": "45",
                	  "postalCode": "3011",
                	  "locality": "Bern",
                	  "province": "",
                	  "country": "Switzerland"
                	},
                	{
                	  "id": 30,
                	  "firstName": "Thomas",
                	  "lastName": "Ferguson",
                	  "title": "Developer",
                	  "phone": "+41 21 613 12 12",
                	  "mail": "Thomas.Ferguson@example.com",
                	  "streetName": "Rue de Bourg",
                	  "houseNumber": "20",
                	  "postalCode": "1003",
                	  "locality": "Lausanne",
                	  "province": "",
                	  "country": "Switzerland"
                	}
                  ]
                }]""";
    }
}