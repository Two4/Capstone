package za.ac.mandela.WRPV301.Capstone.Map.Access;

import com.google.common.collect.ImmutableList;
import za.ac.mandela.WRPV301.Capstone.Action.ActionBuilder;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableRunnable;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Item.Player.PlayerItem;
import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;
import za.ac.mandela.WRPV301.Capstone.Event.AccesswayChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Player;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.*;

/**
 * Abstract class to represent {@link Accessway}s that can be locked
 */
public abstract class Lockable extends Accessway {
    /**
     * The {@link Lock} for this Lockable instance
     */
    private final Lock lock;
    /**
     * The {@link Material} this lockable is made from
     */
    protected final Material material;

    /**
     * Constructor
     * @param size enum value describing the size of this Lockable
     * @param material the {@link Material} this Lockable is made from
     * @param lock the {@link Lock} for this Lockable instance
     * @param isOpen whether this Lockable is open or not, i.e. can be traversed by the player to another {@link MapLocation}
     */
    protected Lockable(Size size, Material material, Lock lock, boolean isOpen) {
        super(size, isOpen);
        this.lock = lock;
        this.material = material;
        addAction(ActionBuilder.newInstance()
                .setAliases("Unlock")
                .setDescriptor(() -> String.format("Unlock the locked %s to the %s", getSingleWordDescription(), getFacingDirection()))
                .setFeedbackProvider(this::unlockFeedback)
                .setActivator(this::keyExists)
                .setActionRunnable(this::unlock)
                .build()
        );
        addAction(ActionBuilder.newInstance()
                .setAliases("Open")
                .setDescriptor(() -> String.format("Open the %s to the %s", getSingleWordDescription(), getFacingDirection()))
                .setFeedbackProvider(() -> String.format("You open the %s", getSingleWordDescription()))
                .setActivator(this::canOpen)
                .setActionRunnable(this::open)
                .build()
        );
    }

    /**
     * @return a random locked Lockable instance, i.e. a locked {@link Door} or {@link Gate} instance
     */
    public static Lockable randomLocked() {
        return coin() ? Door.randomLocked() : Gate.randomLocked();
    }

    /**
     * @return the {@link Lock} of this Lockable
     */
    public Lock getLock() {
        return lock;
    }

    /**
     * @return whether the player can open this Lockable; if it is locked, the player must have the key.
     */
    public boolean canOpen() {
        return !isOpen() && (getLock().isUnlocked() || getLock().canUnlock());
    }

    /**
     * @return true if this Lockable has an existing key
     */
    public boolean keyExists() {
        return Objects.nonNull(getLock().getKey());
    }

