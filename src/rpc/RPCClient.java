package rpc;

import groupMembership.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.UUID;

import session.Session;
/**
 * Operation codes:
 * probe: 0
 * get: 1
 * put: 2
 * @author Harrison
 *
 */
public class RPCClient {
   static final int nQ = 1;
   
   /**
    * Return true if probe succeeded, false otherwise
    * @return
    */
   public static boolean probe(String ip, String port) {
      System.out.println("Probing "+ip+":"+port);
      DatagramSocket rpcSocket;
      try {
        rpcSocket = new DatagramSocket();
        String callID = UUID.randomUUID().toString();
        byte[] outBuf = new byte[4096];
        outBuf = (callID + ",0,,").getBytes();
        DatagramPacket sendPkt;
        try {
          sendPkt = new DatagramPacket(outBuf, outBuf.length, InetAddress.getByName(ip), Integer.valueOf(port));
          rpcSocket.send(sendPkt);
        } catch (IOException e1) {
           // TODO Auto-generated catch block
           e1.printStackTrace();
        }
        byte[] inBuf = new byte[4096];
        DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
        try {
          do {
             recvPkt.setLength(inBuf.length);
             rpcSocket.receive(recvPkt);
          } while(!(new String(recvPkt.getData())).split(",")[0].equals(callID));
        } catch (IOException e1) {
          recvPkt = null;
        }
      } catch (SocketException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return false;
      }
      return true;
   }
   
   /**
    * Find session data from SSM servers, return null if not found
    * @return
    */
   public static Session get(Session s) {
      try {
         DatagramSocket rpcSocket = new DatagramSocket();
         String callID = UUID.randomUUID().toString();
         byte[] outBuf = new byte[4096];
         outBuf = (callID + ",1," + s.getSessionID() + "," + s.getVersion()).getBytes();
         for(Server e : s.getLocations()) {
            DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, e.ip, e.port);
            try {
               rpcSocket.send(sendPkt);
            } catch (IOException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }
         }
         byte[] inBuf = new byte[4096];
         DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
         try {
            do {
               recvPkt.setLength(inBuf.length);
               rpcSocket.receive(recvPkt);
            } while(!(new String(recvPkt.getData())).split(",")[0].equals(callID));
         } catch (IOException e1) {
            recvPkt = null;
         }
         String[] response = inBuf.toString().split(",");
         s.setData("count",response[1]);
         s.setData("message",response[2]);
         
      } catch (SocketException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
         return null;
      }

      return s;
   }
   
   /**
    * Send call to several destinations, and return the first NQ responses
    * @return
    */
   public static Session put(Session s, List<Server> servers) {
      try {
         DatagramSocket rpcSocket = new DatagramSocket();
         String callID = UUID.randomUUID().toString();
         byte[] outBuf = new byte[4096];
         outBuf = (callID + ",2," + s.getSessionID() + "," + s.getVersion() + "," + s.getData("count") + "," + s.getData("message")).getBytes();

         for(Server e : servers) {
            DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, e.ip, e.port);
            try {
               rpcSocket.send(sendPkt);
            } catch (IOException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }//TODO what if less than nq servers respond, where is nq?
         }
         int recCount = 0;
         byte[] inBuf = new byte[4096];
         DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
         try {
            do {
               recvPkt.setLength(inBuf.length);
               rpcSocket.receive(recvPkt);
            } while(!(new String(recvPkt.getData())).split(",")[0].equals(callID) || ++recCount < nQ);
         } catch (IOException e1) {
            recvPkt = null;
         }
         
      } catch (SocketException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
         return null;
      }

      return s;
      
   }
   
}
