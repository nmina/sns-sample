package com.aws.nsn;

import com.amazonaws.services.sns.AmazonSNS;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        AmazonSNS snsClient = SNSCommands.setUpAwsSnsClient();
        SNSCommands.sendSMS(snsClient, "+19545529974");
    }
}
