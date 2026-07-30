package tienda.uni.api.app.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class ProblemDetailResponse extends ProblemDetail {

    public static ResponseEntity<ProblemDetail> buildResponse(
            HttpStatus status,
            String uri,
            String title,
            String detail
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(uri));
        problem.setTitle(title);

        return ResponseEntity.status(status).body(problem);
    }
}
