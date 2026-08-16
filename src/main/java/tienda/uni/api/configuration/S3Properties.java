package tienda.uni.api.configuration;

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
            BucketItem profilePictures,
            BucketItem postMedia
    ) {
    }

    public record BucketItem(
            String name,
            String url
    ) {
    }
}