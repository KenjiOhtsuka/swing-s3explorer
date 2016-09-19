package com.improve_future.s3explorer.service;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.improve_future.s3explorer.config.AwsS3Config;
import com.improve_future.s3explorer.gateway.AwsS3Gateway;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kenji on 16/09/17.
 */
public class AwsS3Service {
//    int equals(AwsS3Service awsS3Service) {
//
//    }

//    private static List<AwsS3Config> configList = new ArrayList<AwsS3Config>();
//    private static List<AwsS3Service> instances = new ArrayList<AwsS3Service>();

    private static AwsS3Service instance;
    private AwsS3Gateway awsS3Gateway;

    public static AwsS3Service getInstance(AwsS3Config awsS3Config) {
        if (instance == null) {
            instance = new AwsS3Service(awsS3Config);
        }
        return instance;
    }

    private AwsS3Service(AwsS3Config config) {
        awsS3Gateway = new AwsS3Gateway(config);
    }

    public List<Bucket> findAllBuckets() {
        return awsS3Gateway.findAllBuckets();
    }

    public ObjectListing findAllObjects(
            String bucketName,
            String prefix) {
        return this.findAllObjects(bucketName, prefix, "/");
    }

    public ObjectListing findAllObjects(
            String bucketName,
            String prefix,
            String delimiter) {
        ListObjectsRequest request = new ListObjectsRequest();
        request.setBucketName(bucketName);
        request.setPrefix(prefix);
        request.setDelimiter(delimiter);
        return awsS3Gateway.findAllFolders(request);
    }
}
