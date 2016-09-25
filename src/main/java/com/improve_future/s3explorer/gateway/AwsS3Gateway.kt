package com.improve_future.s3explorer.gateway

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.*
import com.improve_future.s3explorer.config.AwsS3Config
import java.io.FilterInputStream
import java.io.FilterOutputStream

class AwsS3Gateway(config: AwsS3Config) {
    private val client: AmazonS3Client

    init {
        val credentials = object : AWSCredentials {
            override fun getAWSAccessKeyId(): String {
                return config.accessKeyId
            }

            override fun getAWSSecretKey(): String {
                return config.secretAccessKey
            }
        }
        this.client = AmazonS3Client(credentials)
    }

    fun findAllBuckets(): List<Bucket> {
        return client.listBuckets()
    }

    fun findAllObjects(
            bucketName: String,
            prefix: String,
            delimiter: String = "/"): ObjectListing {
        val request = ListObjectsRequest()
        request.bucketName = bucketName
        request.prefix = prefix
        request.delimiter = delimiter
        return this.client.listObjects(request)
    }

    fun deleteObject(
            bucketName: String,
            key: String) {
        val request = DeleteObjectRequest(bucketName, key)
        this.client.deleteObject(request)
    }

    fun findObject(
            bucketName: String,
            key: String): S3Object {
        return this.client.getObject(bucketName, key)
    }

    fun findObjectStream(
            bucketName: String,
            key: String): FilterInputStream {
        return findObject(bucketName, key).objectContent
    }
}