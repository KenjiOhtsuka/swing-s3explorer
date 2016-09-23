package com.improve_future.s3explorer.config;

public class AwsS3Config {
    private String region;
    private String accessKeyId;
    private String secretAccessKey;
    private String bucketName;

    public String getRegion() {
        return this.region;
    }

    public String getAccessKeyId() {
        return this.accessKeyId;
    }

    public String getSecretAccessKey() {
        return this.secretAccessKey;
    }

    public String getBucketName() { return this.bucketName; }

    public AwsS3Config(String oneLineConfig) {
        String[] items = oneLineConfig.split(":");
        region = items[0];
        accessKeyId = items[1];
        secretAccessKey = items[2];
        if (items.length > 3) bucketName = items[3];
    }

    public boolean isBucketSpecific() {
        return bucketName != null;
    }
}
