package com.improve_future.s3explorer.config

class AwsS3Config(oneLineConfig: String) {
    val region: String
    val accessKeyId: String
    val secretAccessKey: String
    var bucketName: String? = null
        private set

    init {
        val items = oneLineConfig.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        region = items[0]
        accessKeyId = items[1]
        secretAccessKey = items[2]
        if (items.size > 3) bucketName = items[3]
    }

    val isBucketSpecific: Boolean
        get() = bucketName != null
}
