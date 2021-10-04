package example.domain.game.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Читаем конфигурационный файл и парсим его в List<String[]>
 * где List - это ряды, а String[] колонки в ряде
 *
 *  на выходее имеем конфигурацию каждой ячейки по отдельности
 */


public class GetFieldConfig {

    public FieldConfig fromFile(File config) throws IOException {
        List<String> strings = Files.readAllLines(config.toPath());
        List<int[]> respawnPositions = getRespawnPositions(strings, 1);

        List<String> fieldData = parseFieldData(strings, 0);
        List<String[]> itemsData = parseFieldData(fieldData, ":");

        FieldConfig fieldConfig = new FieldConfig(itemsData, respawnPositions);
        return fieldConfig;
    }

    private int[] stringArrayToIntArray(String[] array){
        final int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = Integer.parseInt(array[i]);
        }
        return intArray;
    }

    // координаты точек спауна во второй строчке конфиг файла, разделены ';'
    private List<int[]> getRespawnPositions(List<String> strings, final int line){
        return Arrays
                .stream(strings.get(line).split(";"))
                .map(s -> s.split(","))
                .map(this::stringArrayToIntArray)
                .peek(coords->{
                    if (coords.length != 2)
                        throw new IllegalArgumentException(
                                "Ошибка в файле конфигурации, в разделе точек спауна! линия:" + line+1);
                })
                .collect(Collectors.toList());
    }

    // line - стока в файле кофигурации содержит позицию(не индекс) начала и конца карты
    private List<String> parseFieldData(List<String> strings, final int line){
        int[] coords = Arrays
                .stream(strings.get(line).split(":"))
                .limit(2)
                .mapToInt(Integer::valueOf)
                .toArray();
        List<String> fields = new ArrayList<>();
        for (int i = coords[0]-1; i < coords[1]; i++){
            fields.add(strings.get(i));
        }
        return fields;
    }

    // парсим данные каждого элемента карты в отдельный стринг, для более удобной работы
    private List<String[]> parseFieldData(List<String> cgfLines, String separator){
        if (cgfLines.size() == 0) throw new IllegalArgumentException("Поле не может быть пустым!");
        List<String[]> rows = new ArrayList<>(cgfLines.size());
        for(String line : cgfLines){
            // если пустая строка - игнорируем и пропускаем
            if (line.length() == 0) continue;
            String[] columns = line.split(separator);
            rows.add(columns);
        }
        return rows;
    }
}
