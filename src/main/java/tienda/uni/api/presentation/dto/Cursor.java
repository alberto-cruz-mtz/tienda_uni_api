package tienda.uni.api.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record Cursor(UUID id, Instant postedAt) {

    public String join() {
        return postedAt.toString() + "," + id.toString();
    }

    public static Cursor fromString(String cursorString) {
        String[] parts = cursorString.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid cursor format");
        }

        if (parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new IllegalArgumentException("Cursor parts cannot be empty");
        }

        Instant postedAt = Instant.parse(parts[0]);
        UUID id = UUID.fromString(parts[1]);

        return new Cursor(id, postedAt);
    }
}