    /**
     * Set this Lockable to the open state
     */
    protected void open() {
        if (getLock().isLocked()) {
            unlock();
        }
        isOpen = true;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean canEnter() {
        return isOpen() || canOpen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter() {
        if (!isOpen()) {
            open();
        }
        super.enter();
    }

    /**
     * Unlocks this Lockable
     */
    private void unlock() {
        if (Game.getPlayer().hasItem(getLock().getKey())) {
            getLock().unlock();
            AccesswayChangeEvent.on(this);
        }
    }

    /**
     * @return true if this Lockable is unlocked
     */
    public boolean isUnlocked() {
        return getLock().isUnlocked();
    }

    /**
     * @return a feedback string for when this Lockable is unlocked
     */
    private String unlockFeedback() {
        if (getLock().canUnlock()) {
            return String.format("You unlock the %s to the %s", getSingleWordDescription(), getFacingDirection());
        }
        else return "You do not have the key.";
    }

    /**
     * Enum for values describing the material a Lockable can be made from; for fluff only, does not
     */
    @SuppressWarnings("JavaDoc")
    protected enum Material {
        STEEL,
        WOOD,
        IRON,
        STONE;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<Material> values = ImmutableList.copyOf(values());
        /**
         * Statically stored size of this enum
         */
        private static final int size = values.size();

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        /**
         * @return a random value instance of this enum
         */
        public static Material random() {
            return values.get(d(size));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder(String.format("A %s %s, facing %s, ", size, getSingleWordDescription(), getFacingDirection()));
        if (isOpen()) {
            stringBuilder.append("currently open and unlocked.");
        } else {
            stringBuilder.append(String.format("currently closed and %s", getLock().isLocked() ? "locked. " : "unlocked. "));
        }
        stringBuilder.append(String.format("It is seemingly made of %s. ", material));
        if (canPeek()) {
            getOpposingMapLocation().setVisible(true);
            stringBuilder.append(String.format("Through this %s, you can see %s.", getSingleWordDescription(), getOpposingMapLocation().getShortDescription()));
        }
        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        String lockableState;
        if (isOpen()) {
            lockableState = "open";
        } else if (getLock().isLocked()) {
            lockableState = "locked";
        } else {
            lockableState = "closed";
        }
        return String.format("%s, %s, %s %s", size, lockableState, material, getSingleWordDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashSet<String> getAdjectives() {
        HashSet<String> adjectives = super.getAdjectives();
        adjectives.add(material.toString());
        adjectives.add(isOpen? "open" : "closed");
        if (getLock().isLocked()) {
            adjectives.add("locked");
        }
        return adjectives;
    }

    /**
     * Internal class to represent a Lockable lock
     */
    public static class Lock implements Serializable {
        /**
         * The {@link Key} associated with this Lock
         */
        private final Key key;
        /**
         * The lock state
         */
        private boolean isLocked;

        /**
         * Constructor
         * @param key the {@link Key} associated with this Lock
         * @param isLocked the lock state
         */
        Lock(Key key, boolean isLocked) {
            this.key = key;
            this.isLocked = isLocked;
        }

        /**
         * @return an unlocked Lock with a random {@link Key}
         */
        static Lock unlocked() {
            return new Lock(null, false);
        }

        /**
         * @return an locked Lock with a random {@link Key}
         */
        static Lock randomLocked() {
            return new Lock(Key.randomKey(), true);
        }

        /**
         * @return if this Lock can be unlocked by the current {@link Player}
         */
        boolean canUnlock() {
            return !isLocked && playerHasKey();
        }

        /**
         * @return true if this Lock is locked
         */
        public boolean isLocked() {
            return isLocked;
        }

        /**
         * @return true if this Lock is unlocked
         */
        boolean isUnlocked() {
            return !isLocked;
        }

        /**
         * sets the Lock state to unlocked
         */
        void unlock() {
            if (playerHasKey()) {
                isLocked = false;
            }
        }

        /**
         * @return true if the {@link Player#inventory} contains the {@link Key} to this Lock
         */
        private boolean playerHasKey() {
            return Game.getPlayer().hasItem(key);
        }

        /**
         * @return the Key to this lock
         */
        public Key getKey() {
            return key;
        }

        /**
         * Internal class to represent the Key for a {@link Lock}
         */
        public static class Key extends PlayerItem {
            /**
             * {@link Size} enum value for the size description of this Key
             */
            private final Size size;
            /**
             * {@link Material} enum value for the size description of this Key
             */
            private final Material material;
            /**
             * {@link Quality} enum value for the size description of this Key
             */
            private final Quality quality;

            /**
             *
             * @param size {@link Size} enum value for the size description of this Key
             * @param material {@link Material} enum value for the size description of this Key
             * @param quality {@link Quality} enum value for the size description of this Key
             */
            private Key(Size size, Material material, Quality quality) {
                this.size = size;
                this.material = material;
                this.quality = quality;
            }

            /**
             * @return a new Key instance populated with random enum values
             */
            public static Key randomKey() {
                return new Key(Size.random(), Material.random(), Quality.random());
            }

            /**
             * Enum with values describing Key size
             */
            @SuppressWarnings("JavaDoc")
            public enum Size {
                SMALL,
                MEDIUM,
                LARGE,
                HUGE;

                /**
                 * Static list of enum values, used to generate random values statically
                 */
                private static final ImmutableList<Size> values = ImmutableList.copyOf(Size.values());
                /**
                 * Statically stored size of this enum
                 */
                private static final int size = values.size();

                /**
                 * {@inheritDoc}
                 */
                @Override
                public String toString() {
                    return this.name().toLowerCase();
                }

                /**
                 * @return a random value instance of this enum
                 */
                public static Size random() {
                    return values.get(d(size));
                }
            }

            /**
             * Enum with values describing Key material
             */
            @SuppressWarnings("JavaDoc")
            private enum Material {
                IRON,
                BRASS,
                LEAD,
                WOOD,
                GLASS;

                /**
                 * Static list of enum values, used to generate random values statically
                 */
                private static final ImmutableList<Material> values = ImmutableList.copyOf(Material.values());
                /**
                 * Statically stored size of this enum
                 */
                private static final int size = values.size();

                /**
                 * {@inheritDoc}
                 */
                @Override
                public String toString() {
                    return this.name().toLowerCase();
                }

                /**
                 * @return a random value instance of this enum
                 */
                public static Material random() {
                    return values.get(d(size));
                }
            }

            /**
             * Enum with values describing Key quality
             */
            @SuppressWarnings("JavaDoc")
            private enum Quality {
                DIRTY,
                WORN,
                PLAIN,
                EMBELLISHED,
                FANCY;

                /**
                 * Static list of enum values, used to generate random values statically
                 */
                private static final ImmutableList<Quality> values = ImmutableList.copyOf(Quality.values());
                /**
                 * Statically stored size of this enum
                 */
                private static final int size = values.size();

                /**
                 * {@inheritDoc}
                 */
                @Override
                public String toString() {
                    return this.name().toLowerCase();
                }

                /**
                 * @return a random value instance of this enum
                 */
                public static Quality random() {
                    return values.get(d(size));
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getDescription() {
                return String.format("A %s key, %s in size, seemingly made of %s.", quality, size, material);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getShortDescription() {
                return String.format("%s, %s %s key", size, quality, material);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getSingleWordDescription() {
                return "key";
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public HashSet<String> getAdjectives() {
                HashSet<String> adjectives = new HashSet<>();
                adjectives.add(size.toString());
                adjectives.add(material.toString());
                adjectives.add(quality.toString());
                return adjectives;
            }
        }
    }
}
