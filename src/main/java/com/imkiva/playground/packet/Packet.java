package com.imkiva.playground.packet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kiva
 * @date 2019-07-17
 */
public class Packet {
    class PacketData {
        byte[] data;

        public PacketData(byte[] data) {
            this.data = data;
        }
    }

    private Packet parent = null;
    private List<Packet> subPackets = new ArrayList<>();
    private List<PacketData> packetData = new ArrayList<>();

    public Packet() {
    }

    public Packet(Packet parent) {
        this.parent = parent;
    }

    public Packet addSubPacket() {
        Packet p = new Packet(this);
        subPackets.add(p);
        return p;
    }

    public Packet done() {
        return parent;
    }

    public Packet addData(String data) {
        return addData(data.getBytes());
    }

    public Packet addData(byte[] data) {
        packetData.add(new PacketData(data));
        return this;
    }
}
