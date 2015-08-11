package io.vertx.test.codegen;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

public class ParentDataObjectConverter {

  public static void fromJson(JsonObject json, ParentDataObject obj) {
    if (json.getValue("parentProperty") instanceof String) {
      obj.setParentProperty((String)json.getValue("parentProperty"));
    }
  }

  public static void toJson(ParentDataObject obj, JsonObject json) {
    if (obj.getParentProperty() != null) {
      json.put("parentProperty", obj.getParentProperty());
    }
  }
}