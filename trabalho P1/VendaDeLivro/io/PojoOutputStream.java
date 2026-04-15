package io;

import model.Produto;
import model.LivroFisico;
import model.LivroDigital;
import model.LivroColecionavel;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PojoOutputStream extends OutputStream {
    private Produto[] produtos;
    private int numObjetos;
    private OutputStream destino;

    public PojoOutputStream(Produto[] produtos, int numObjetos, OutputStream destino) {
        this.produtos = produtos;
        this.numObjetos = numObjetos;
        this.destino = destino;
    }

    @Override
    public void write(int b) throws IOException {
        destino.write(b);
    }

    public void enviarDados() throws IOException {
    DataOutputStream dos = new DataOutputStream(destino);
    dos.writeInt(numObjetos);

    for (int i = 0; i < numObjetos; i++) {
        Produto p = produtos[i];

        // 1. Criamos um buffer temporário em memória
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream tempDos = new DataOutputStream(baos);

        // 2. Escrevemos TUDO que compõe o objeto no buffer temporário
        tempDos.writeInt(p.getId());
        tempDos.writeUTF(p.getTitulo());
        tempDos.writeUTF(p.getDescricao());
        tempDos.writeUTF(p.getCategoria());
        tempDos.writeDouble(p.getPreco());
        tempDos.writeUTF(p.getAutor());
        enviarDadosEspecificos(tempDos, p); // Também escreve os específicos
        
        tempDos.flush();
        byte[] objetoEmBytes = baos.toByteArray();

        // 3. Agora enviamos os metadados e o conteúdo real para o destino
        // Identificador de tipo
        if (p instanceof LivroFisico) dos.writeInt(1);
        else if (p instanceof LivroDigital) dos.writeInt(2);
        else if (p instanceof LivroColecionavel) dos.writeInt(3);

        // O tamanho exato do que foi escrito no buffer
        dos.writeInt(objetoEmBytes.length);
        
        // Os dados propriamente ditos
        dos.write(objetoEmBytes);
    }
    dos.flush();
}

    private void enviarDadosEspecificos(DataOutputStream dos, Produto p) throws IOException {
        if (p instanceof LivroFisico) {
            LivroFisico lf = (LivroFisico) p;
            dos.writeDouble(lf.getPeso());
            dos.writeInt(lf.getEstoque());
            dos.writeUTF(lf.getTipoDeCapa());
        } else if (p instanceof LivroDigital) {
            LivroDigital ld = (LivroDigital) p;
            dos.writeUTF(ld.getFormato());
            dos.writeDouble(ld.getTamanho());
            dos.writeUTF(ld.getLinkDownload());
        } else if (p instanceof LivroColecionavel) {
            LivroColecionavel lc = (LivroColecionavel) p;
            dos.writeUTF(lc.getEstado());
            dos.writeUTF(lc.getSerie());
            dos.writeBoolean(lc.isAutrografado());
        }
    }
}