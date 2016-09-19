package com.improve_future.s3explorer.config;

public class AwsS3Config {
    private String region;
    private String accessKeyId;
    private String secretAccessKey;

    public String getRegion() {
        return this.region;
    }

    public String getAccessKeyId() {
        return this.accessKeyId;
    }

    public String getSecretAccessKey() {
        return this.secretAccessKey;
    }

    public AwsS3Config(String oneLineConfig) {
        String[] items = oneLineConfig.split(":");
        region = items[0];
        accessKeyId = items[1];
        secretAccessKey = items[2];
    }
}
