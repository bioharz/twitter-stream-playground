package at.shokri;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class App {

    static private final String CONSUMER_KEY = "PWqQrFSwQPf2eoMBYOvsskmWN";
    static private final String CONSUMER_SECRET = "UjNF6NR2PvWZaEo2j7iOM8j2ZAapjy1L9QTB2WkjXBlBIX102J";
    static private final String TOKEN = "906129464299585536-cYQ1jZpVR7r7brbmBfkBOf6a51aAaqz"; //Access Token???
    static private final String SECRET = "GErYeNEsWHrZ9vnPENQKDG3RTtnYD7DMKdRnhIoLV91LF"; //Access Token Secret???



    public static void main(String[] args) {
        System.out.println("configure...");
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        // Optional: set up some followings and track terms
        //List<Long> followings = Lists.newArrayList(1234L, 566788L);
        List<Long> followings = Lists.newArrayList(906129464299585536L);


        //List<String> terms = Lists.newArrayList("twitter", "api");

        hosebirdEndpoint.followings(followings);
        //hosebirdEndpoint.....
        //hosebirdEndpoint.trackTerms(terms);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, SECRET);

        System.out.println("building client...");
        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

        Client hosebirdClient = builder.build();
        // Attempts to establish a connection.
        hosebirdClient.connect();

        // on a different thread, or multiple different threads....
        try {

            while (!hosebirdClient.isDone()) {
                System.out.println("hosebirdClient...");

            /*
            String msg = msgQueue.take();
            something(msg);
            profit();
            */

                String msg = msgQueue.take();
                System.out.println(msg);


            }
        } catch (Exception e) {
            hosebirdClient.stop();
            e.fillInStackTrace();
        }
        System.out.println("end of hosebirdClient...");

/*
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        // Optional: set up some followings and track terms
        List<Long> followings = Lists.newArrayList(1234L, 566788L);
        List<String> terms = Lists.newArrayList("twitter", "api");
        endpoint.followings(followings);
        endpoint.trackTerms(terms);*/

    }
}
