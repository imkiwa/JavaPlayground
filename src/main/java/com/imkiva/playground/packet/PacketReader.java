package com.imkiva.playground.packet;

/**
 * @author kiva
 * @date 2019-07-17
 */
public class PacketReader {
    private Packet readAll() {
        // 1
        Packet rootPacket = new Packet()
                .addSubPacket() // 1.1 begin
                    .addData("1721115102")  // 1.1.1
                    .addData("1959903082")  // 1.1.2
                    .addData("...")         // ...
                    .addSubPacket()         // 1.1.9
                        .addData("809551523")
                        .addData("1")
                        .addData("1212")
                        .addData("TICK TOCK")
                        .addData("...")
                    .done()                 // 1.1.9 end
                    .addData("1001")        // 1.1.10
                    .addData("537058562")   // 1.1.11
                    .addData("0")           // 1.1.12
                    .addData("8")           // 1.1.17
                .done()         // 1.1 end
                .addSubPacket() // 1.2 begin
                    .addData("1")           // 1.2.1
                    .addData("0")           // 1.2.2
                    .addData("8")           // 1.2.3
                .done();

        return rootPacket;
    }

    public static void main(String[] args) {
        Packet packet = new PacketReader().readAll();
        System.out.println(packet);
    }
}
