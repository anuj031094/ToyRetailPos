package com.ey.posvendor.config;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AwsSQSConfig {

    @Value( "${aws.access.key}" )
    private String AWS_ACCESS_KEY;
    @Value("${aws.secret.key}")
    private String AWS_SECRET_KEY;

    @Value("${aws.sqs.queue}")
    private String AWS_SQS_QUEUE;
    @Value("${aws.sqs.arn}")
    private String AWS_SQS_QUEUE_ARN;
    @Value("${aws.sqs.queue.url}")
    private String AWS_SQS_QUEUE_URL;

    @Value("${aws.sqs.dl.queue}")
    private String AWS_SQS_DL_QUEUE;
    @Value("${aws.sqs.dl.arn}")
    private String AWS_SQS_QUEUE_DL_ARN;
    @Value("${aws.sqs.dl.queue.url}")
    private String AWS_SQS_QUEUE_DL_URL;

    private AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
    }

    @Bean("sqsClientBuilder")
    public AmazonSQS sqsClientBuilder() {
        return AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    public void createSQSQueue(String queueName) {
        AmazonSQS sqsClient = sqsClientBuilder();
        CreateQueueRequest createStandardQueueRequest = new CreateQueueRequest(queueName);
        String standardQueueUrl = sqsClient.createQueue(createStandardQueueRequest).getQueueUrl();
        System.out.println("AWS SQS Queue URL: " + standardQueueUrl);
    }
}