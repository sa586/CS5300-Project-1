package rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import session.Session;
import session.SessionManager;

public class RPCServer extends Thread {
  boolean setFalseToStop = true;
  
  DatagramSocket rpcSocket;
  int serverPort;
   
   public RPCServer() {
      try {
         rpcSocket = new DatagramSocket();
         serverPort = rpcSocket.getLocalPort();
      } catch (SocketException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public int getPort() {
      return serverPort;
   }
   
   public void run() {
      while(setFalseToStop) {
         byte[] inBuf = new byte[4096];
         DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
         try {
            rpcSocket.receive(recvPkt);
            InetAddress returnAddr = recvPkt.getAddress();
            int returnPort = recvPkt.getPort();
            // here inBuf contains the callID and operationCode
            byte[] outBuf = computeResponse(recvPkt.getData(), recvPkt.getLength());
            // here outBuf should contain the callID
            DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, returnAddr, returnPort);
            rpcSocket.send(sendPkt);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
   
   public void cleanup() {
     setFalseToStop = false;
   }
   
   private byte[] computeResponse(byte[] data, int length) {
      //read data
      Session retrievedSession;
      byte[] returnVal = null;
      String requestString = RPCClient.unmarshal(data);
      System.out.println("Server got data: "+requestString);
      String[] request = requestString.split(",");
      if (request.length < 4){
        return null;
      }
      String callid = request[0];
      int operationType = Integer.parseInt(request[1]);
      String sessionid = request[2];
      String sessionVersion = request[3];
      String response=null;
      
      //compute response
      if (operationType == 0){
        //probe
        response = callid;
      }
      else if (operationType == 1){
        //get call
        retrievedSession = SessionManager.getSessionById(sessionid,sessionVersion);
        response = callid;
        response += "," + retrievedSession.getData("count");
        response += "," + retrievedSession.getData("message");
      }
      else if (operationType == 2){
        //put call
        String count = request[4];
        String message = request[5];
        SessionManager.putSession(sessionid,sessionVersion, count, message);
        response = callid;
      }
      System.out.println("Server responding with: " + response);
      returnVal = RPCClient.marshal(response);
      return returnVal;
   }
}
