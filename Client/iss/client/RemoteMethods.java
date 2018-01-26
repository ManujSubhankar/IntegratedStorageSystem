package iss.client;
import java.net.*;

public class RemoteMethods {
	
public static final int PORT = 9; 
	
	public static void InvokeRemoteBoot(byte[] macBytes,String broadcastaddress){
        try {
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }
            
            InetAddress address = InetAddress.getByName(broadcastaddress);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
        }
        catch (Exception e) {
           // System.out.println("Failed to send Wake-on-LAN packet: " + e.getMessage());
        }
	}
}
