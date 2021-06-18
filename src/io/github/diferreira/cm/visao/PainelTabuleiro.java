package io.github.diferreira.cm.visao;

import io.github.diferreira.cm.modelo.Tabuleiro;

import javax.swing.*;
import java.awt.*;

public class PainelTabuleiro extends JPanel {
    public PainelTabuleiro(Tabuleiro tabuleiro) {
        setLayout(new GridLayout(tabuleiro.getLinhas(), tabuleiro.getColunas()));

        tabuleiro.paraCadaCampo(c -> add(new BotaoCampo(c)));
        tabuleiro.registrarObservador(e -> {

            SwingUtilities.invokeLater(() -> {
                if (e.isGanhou()) {
                    JOptionPane.showMessageDialog(this, "Parabéns você ganhou :)");
                } else {
                    JOptionPane.showMessageDialog(this, "Você perdeu :(");
                }

                tabuleiro.reiniciar();
            });
        });

    }
}
