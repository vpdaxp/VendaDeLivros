package io;

import model.*;
import java.io.*;
import java.util.List;

public class SerializadorManual {
    
    public static byte[] listaProdutosParaBytes(List<Produto> lista) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PojoOutputStream pos = new PojoOutputStream(lista.toArray(new Produto[0]), lista.size(), baos);
        pos.enviarDados();
        return baos.toByteArray();
    }

    public static List<Produto> bytesParaListaProdutos(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        PojoInputStream pis = new PojoInputStream(bais);
        return pis.lerDados();
    }

    public static byte[] stringParaBytes(String texto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(texto);
        dos.flush();
        return baos.toByteArray();
    }

    public static String bytesParaString(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        return dis.readUTF();
    }

    public static byte[] intParaBytes(int valor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(valor);
        dos.flush();
        return baos.toByteArray();
    }

    public static int bytesParaInt(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        return dis.readInt();
    }

    public static byte[] produtoParaBytes(Produto p) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PojoOutputStream pos = new PojoOutputStream(new Produto[]{p}, 1, baos);
        pos.enviarDados();
        return baos.toByteArray();
    }

    public static Produto bytesParaProduto(byte[] bytes) throws IOException {
        List<Produto> lista = bytesParaListaProdutos(bytes);
        return lista.isEmpty() ? null : lista.get(0);
    }
}