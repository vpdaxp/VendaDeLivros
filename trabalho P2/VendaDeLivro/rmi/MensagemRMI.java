package rmi;

import java.io.*;

public class MensagemRMI implements Serializable {
    private int messageType; // 0 = Request, 1 = Reply
    private int requestId;
    private String objectReference;
    private String methodId;
    private byte[] arguments;

    public MensagemRMI(int messageType, int requestId, String objectReference, String methodId, byte[] arguments) {
        this.messageType = messageType;
        this.requestId = requestId;
        this.objectReference = objectReference;
        this.methodId = methodId;
        this.arguments = arguments;
    }

    // Getters
    public int getMessageType() { return messageType; }
    public int getRequestId() { return requestId; }
    public String getObjectReference() { return objectReference; }
    public String getMethodId() { return methodId; }
    public byte[] getArguments() { return arguments; }

    // Métodos utilitários para transformar a mensagem inteira em bytes e vice-versa
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.flush();
        return baos.toByteArray();
    }

    public static MensagemRMI fromBytes(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (MensagemRMI) ois.readObject();
    }
}