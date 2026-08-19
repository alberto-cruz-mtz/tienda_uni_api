package tienda.uni.api.presentation.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public final class PostRequestParams {

    private final Pageable pageable;
    private final String search;
    private final Boolean isOutOfStock;
    private final Cursor cursor;

    public PostRequestParams(Integer limit, String search, Boolean isOutOfStock, String cursor) {
        this.pageable = createPageable(limit);
        this.search = search;
        this.isOutOfStock = isOutOfStock;
        this.cursor = buildCursor(cursor);
    }

    private static Cursor buildCursor(String cursor) {
        if (cursor == null) return new Cursor(null, null);

        byte[] decodedCursor = Base64.getUrlDecoder().decode(cursor);
        String cursorString = new String(decodedCursor, StandardCharsets.UTF_8);

        try {
            return Cursor.fromString(cursorString);
        } catch (Exception exception) {
            // TODO: Cambiar por una excepción personalizada para manejar errores de cursor inválido
            throw new IllegalArgumentException("Invalid cursor format", exception);
        }
    }

    private static Pageable createPageable(Integer limit) {
        int size = limit != null ? limit : 10; // Default limit
        Sort sort = Sort.by(Sort.Direction.DESC, "postedAt", "id");
        return PageRequest.of(0, size, sort);
    }

    public Pageable pageable() {
        return pageable;
    }

    public String search() {
        return search;
    }

    public Boolean isOutOfStock() {
        return isOutOfStock;
    }

    public Cursor cursor() {
        return cursor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PostRequestParams) obj;
        return Objects.equals(this.pageable, that.pageable) &&
                Objects.equals(this.search, that.search) &&
                Objects.equals(this.isOutOfStock, that.isOutOfStock) &&
                Objects.equals(this.cursor, that.cursor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageable, search, isOutOfStock, cursor);
    }

    @Override
    public String toString() {
        return "PostRequestParams[" +
                "pageable=" + pageable + ", " +
                "search=" + search + ", " +
                "isOutOfStock=" + isOutOfStock + ", " +
                "cursor=" + cursor + ']';
    }

}