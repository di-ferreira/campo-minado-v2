package io.github.diferreira.cm.modelo;

import java.util.ArrayList;
import java.util.List;

public class Campo {
    private final int linha;
    private final int coluna;
    private final List<Campo> vizinhos = new ArrayList<>();
    //List de interface propria
    private final List<CampoObservador> observadores = new ArrayList<>();
    private boolean aberto;
    private boolean minado;
    private boolean marcado;
    //List com interface padrão
    //private List<BiConsumer<Campo, CampoEvento>> observadores2 = new ArrayList<>();


    public Campo(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public void registrarObservador(CampoObservador observador) {
        observadores.add(observador);
    }

    private void notificarObservadores(CampoEvento evento) {
        observadores.forEach(o -> o.eventoOcorreu(this, evento));
    }

    void adicionarVizinho(Campo vizinho) {

        boolean linhaDiferente = linha != vizinho.linha;
        boolean colunaDiferente = coluna != vizinho.coluna;
        boolean diagonal = linhaDiferente && colunaDiferente;

        int deltaLinha = Math.abs(linha - vizinho.linha);
        int deltaColuna = Math.abs(coluna - vizinho.coluna);
        int deltaGeral = deltaColuna + deltaLinha;

        if (deltaGeral == 1) {
            vizinhos.add(vizinho);
        } else if (deltaGeral == 2 && diagonal) {
            vizinhos.add(vizinho);
        }

    }

    public void alternarMarcacao() {
        if (!aberto) {
            marcado = !marcado;

            if (marcado) {
                notificarObservadores(CampoEvento.MARCAR);
            } else {
                notificarObservadores(CampoEvento.DESMARCAR);
            }
        }
    }

    public void abrir() {
        if (!aberto && !marcado) {
            if (minado) {
                notificarObservadores(CampoEvento.EXPLODIR);
                return;
            }

            setAberto();

            notificarObservadores(CampoEvento.ABRIR);

            if (vizinhancaSegura()) {
                vizinhos.forEach(Campo::abrir);
            }
        }

    }

    public boolean vizinhancaSegura() {
        return vizinhos.stream().noneMatch(v -> v.minado);
    }

    public boolean isMarcado() {
        return marcado;
    }

    void setAberto() {
        this.aberto = true;

        notificarObservadores(CampoEvento.ABRIR);
    }


    void minar() {
        minado = true;
    }

    public boolean isMinado() {
        return minado;
    }


    boolean objetivoAlcancado() {
        boolean desvendado = !minado && aberto;
        boolean protegido = minado && marcado;
        return desvendado || protegido;
    }

    public int minasNaVizinhanca() {
        return (int) vizinhos.stream().filter(v -> v.minado).count();
    }

    void reiniciar() {
        aberto = false;
        minado = false;
        marcado = false;
        notificarObservadores(CampoEvento.REINICIAR);
    }

}
