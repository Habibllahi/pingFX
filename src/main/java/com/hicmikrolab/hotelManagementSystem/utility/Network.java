/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.utility;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Data
public class Network implements NetworkInterface {

    /**
     * port value is assign initial value.
     */
    @Value("${com.hicmikrolab.hotelManagementSystem.socket.port}")
    private int port;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    /**
     * <p>
     *     Test whether the ip address is reachable. Best effort is made by the implementation to try to reach the host,
     *     but firewalls and server configuration may block requests resulting in a unreachable status while some specific
     *     ports may be accessible.
     *     A typical implementation will use ICMP ECHO REQUESTTs if the privilege can be obtained, otherwise it will try
     *     to establish a TCP connection on port 7 (Echo) of the destination host.
     *
     *     The timeout value is 5000 milliseconds
     * </p>
     * @param ip is the string representation of the node ip address
     * @return boolean. True means node is online and false means node is offline
     * @throws IOException
     */
    @Override
    public boolean sendPingRequest(String ip) {
        boolean reachable = false;
        try {
            var inetAddress = getNodeIp(ip);
            reachable =  inetAddress.isReachable(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reachable;
    }

    /**
     * Returns the address of the local host. This is achieved by retrieving the name of the host from the system,
     * then resolving that name into an InetAddress.
     * @return InetAddress of the Local host
     * @throws IOException
     */
    @Override
    public InetAddress getLocalHostIp()throws IOException{
        return InetAddress.getLocalHost();
    }

    /**
     * Determines the IP address of a node
     * @param ip string literal of the node ip address
     * @return InetAddress of the node
     * @throws IOException
     */
    @Override
    public InetAddress getNodeIp(String ip) throws IOException{
        return InetAddress.getByName(ip);
    }

    /**
     * <p>
     *     To write to the node a message. This method employs Low level API network communication to the node
     * </p>
     * @param ip is the string representation of the node's ip address
     * @param port is port number for the node socket
     * @param message is the message to send to the node
     */
    @Override
    public boolean lowLevelWriteAndRead(String ip, int port, String message){
        var isReadSuccessful = false; //An indicator to flag if something was returned
        try(var channel = SocketChannel.open(new InetSocketAddress(getNodeIp(ip),port))){
                channel.configureBlocking(false);
                //System.out.println("Node online, will commence writing");
                var buffer = ByteBuffer.allocateDirect(16*1024);
                buffer.clear();
                buffer.put(message.getBytes()).flip(); //assign data in to the buffer for channel to write
                while (buffer.hasRemaining()){
                    channel.write(buffer);
                    //System.out.println("byte written"); //Just for debug purpose
                }

                //now lets us listen to result upon successful write
                if(channel.isConnected()){
                    //System.out.println("Channel is still connected, will commence reading");
                    buffer.clear();
                    var bytesRead =  channel.read(buffer); //the channel assign incoming data in to the readBuffer and return the number of byte
                    //System.out.println("--------------");
                    if(bytesRead>0){
                        //System.out.println("Channel Read something, will proceed to writing on console");
                        isReadSuccessful = true; // response received
                        buffer.flip();
                        while (buffer.hasRemaining()){
                            //System.out.print((char)buffer.get()); //print response

                        }
                    }else{
                        //System.out.println("Nothing was read from channel");
                    }
                }
        }catch (IOException e){
            e.printStackTrace();
        }
        return isReadSuccessful;
    }


    public String httpClientApproach(String ip, int port, String message){
        String result = "FAILED";
        try{
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://"+ip+":"+port+message))
                    .setHeader("User-Agent", "HIC MikroLab")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            // print response headers
            HttpHeaders headers = response.get().headers();
            headers.map().forEach((k, v) -> System.out.println(k + ":" + v));

            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);

            //System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
