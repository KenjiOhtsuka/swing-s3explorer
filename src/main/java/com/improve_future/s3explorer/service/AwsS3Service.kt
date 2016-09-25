package com.improve_future.s3explorer.service

import com.amazonaws.services.s3.model.*
import com.improve_future.s3explorer.config.AwsS3Config
import com.improve_future.s3explorer.gateway.AwsS3Gateway
import com.improve_future.s3explorer.gateway.FileSystemGateway

class AwsS3Service private constructor(config: AwsS3Config) {
    private val awsS3Gateway: AwsS3Gateway
    private val fileSystemGateway: FileSystemGateway

    init {
        awsS3Gateway = AwsS3Gateway(config)
        fileSystemGateway = FileSystemGateway()
    }

    fun findAllBuckets(): List<Bucket> {
        return awsS3Gateway.findAllBuckets()
    }

    fun findAllObjects(
            bucketName: String,
            prefix: String,
            delimiter: String = "/"): ObjectListing {
        return awsS3Gateway.findAllObjects(
                bucketName, prefix, delimiter)
    }

    fun deleteObject(
            bucketName: String,
            key: String) {
        return awsS3Gateway.deleteObject(
                bucketName, key)
    }

    fun downloadObject(
            bucketName: String,
            key: String) {
        val stream = awsS3Gateway.findObjectStream(bucketName, key)
        fileSystemGateway.save(stream, bucketName + "/" + key)

    }

    companion object {
        //    int equals(AwsS3Service awsS3Service) {
        //
        //    }

        //    private static List<AwsS3Config> configList = new ArrayList<AwsS3Config>();
        //    private static List<AwsS3Service> instances = new ArrayList<AwsS3Service>();

        private var instance: AwsS3Service? = null

        fun getInstance(awsS3Config: AwsS3Config): AwsS3Service {
            if (instance == null) {
                instance = AwsS3Service(awsS3Config)
            }
            return instance!!
        }
    }
}
