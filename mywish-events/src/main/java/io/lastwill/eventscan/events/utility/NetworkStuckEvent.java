package io.lastwill.eventscan.events.utility;

import io.mywish.scanner.model.BaseEvent;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
public class NetworkStuckEvent extends BaseEvent {
    private final LocalDateTime receivedTime;
    private final Instant lastTimestamp;
    private final long lastBlockNo;
    public NetworkStuckEvent(NetworkType networkType, LocalDateTime receivedTime, Instant lastTimestamp, long lastBlockNo) {
        super(networkType);
        this.receivedTime = receivedTime;
        this.lastTimestamp = lastTimestamp;
        this.lastBlockNo = lastBlockNo;
    }
}
