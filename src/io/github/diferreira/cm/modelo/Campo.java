package io.github.diferreira.cm.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Campo {
    private final int linha;
    private final int coluna;

    private boolean aberto;
    private boolean minado;
    private boolean marcado;

    private final List<Campo> vizinhos = new ArrayList<>();

    //List de interface propria
    private final List<CampoObservador> observadores = new ArrayList<>();
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
        observadores.stream().forEach(o -> o.eventoOcorreu(this, evento));
    }

    boolean adicionarVizinho(Campo vizinho) {

        boolean linhaDiferente = linha != vizinho.linha;
        boolean colunaDiferente = coluna != vizinho.coluna;
        boolean diagonal = linhaDiferente && colunaDiferente;

        int deltaLinha = Math.abs(linha - vizinho.linha);
        int deltaColuna = Math.abs(coluna - vizinho.coluna);
        int deltaGeral = deltaColuna + deltaLinha;

        if (deltaGeral == 1 && !diagonal) {
            vizinhos.add(vizinho);
            return true;
        } else if (deltaGeral == 2 && diagonal) {
            vizinhos.add(vizinho);
            return true;
        } else {
            return false;
        }

    }

    void alternarMarcacao() {
        if (!aberto) {
            marcado = !marcado;

            if (marcado) {
                notificarObservadores(CampoEvento.MARCAR);
            } else {
                notificarObservadores(CampoEvento.DESMARCAR);
            }
        }
    }

    boolean abrir() {
        if (!aberto && !marcado) {
            if (minado) {
                notificarObservadores(CampoEvento.EXPLODIR);
                return true;
            }

            setAberto(true);

            notificarObservadores(CampoEvento.ABRIR);

            if (vizinhancaSegura()) {
                vizinhos.forEach(v -> v.abrir());
            }
            return true;
        } else {
            return false;
        }

    }

    boolean vizinhancaSegura() {
        return vizinhos.stream().noneMatch(v -> v.minado);
    }

    public boolean isMarcado() {
        return marcado;
    }

    public boolean isAberto() {
        return aberto;
    }

    void setAberto(boolean aberto) {
        this.aberto = aberto;

        if (aberto){
            notificarObservadores(CampoEvento.ABRIR);
        }
    }

    public boolean isFechado() {
        return !isAberto();
    }

    void minar() {
        minado = true;
    }

    public boolean isMinado() {
        return minado;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    boolean objetivoAlcancado() {
        boolean desvendado = !minado && aberto;
        boolean protegido = minado && marcado;
        return desvendado || protegido;
    }

    long minasNaVizinhanca() {
        return vizinhos.stream().filter(v -> v.minado).count();
    }

    void reiniciar() {
        aberto = false;
        minado = false;
        marcado = false;
    }

}
