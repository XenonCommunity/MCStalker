package ir.xenoncommunity.scan;

import ir.xenoncommunity.config.Config;
import ir.xenoncommunity.utils.PacketUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Getter
@Setter
@AllArgsConstructor
public class Connection implements Runnable {

    private String ip;
    private int port;
    private Config config;

    /**
     * Checking the connection to Receive the server data by sending a handshake packet to the server
     */

    @Override
    public void run() {
        try {
            // Create a new socket
            final Socket socket = new Socket();

            // Set socket options
            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);

            // Connect to the specified IP and port with a timeout
            socket.connect(new InetSocketAddress(getIp(), getPort()), config.getTimeOut());

            // Create input and output streams for the socket
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            final DataInputStream in = new DataInputStream(socket.getInputStream());

            // Write the handshake + ping packet to the output stream
            out.write(PacketUtils.createStatusPacket(getIp(), getPort(), 47));
            // Read and print the response from the input stream
            System.out.println(PacketUtils.readString(in).substring(3));

            // Close the socket
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
