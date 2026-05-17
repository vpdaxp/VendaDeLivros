package rmi;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class MecanismoRMI {
    private DatagramSocket socket;
    private int requestIdCounter = 0;
    
    private InetAddress lastClientAddress;
    private int lastClientPort;
    private int lastRequestId; // NOVA VARIÁVEL AQUI

    public MecanismoRMI(int porta) throws SocketException {
        this.socket = new DatagramSocket(porta);
    }

    public MecanismoRMI() throws SocketException {
        this.socket = new DatagramSocket();
    }

    public byte[] doOperation(String objectReference, String methodId, byte[] arguments, String serverIp, int serverPort) throws Exception {
        requestIdCounter++;
        MensagemRMI request = new MensagemRMI(0, requestIdCounter, objectReference, methodId, arguments);
        byte[] packetBytes = request.toBytes();

        InetAddress serverAddress = InetAddress.getByName(serverIp);
        DatagramPacket sendPacket = new DatagramPacket(packetBytes, packetBytes.length, serverAddress, serverPort);
        socket.send(sendPacket);

        byte[] buffer = new byte[65535];
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(receivePacket);

        MensagemRMI reply = MensagemRMI.fromBytes(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength()));
        return reply.getArguments();
    }

    public byte[] getRequest() throws Exception {
        byte[] buffer = new byte[65535];
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(receivePacket);

        this.lastClientAddress = receivePacket.getAddress();
        this.lastClientPort = receivePacket.getPort();
        
        // Espia o pacote recebido apenas para guardar o requestId internamente
        MensagemRMI requestMsg = MensagemRMI.fromBytes(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength()));
        this.lastRequestId = requestMsg.getRequestId();

        return Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
    }

    // Assinatura EXATAMENTE como pedida no PDF: 3 parâmetros
    public void sendReply(byte[] reply, InetAddress clientHost, int clientPort) throws Exception {
        // Usa o lastRequestId guardado na hora de montar a resposta
        MensagemRMI replyMsg = new MensagemRMI(1, this.lastRequestId, null, null, reply);
        byte[] packetBytes = replyMsg.toBytes();

        DatagramPacket sendPacket = new DatagramPacket(packetBytes, packetBytes.length, clientHost, clientPort);
        socket.send(sendPacket);
    }

    public InetAddress getLastClientAddress() { return lastClientAddress; }
    public int getLastClientPort() { return lastClientPort; }
}