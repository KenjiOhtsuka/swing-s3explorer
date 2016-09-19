package com.improve_future.s3explorer.gateway;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.improve_future.s3explorer.config.AwsS3Config;

import java.util.List;

public class AwsS3Gateway {
    private AmazonS3Client client;

    public AwsS3Gateway(final AwsS3Config config) {
        AWSCredentials credentials =
                new AWSCredentials() {
                    public String getAWSAccessKeyId() {
                        return config.getAccessKeyId();
                    }

                    public String getAWSSecretKey() {
                        return config.getSecretAccessKey();
                    }
                };
        this.client = new AmazonS3Client(credentials);
    }

    public List<Bucket> findAllBuckets() {
        return client.listBuckets();
    }

    public ObjectListing findAllFolders(ListObjectsRequest request) {
        return this.client.listObjects(request);
    }
}
