package io.vertx.test.codegen;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

public class ChildNotInheritingDataObjectConverter {

  public static void fromJson(JsonObject json, ChildNotInheritingDataObject obj) {
    if (json.getValue("childProperty") instanceof String) {
      obj.setChildProperty((String)json.getValue("childProperty"));
    }
  }

  public static void toJson(ChildNotInheritingDataObject obj, JsonObject json) {
    if (obj.getChildProperty() != null) {
      json.put("childProperty", obj.getChildProperty());
    }
  }
}