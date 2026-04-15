package io;

import model.Produto;
import model.LivroFisico;
import model.LivroDigital;
import model.LivroColecionavel;
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

            if (p instanceof LivroFisico) dos.writeInt(1);
            else if (p instanceof LivroDigital) dos.writeInt(2);
            else if (p instanceof LivroColecionavel) dos.writeInt(3);

            byte[] tituloBytes = p.getTitulo().getBytes();
            int totalBytesAtributos = 4 + 8 + tituloBytes.length;

            dos.writeInt(totalBytesAtributos);

            dos.writeInt(p.getId());
            dos.writeUTF(p.getTitulo());
            dos.writeUTF(p.getDescricao());
            dos.writeUTF(p.getCategoria());
            dos.writeDouble(p.getPreco());
            dos.writeUTF(p.getAutor());

            enviarDadosEspecificos(dos, p);
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