package com.lokcenter.AZN_Spring_ResourceServer.services;

import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Service to use Memcached
 *
 * @version 1.0 22-07-29
 */

@Service
@Slf4j
@Profile("dev")
public class MemService {
    @Value("${memcached.port}")
    private int port;

    @Value("${memcached.address}")
    private String address;

    private MemcachedClient mcc;

    /**
     * Init MemcachedClient
     *
     */
    @PostConstruct
    public void init() throws IOException {
        // connection
        try {
            mcc = new MemcachedClient(new InetSocketAddress(address, port));

            log.info("Memcached connected");
        } catch (Exception e) {
            log.error("Memcached connection failed");
        }
    }

    /**
     * Store new values
     * @param key Key
     * @param value value
     */
    public void storeKeyValue(String key, Object value) {
        // set key
        boolean done = mcc.set(key, 90, value).isDone();

        log.info("isDone: " +done);
    }

    /**
     * Get value by key
     * @param key Key
     * @return Value
     */
    public Object getKeyValue(String key) {
        return mcc.get(key);
    }
}
