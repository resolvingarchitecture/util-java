package ra.util;

import java.io.Serializable;
import java.util.Map;

public interface JSONSerializable extends Serializable {
    Map<String, Object> toMap();
    void fromMap(Map<String, Object> m);
    String toJSON();
    void fromJSON(String json);
}
