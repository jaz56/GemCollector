package gemcollector.core;

public abstract  class CoreComponent {
    /**
     * Initialise le composant (ex: créer les entités, charger la carte, etc.)
     */
    public abstract void init();

    /**
     * Met à jour le composant (ex: déplacement, collisions, logique du jeu)
     */
    public abstract void update();
}
