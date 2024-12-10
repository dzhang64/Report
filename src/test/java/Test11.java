import java.io.IOException;
import java.net.*;


public class Test11 {
    public static void main(String[] args) {
        String ipAddress1 = "58.213.147.138";
        String ipAddress2 = "47.106.97.166";
        int serverPort1 = 6050;
        int serverPort2 = 9999;
        byte[] sendData = "0101200000003075000064000000".getBytes();
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(ipAddress1);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort1);
            socket.send(sendPacket);
            byte[] buffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            // 接收数据报
            socket.receive(receivePacket);

            // 获取接收到的数据
            byte[] data = receivePacket.getData();
            int length = receivePacket.getLength();
            String receivedMessage = new String(data, 0, length);

            // 获取发送方的IP地址和端口
            InetAddress address = receivePacket.getAddress();
            int port = receivePacket.getPort();

            // 处理接收到的数据（这里只是打印出来）
            System.out.println("Received message: " + receivedMessage);
            System.out.println("From IP: " + address.getHostAddress());
            System.out.println("From Port: " + port);

            // 关闭DatagramSocket（如果不再需要）
            socket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }






}





