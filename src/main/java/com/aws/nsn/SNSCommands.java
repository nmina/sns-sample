package com.aws.nsn;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CheckIfPhoneNumberIsOptedOutRequest;
import com.amazonaws.services.sns.model.CheckIfPhoneNumberIsOptedOutResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicResult;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

import java.util.HashMap;
import java.util.Map;


public class SNSCommands {
    
    /**
     * Sets up an {@link AmazonSNS} instance thru {@link AmazonSNSClientBuilder} which will be the main interface
     * used for AWS SNS commands.
     * <p>
     * <p>
     * By default, it is looking at the default {@code AwsCredentials.properties} in classpath for credentials.
     * </p>
     *
     * @return
     */
    public static AmazonSNS setUpAwsSnsClient() {
        AmazonSNSClientBuilder snsClientBuilder = AmazonSNSClientBuilder.standard().withCredentials(new ClasspathPropertiesFileCredentialsProvider());
        snsClientBuilder.setRegion(Regions.US_EAST_1.getName());
        AmazonSNS snsClient = snsClientBuilder.build();
        
        return snsClient;
        
    }
    
    /**
     * Creates a topic using {@link CreateTopicRequest}. If by any chance, a topic with the same name is already existing
     * in AWS SNS Cloud, it will return that instance and will not create a new one.
     *
     * @param snsClient
     * @param topic
     * @return {@link CreateTopicResult} - contains the topicArn
     */
    public static CreateTopicResult createTopic(AmazonSNS snsClient, String topic) {
        CreateTopicRequest createTopicRequest = new CreateTopicRequest(topic);
        CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
        
        System.out.println(createTopicResult);
        System.out.println("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
        
        return createTopicResult;
    }
    
    /**
     * Subscribes an email to a topic determined by the topicArn provided.
     *
     * @param snsClient
     * @param topicArn
     * @param emailAddress
     */
    public static void subscribetoTopicEmail(AmazonSNS snsClient, String topicArn, String emailAddress) {
        //subscribe to an SNS topic
        SubscribeRequest subRequest = new SubscribeRequest(topicArn, "email", emailAddress);
        snsClient.subscribe(subRequest);
        //get request id for SubscribeRequest from SNS metadata
        System.out.println("SubscribeRequest Email - " + snsClient.getCachedResponseMetadata(subRequest));
        System.out.println("Check your email and confirm subscription.");
    }
    
    /**
     * Sends a direct SMS to the phone number(format: +1##########) specified.
     * <p>
     * Right now, it only supports USA numbers. Free version :)
     * </p>
     *
     * @param snsClient
     * @param phoneNumber
     */
    public static void sendSMS(AmazonSNS snsClient, String phoneNumber) {
        CheckIfPhoneNumberIsOptedOutResult checkResult = snsClient.checkIfPhoneNumberIsOptedOut(new CheckIfPhoneNumberIsOptedOutRequest().withPhoneNumber(phoneNumber));
        System.out.println("Is number opted out? = " + checkResult.isOptedOut());
        
        if (true) {
            String msg = "CONGRATULATIONS! You won a round trip for 2 in Miramar 3rd floor office!";
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            // smsAttributes.put("AWS.SNS.SMS.SenderId", new MessageAttributeValue().withStringValue("RCCL").withDataType("String"));
            // smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue().withStringValue("Promotional").withDataType("String"));
            // smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue().withStringValue("0.00").withDataType("Number"));
            
            PublishResult result = snsClient.publish(new PublishRequest()
                    .withMessage(msg)
                    .withPhoneNumber(phoneNumber)
                    .withMessageAttributes(smsAttributes));
            
            System.out.println(result);
            System.out.println(result.getSdkResponseMetadata());
        }
    }
    
    /**
     * Publish a message to a topic which is being identified by the topic ARN.
     *
     * @param snsClient
     * @param topicArn
     */
    public static void publish(AmazonSNS snsClient, String topicArn) {
        //publish to an SNS topic
        String msg = "My text published to SNS topic with email endpoint";
        PublishRequest publishRequest = new PublishRequest(topicArn, msg);
        PublishResult publishResult = snsClient.publish(publishRequest);
        //print MessageId of message published to SNS topic
        System.out.println("MessageId - " + publishResult.getMessageId());
    }
    
    /**
     * Deletes a specific topic which is identified by the topic ARN.
     *
     * @param snsClient
     * @param topicArn
     */
    public static void deleteTopic(AmazonSNS snsClient, String topicArn) {
        //delete an SNS topic
        DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
        snsClient.deleteTopic(deleteTopicRequest);
        //get request id for DeleteTopicRequest from SNS metadata
        System.out.println("DeleteTopicRequest - " + snsClient.getCachedResponseMetadata(deleteTopicRequest));
    }
    
    /**
     * Prints out all the endpoints subscribed to a specific topic identified by topic ARN.
     *
     * @param snsClient
     * @param topicArn
     */
    public static void getSubscriptionsbyTopicArn(AmazonSNS snsClient, String topicArn) {
        ListSubscriptionsByTopicResult results = snsClient.listSubscriptionsByTopic("arn:aws:sns:us-east-1:371633843640:MyNewTopic");
        
        results.getSubscriptions().forEach(result -> {
            System.out.println(result);
        });
        // GetSubscriptionAttributesResult gresult = snsClient.getSubscriptionAttributes("arn:aws:sns:us-east-1:371633843640:MyNewTopic:58fe6668-5852-422b-ad2d-a38e52a7f8ed");
        // System.out.println(gresult);
    }
}

