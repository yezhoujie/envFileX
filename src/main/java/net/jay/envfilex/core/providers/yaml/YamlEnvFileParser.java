package net.jay.envfilex.core.providers.yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import net.jay.envfilex.core.providers.EnvFileParser;
import org.yaml.snakeyaml.Yaml;

@AllArgsConstructor
public class YamlEnvFileParser implements EnvFileParser {
    private final Yaml yaml;

    @Override
    public Map<String, String> parse(String content) {
        Map<String, Object> yamlMap = yaml.load(content);
        Map<String, String> envMap = new HashMap<>();

        // 处理 YAML 嵌套结构，转换为 ENV 格式
        flattenYamlMap(yamlMap, "", envMap);

        return envMap;
    }

    /**
     * 递归地将嵌套的 YAML 结构转换为扁平的环境变量格式
     *
     * @param yamlMap 原始的嵌套 YAML Map
     * @param prefix 当前路径前缀
     * @param resultMap 结果 Map（存储转换后的环境变量）
     */
    private void flattenYamlMap(Map<String, Object> yamlMap, String prefix, Map<String, String> resultMap) {
        if (yamlMap == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : yamlMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 转换为大写的 ENV 格式键名
            String envKey = prefix.isEmpty()
                ? key.toUpperCase()
                : prefix + "_" + key.toUpperCase();

            if (value instanceof Map) {
                // 处理嵌套 Map
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                flattenYamlMap(nestedMap, envKey, resultMap);
            } else if (value instanceof List) {
                // 处理数组/列表
                handleList((List<?>) value, envKey, resultMap);
            } else {
                // 处理简单值
                resultMap.put(envKey, value != null ? value.toString() : "");
            }
        }
    }

    /**
     * 处理 YAML 中的列表/数组
     *
     * @param list 要处理的列表
     * @param prefix 当前路径前缀
     * @param resultMap 结果 Map
     */
    private void handleList(List<?> list, String prefix, Map<String, String> resultMap) {
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            String indexKey = prefix + "_" + i;

            if (item instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) item;
                flattenYamlMap(nestedMap, indexKey, resultMap);
            } else if (item instanceof List) {
                handleList((List<?>) item, indexKey, resultMap);
            } else {
                resultMap.put(indexKey, item != null ? item.toString() : "");
            }
        }
    }
}
