package Model.CardEffects;

public class Shield extends Effect{

    /**
     * Effekten gör så att kortet tar mindra skada när motståndarens kort attackerar detta kort.
     * T.ex om motstånarens kort har 10 AD, och väljer att attakera detta kortet, tar endast detta kortet 6-skada istället för 10.
     * Mängden reducerad skada som kortet tar borde baseras på en %-mängd av motståndar-kortets AD. Gäller vid varje defense.
     */
    public Shield(){

    }
    @Override
    public void activateEffekt() {

    }
}
