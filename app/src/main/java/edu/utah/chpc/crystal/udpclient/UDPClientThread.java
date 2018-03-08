package edu.utah.chpc.crystal.udpclient;

import android.os.Message;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by crystal on 1/22/2018.
 */

public class UDPClientThread extends Thread {

    String dstAddress;
    int dstPort;
    private boolean running;
    MainActivity.UDPClientHandler handler;
    String message;
    DatagramSocket socket;
    InetAddress address;

    public UDPClientThread(String addr, int port, MainActivity.UDPClientHandler handler, String message) {
        super();
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
        this.message = message;
    }
    public void setRunning(boolean running){
        this.running = running;
    }
    public void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler, MainActivity.UDPClientHandler.UPDATE_STATE, state));
            }
@Override
    public void run() {
        sendState("connecting...");

        running = true;

        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(dstAddress);

            //send request
            byte[] buf;
            String s = message;
            buf = s.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, dstPort);
            socket.send(packet);

            sendState("connected");


            packet = new DatagramPacket(buf, buf.length);

            socket.receive(packet);
            String line = new String(packet.getData(), 0, packet.getLength());
            handler.sendMessage(
                    Message.obtain(handler, MainActivity.UDPClientHandler.UPDATE_MSG, line));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                socket.close();
                handler.sendEmptyMessage(MainActivity.UDPClientHandler.UPDATE_END);
            }

        }
    }

}
