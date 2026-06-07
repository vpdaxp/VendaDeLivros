package io;

import model.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PojoInputStream extends InputStream {
    private InputStream origem;

    /**
     * Construtor conforme o Exercício 3.a.
     * @param origem InputStream de onde as sequências de bytes serão lidas.
     */
    public PojoInputStream(InputStream origem) {
        this.origem = origem;
    }

    @Override
    public int read() throws IOException {
        return origem.read();
    }

    public List<Produto> lerDados() throws IOException {
        DataInputStream dis = new DataInputStream(origem);
        List<Produto> listaReconstruida = new ArrayList<>();


        int numObjetos = dis.readInt();

        for (int i = 0; i < numObjetos; i++) {

            int tipo = dis.readInt();
            
            int tamanhoBytes = dis.readInt();

            Produto p;
            

            if (tipo == 1) p = new LivroFisico();
            else if (tipo == 2) p = new LivroDigital();
            else p = new LivroColecionavel();


            p.setId(dis.readInt());
            p.setTitulo(dis.readUTF());
            p.setDescricao(dis.readUTF());
            p.setCategoria(dis.readUTF());
            p.setPreco(dis.readDouble());
            p.setAutor(dis.readUTF());


            preencherDadosEspecificos(dis, p, tipo);
            
            listaReconstruida.add(p);
        }

        return listaReconstruida;
    }

    private void preencherDadosEspecificos(DataInputStream dis, Produto p, int tipo) throws IOException {
        if (tipo == 1) { // LivroFisico
            LivroFisico lf = (LivroFisico) p;
            lf.setPeso(dis.readDouble());
            lf.setEstoque(dis.readInt());
            lf.setTipoDeCapa(dis.readUTF());
        } else if (tipo == 2) { // LivroDigital
            LivroDigital ld = (LivroDigital) p;
            ld.setFormato(dis.readUTF());
            ld.setTamanho(dis.readDouble());
            ld.setLinkDownload(dis.readUTF());
        } else if (tipo == 3) { // LivroColecionavel
            LivroColecionavel lc = (LivroColecionavel) p;
            lc.setEstado(dis.readUTF());
            lc.setSerie(dis.readUTF());
            lc.setAutrografado(dis.readBoolean());
        }
    }
}