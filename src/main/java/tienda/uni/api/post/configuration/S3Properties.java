package tienda.uni.api.post.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record S3Properties(
        String region,
        String endpoint,
        String accessKey,
        String secretKey,
        Buckets buckets
) {
    public record Buckets(
            String profilePictures,
            String postMedia
    ) {
    }
}