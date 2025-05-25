package aki;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class GlobalImageCache {
    private static final Map<String, Image> CACHE = new HashMap<>();

    // 私有化构造函数，禁止实例化
    private GlobalImageCache() {}

    // 获取图片（自动缓存）
    public static Image getImage(String path) {
        if (!CACHE.containsKey(path)) {
            Image image = new Image(GlobalImageCache.class.getResourceAsStream(path));
            CACHE.put(path, image);
        }
        return CACHE.get(path);
    }

    // 清空缓存（可选）
    public static void clearCache() {
        CACHE.clear();
    }
}