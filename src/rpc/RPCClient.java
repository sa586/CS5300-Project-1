package rpc;

import groupMembership.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

import session.Session;

/**
 * Operation codes: probe: 0 get: 1 put: 2
 * 
 * @author Harrison
 * 
 */
public class RPCClient {
  static final double nQ = 0.25;
  static final double n = 0.5;

  public static String unmarshal(byte[] data) {

    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(data);
      ObjectInput in = new ObjectInputStream(bis);
      String output = (String) in.readObject();
      return output;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

  public static byte[] marshal(String data) {

    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutput out = new ObjectOutputStream(bos);
      out.writeObject(data);
      byte[] output = bos.toByteArray();
      return output;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Return true if probe succeeded, false otherwise
   * 
   * @return
   */
  public static boolean probe(Server s) {
    System.out.println("Probing " + s);
    DatagramSocket rpcSocket;
    try {
      rpcSocket = new DatagramSocket();
      rpcSocket.setSoTimeout(2000); // Timeout after 2 seconds
      String callID = UUID.randomUUID().toString();
      // byte[] outBuf = new byte[4096];

      String outstr = (callID + ",0,0,0");
      byte[] outBuf = RPCClient.marshal(outstr);

      String newstr = RPCClient.unmarshal(outBuf);
      DatagramPacket sendPkt;
      try {
        sendPkt = new DatagramPacket(outBuf, outBuf.length, s.ip, s.port);
        rpcSocket.send(sendPkt);
        System.out.println("Sent packet: " + newstr);
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
        } while (!(RPCClient.unmarshal(recvPkt.getData())).split(",")[0]
            .equals(callID));
      } catch (IOException e1) {
        recvPkt = null;
        return false;
      }
    } catch (SocketException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    System.out.println(s + " Online");
    return true;
  }

  /**
   * Find session data from SSM servers, return null if not found
   * 
   * @return
   */
  public static Session get(Session s) {
    try {
      DatagramSocket rpcSocket = new DatagramSocket();
      rpcSocket.setSoTimeout(2000); // Timeout after 2 seconds
      String callID = UUID.randomUUID().toString();
      String outstr = (callID + ",1," + s.getSessionID() + "," + s.getVersion());
      byte[] outBuf = RPCClient.marshal(outstr);
      System.out.println("Get call sending: " + outstr);
      for (Server e : s.getLocations()) {
        DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length,
            e.ip, e.port);
        try {
          rpcSocket.send(sendPkt);
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
      byte[] inBuf = new byte[4096];
      DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
      String response_str = null;
      recvPkt.setLength(inBuf.length);
      do {
        try {
          rpcSocket.receive(recvPkt);
          response_str = RPCClient.unmarshal(inBuf);

        } catch (IOException e1) {
          e1.printStackTrace();
          return null;
        } 
      } while (response_str == null || response_str.equals("")
          || !(response_str.split(",")[0].equals(callID)));

      System.out.println("Client received response: " + response_str);
      String[] response = response_str.split(",");
      s.setData("count", response[1]);
      s.setData("message", URLDecoder.decode(response[2],"UTF-8"));

    } catch (SocketException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return null;
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return s;
  }

  /**
   * Send call to several destinations, and return the first NQ responses
   * 
   * @return
   */
  public static Session put(Session s) {
    try {
      int numServers = GroupMembership.numServers();
      DatagramSocket rpcSocket = new DatagramSocket();
      rpcSocket.setSoTimeout(2000); // Timeout after 2 seconds
      String callID = UUID.randomUUID().toString();
      String outstr = (callID + ",2," + s.getSessionID() + "," + s.getVersion()
          + "," + s.getData("count") + "," + URLEncoder.encode(s.getData("message"),"UTF-8"));
      byte[] outBuf = RPCClient.marshal(outstr);
      System.out.println("Put call sending: " + outstr);
      
      for (Server e : GroupMembership.getServers((int) Math.ceil(n * numServers))) {
        DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length,
            e.ip, e.port);
        try {
          rpcSocket.send(sendPkt);
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }// TODO what if less than nq servers respond, where is nq?
      }
      s.clearLocations();
      System.out.println("Sent puts, waiting for receive");
      int recCount = 0;
      byte[] inBuf = new byte[4096];
      DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
      do {
        try {
          recvPkt.setLength(inBuf.length);
          rpcSocket.receive(recvPkt);
          String response = RPCClient.unmarshal(inBuf);
          System.out.println("Put client received:" + response);
          if (response.split(",")[0].equals(callID)) {
            recCount++;
            s.addLocation(new Server(recvPkt.getAddress(), recvPkt.getPort()));
          }

          System.out.println(recCount);
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
          return null;

        }
      } while (recCount < (nQ * numServers));

    } catch (SocketException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return null;
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println("Client finished put");
    return s;

  }

}
