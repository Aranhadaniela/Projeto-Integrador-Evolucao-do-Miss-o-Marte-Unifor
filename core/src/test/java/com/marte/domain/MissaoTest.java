package com.marte.domain;

import com.marte.application.Missao;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissaoTest {

    @Test
    void deveEmbarcarPassageiroNaMesmaPosicao() {
        Nave nave = new Nave("A-1", 3);
        nave.moverComLimites('d', -5, 5, -5, 5);
        Missao missao = new Missao(nave);
        missao.addPassageiro(new Astronauta("Ast. Maria", 1, 0));

        assertTrue(missao.embarcarPassageiroNaPosicao());
        assertTrue(missao.todosEmbarcados());
        assertEquals(1, nave.getPassageiros().size());
    }

    @Test
    void deveDetectarColisaoComAsteroide() {
        Nave nave = new Nave("A-1", 3);
        nave.moverComLimites('s', -5, 5, -5, 5);
        Missao missao = new Missao(nave);
        missao.addAsteroide(new Asteroide(0, 1));

        assertTrue(missao.verificaColisao());
        assertFalse(missao.vitoriaAlcancada());
    }
}
