package example.domain.game.configuration;

import java.util.List;

/**
 * Содержит данные для конфигурации игрового мира
 */

public class FieldConfig {
    public final List<String[]> itemsData;
    public final int rows, columns;
    public final List<int[]> spawnPositions;
    public FieldConfig(List<String[]> itemsData, List<int[]> spawnPositions){
        this.spawnPositions = spawnPositions;
        this.itemsData = itemsData;
        rows = itemsData.size();
        columns = itemsData.get(0).length;
    }
}
