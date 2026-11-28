package com.marte.ui.console;

import com.marte.application.Missao;
import com.marte.domain.Asteroide;
import com.marte.domain.Astronauta;
import com.marte.domain.Dificuldade;
import com.marte.domain.Engenheiro;
import com.marte.domain.Inimigo;
import com.marte.domain.Nave;
import com.marte.domain.Passageiro;
import com.marte.domain.Professor;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random random = new Random();

        System.out.println("=== MISSAO MARTE UNIFOR ===");
        System.out.print("Dificuldade (FACIL / MEDIO / DIFICIL): ");
        String entradaDif = sc.nextLine();
        Dificuldade dificuldade = Dificuldade.deString(entradaDif);

        int radius = 5;
        Nave nave = new Nave("A-1", 5);
        Missao missao = new Missao(nave);

        int score = (dificuldade == Dificuldade.FACIL) ? 30 : (dificuldade == Dificuldade.DIFICIL ? 45 : 20);
        int totalPax = (dificuldade == Dificuldade.FACIL) ? 4 : 5;
        int hazards = (dificuldade == Dificuldade.FACIL) ? 1 : (dificuldade == Dificuldade.DIFICIL ? 3 : 2);

        while (missao.getPassageiros().size() < totalPax) {
            int x = random.nextInt(radius * 2 + 1) - radius;
            int y = random.nextInt(radius * 2 + 1) - radius;
            if ((x == 0 && y == 0) || occupied(missao, x, y)) continue;
            int i = missao.getPassageiros().size();
            if (i % 5 == 4) missao.addPassageiro(new Astronauta("Ast. Maria", x, y));
            else if (i % 2 == 1) missao.addPassageiro(new Engenheiro("Eng. " + i, x, y));
            else missao.addPassageiro(new Professor("Prof. " + i, x, y));
        }
        while (missao.getAsteroides().size() < hazards) {
            int x = random.nextInt(radius * 2 + 1) - radius;
            int y = random.nextInt(radius * 2 + 1) - radius;
            if ((x == 0 && y == 0) || occupied(missao, x, y)) continue;
            missao.addAsteroide(new Asteroide(x, y));
        }
        while (missao.getInimigos().size() < hazards) {
            int x = random.nextInt(radius * 2 + 1) - radius;
            int y = random.nextInt(radius * 2 + 1) - radius;
            if ((x == 0 && y == 0) || occupied(missao, x, y)) continue;
            missao.addInimigo(new Inimigo(x, y));
        }

        while (nave.getVidas() > 0 && score > 0 && !missao.vitoriaAlcancada()) {
            System.out.printf("Pos: (%d,%d) | Dif: %s | Pts: %d | Vidas: %d | A bordo: %d/%d%n",
                    nave.getX(), nave.getY(), dificuldade, score, nave.getVidas(),
                    nave.getPassageiros().size(), nave.getCapacidade());
            System.out.println("Passageiros restantes: " + missao.getPassageiros().size());
            System.out.print("Comando (w/a/s/d mover, c embarcar, q sair): ");
            String cmd = sc.nextLine().trim().toLowerCase();

            if (cmd.equals("q")) break;
            if (cmd.equals("w") || cmd.equals("a") || cmd.equals("s") || cmd.equals("d")) {
                nave.moverComLimites(cmd.charAt(0), -radius, radius, -radius, radius);
                score--;
            } else if (cmd.equals("c")) {
                Passageiro p = missao.passagemNaPosicao();
                if (p != null && missao.embarcarPassageiroNaPosicao()) {
                    score += p.getPontuacao();
                    System.out.println("Resgatou " + p.getNome() + "!");
                } else {
                    System.out.println("Nada para embarcar aqui.");
                }
            }

            missao.moverInimigos(random, -radius, radius, -radius, radius);
            if (missao.verificaColisao()) {
                nave.perderVida();
                System.out.println("Colisao! Vidas restantes: " + nave.getVidas());
            }
        }

        if (missao.vitoriaAlcancada()) System.out.println("Vitoria! Pontuacao final: " + score);
        else System.out.println("Fim de jogo. Pontuacao final: " + score);
        sc.close();
    }

    private static boolean occupied(Missao missao, int x, int y) {
        Nave n = missao.getNave();
        if (n.getX() == x && n.getY() == y) return true;
        for (Passageiro p : missao.getPassageiros()) if (p.getX() == x && p.getY() == y) return true;
        for (Asteroide a : missao.getAsteroides()) if (a.getX() == x && a.getY() == y) return true;
        for (Inimigo i : missao.getInimigos()) if (i.getX() == x && i.getY() == y) return true;
        return false;
    }
}
