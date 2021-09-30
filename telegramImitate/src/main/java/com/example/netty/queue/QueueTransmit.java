package com.example.netty.queue;

import io.netty.channel.Channel;

public interface QueueTransmit {
    void pushGroup(String message);
    void register(Channel channel);
}
