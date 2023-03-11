package cn.nukkit;

import cn.nukkit.api.PowerNukkitXInternal;
import cn.nukkit.level.Level;

import java.util.HashMap;
import java.util.Map;


@PowerNukkitXInternal
public class LevelManager {
    private final Map<Integer, Level> levels;

    public Map<Integer, Level> getLevels() {
        return levels;
    }

    private Level[] levelArray;

    public Level[] getLevelArray() {
        return levelArray;
    }

    public void setLevelArray(Level[] levelArray) {
        this.levelArray = levelArray;
    }

    private Level defaultLevel = null;

    public Level getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(Level defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public LevelManager() {
        this.levels = new HashMap<Integer, Level>() {
            @Override
            public Level put(Integer key, Level value) {
                Level result = super.put(key, value);
                levelArray = levels.values().toArray(Level.EMPTY_ARRAY);
                return result;
            }

            @Override
            public boolean remove(Object key, Object value) {
                boolean result = super.remove(key, value);
                levelArray = levels.values().toArray(Level.EMPTY_ARRAY);
                return result;
            }

            @Override
            public Level remove(Object key) {
                Level result = super.remove(key);
                levelArray = levels.values().toArray(Level.EMPTY_ARRAY);
                return result;
            }
        };
    }
}