package ir.xenoncommunity;

import ir.xenoncommunity.config.Config;
import ir.xenoncommunity.scan.Connection;

public class Main {
    public static void main(String[] args) {
        new Thread(new Connection("hub.madcraft.ir", 25565, new Config(5000, true))).start();
    }
}