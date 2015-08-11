package io.vertx.test.codegen;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

public class ChildInheritingDataObjectConverter {

  public static void fromJson(JsonObject json, ChildInheritingDataObject obj) {
    if (json.getValue("childProperty") instanceof String) {
      obj.setChildProperty((String)json.getValue("childProperty"));
    }
    if (json.getValue("parentProperty") instanceof String) {
      obj.setParentProperty((String)json.getValue("parentProperty"));
    }
  }

  public static void toJson(ChildInheritingDataObject obj, JsonObject json) {
    if (obj.getChildProperty() != null) {
      json.put("childProperty", obj.getChildProperty());
    }
    if (obj.getParentProperty() != null) {
      json.put("parentProperty", obj.getParentProperty());
    }
  }
}