package za.ac.mandela.WRPV301.Capstone.Util;

import javafx.scene.image.Image;
import za.ac.mandela.WRPV301.Capstone.Combat.Ghost;
import za.ac.mandela.WRPV301.Capstone.Combat.Humanoid;
import za.ac.mandela.WRPV301.Capstone.Combat.Insect;
import za.ac.mandela.WRPV301.Capstone.Item.Player.HealthPotion;
import za.ac.mandela.WRPV301.Capstone.Item.Room.Corpse;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Armour.Armour;
import za.ac.mandela.WRPV301.Capstone.Item.Room.Crown;
import za.ac.mandela.WRPV301.Capstone.Item.Room.HealthChest;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Weapon.Weapon;
import za.ac.mandela.WRPV301.Capstone.Main;
import za.ac.mandela.WRPV301.Capstone.Map.Access.Lockable;
import za.ac.mandela.WRPV301.Capstone.Player;

import java.util.HashMap;

/**
 * Enum utility to map static image resources to class instances
 */
@SuppressWarnings("JavaDoc")
public enum ResourceMapping {
    KEY(Lockable.Lock.Key.class, "key.png"),
    PLAYER(Player.class, "user.png"),
    WEAPON(Weapon.class, "sword.png"),
    ARMOUR(Armour.class, "shield.png"),
    HEALTH_CHEST(HealthChest.class, "first-aid.png"),
    CORPSE(Corpse.class, "skull.png"),
    INSECT(Insect.class, "bug.png"),
    GHOST(Ghost.class, "ghost.png"),
    HUMANOID(Humanoid.class, "alien.png"),
    HEALTH_POTION(HealthPotion.class, "flask.png"),
    CROWN(Crown.class, "crown.png")
    ;

    /**
     * The static Map of the values of this enum
     */
    private final static HashMap<Class<?>, Image> classImageResourceMap = populateImageResourceMap();

    /**
     * The class mapping for the resource
     */
    private final Class<?> clazz;
    /**
     * The resource associated with the class
     */
    private final String resourceName;

    ResourceMapping(Class<?> clazz, String resourceName) {
        this.clazz = clazz;
        this.resourceName = resourceName;
    }

    /**
     * Gets The resource associated with the class.
     *
     * @return Value of The resource associated with the class.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Gets The class mapping for the resource.
     *
     * @return Value of The class mapping for the resource.
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * @return the static Map of the values of this enum
     */
    private static HashMap<Class<?>, Image> populateImageResourceMap() {
        HashMap<Class<?>, Image> resourceMap = new HashMap<>();
        for (ResourceMapping resourceMapping : ResourceMapping.values()) {
            resourceMap.put(resourceMapping.getClazz(), new Image(Main.class.getResourceAsStream(resourceMapping.getResourceName())));
        }
        return resourceMap;
    }

    /**
     * Returns the {@link Image} resource associated with the {@link Class} of the given Object
     * @param object an Object for which to get the {@link Class}-associated {@link Image} resource
     * @return the {@link Image} resource associated with the {@link Class} of the given Object
     */
    public static Image of(Object object) {
        for (Class<?> clazz : classImageResourceMap.keySet()) {
            if (clazz.isInstance(object)) {
                return classImageResourceMap.get(clazz);
            }
        }
        return classImageResourceMap.get(object.getClass());
    }

}
