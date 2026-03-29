package com.shiyuan.sytp.requests;

import java.util.UUID;

public class TeleportRequest {

    private final UUID id;
    private final UUID requester;
    private final UUID target;
    private final RequestType type;
    private final long timestamp;
    private boolean charged; // 标记是否已经收取过费用（用于TPW）

    public TeleportRequest(UUID id, UUID requester, UUID target, RequestType type, long timestamp) {
        this.id = id;
        this.requester = requester;
        this.target = target;
        this.type = type;
        this.timestamp = timestamp;
        this.charged = false;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRequester() {
        return requester;
    }

    public UUID getTarget() {
        return target;
    }

    public RequestType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isCharged() {
        return charged;
    }

    public void setCharged(boolean charged) {
        this.charged = charged;
    }
}
