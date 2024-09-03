package Entities.Inanimate;

import Interfaces.SentenceHandler;

public class SentenceMaker {
    private final String NAME;
    private SentenceHandler sentenceHandler;

    public SentenceMaker(SentenceHandler handler) {
        this.NAME = "Рассказчик";
        this.sentenceHandler = handler;
    }

    public String getName() {
        return NAME;
    }

    public void printSentence(){
        sentenceHandler.sentenceOut();
    }
}
