package com.cottagecoders.marketmogul;

public class NetworkUse {
    long received;
    long sent;
    long since;

    public long getReceived() {
        return received;
    }

    public void setReceived(long received) {
        this.received = received;
    }

    public long getSent() {
        return sent;
    }

    public void setSent(long sent) {
        this.sent = sent;
    }

    public long getSince() {
        return since;
    }

    public void setSince(long since) {
        this.since = since;
    }

    public NetworkUse(long received, long sent, long since) {
        this.received = received;
        this.sent = sent;
        this.since = since;

    }
}
