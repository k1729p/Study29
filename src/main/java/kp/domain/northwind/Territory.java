package kp.domain.northwind;

import java.util.List;

public record Territory(
        Integer territoryID,
        String territoryDescription,
        // relation IN_REGION
        List<Region> regions) {
}
