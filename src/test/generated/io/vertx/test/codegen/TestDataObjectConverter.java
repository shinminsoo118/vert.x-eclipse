package io.vertx.test.codegen;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

public class TestDataObjectConverter {

  public static void fromJson(JsonObject json, TestDataObject obj) {
    if (json.getValue("addedAggregatedDataObjects") instanceof JsonArray) {
      json.getJsonArray("addedAggregatedDataObjects").forEach(item -> {
        if (item instanceof JsonObject)
          obj.addAddedAggregatedDataObject(new io.vertx.test.codegen.AggregatedDataObject((JsonObject)item));
      });
    }
    if (json.getValue("addedBooleanValues") instanceof JsonArray) {
      json.getJsonArray("addedBooleanValues").forEach(item -> {
        if (item instanceof Boolean)
          obj.addAddedBooleanValue((Boolean)item);
      });
    }
    if (json.getValue("addedBoxedBooleanValues") instanceof JsonArray) {
      json.getJsonArray("addedBoxedBooleanValues").forEach(item -> {
        if (item instanceof Boolean)
          obj.addAddedBoxedBooleanValue((Boolean)item);
      });
    }
    if (json.getValue("addedBoxedByteValues") instanceof JsonArray) {
      json.getJsonArray("addedBoxedByteValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedBoxedByteValue(((Number)item).byteValue());
      });
    }
    if (json.getValue("addedBoxedCharValues") instanceof JsonArray) {
      json.getJsonArray("addedBoxedCharValues").forEach(item -> {
        if (item instanceof String)
          obj.addAddedBoxedCharValue(((String)item).charAt(0));
      });
    }
    if (json.getValue("addedBoxedDoubleValues") instanceof JsonArray) {
      json.getJsonArray("addedBoxedDoubleValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedBoxedDoubleValue(((Number)item).doubleValue());
      });
    }
    if (json.getValue("addedBoxedFloatValues") instanceof JsonArray) {
      json.getJsonArray("addedBoxedFloatValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedBoxedFloatValue(((Number)item).floatValue());
      });
    }
    if (json.getValue("addedBoxedIntValues") instanceof JsonArray) {
      json.getJsonArray("addedBoxedIntValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedBoxedIntValue(((Number)item).intValue());
      });
    }
    if (json.getValue("addedBoxedLongValues") instanceof JsonArray) {
      json.getJsonArray("addedBoxedLongValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedBoxedLongValue(((Number)item).longValue());
      });
    }
    if (json.getValue("addedBoxedShortValues") instanceof JsonArray) {
      json.getJsonArray("addedBoxedShortValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedBoxedShortValue(((Number)item).shortValue());
      });
    }
    if (json.getValue("addedBuffers") instanceof JsonArray) {
      json.getJsonArray("addedBuffers").forEach(item -> {
        if (item instanceof String)
          obj.addAddedBuffer(io.vertx.core.buffer.Buffer.buffer(java.util.Base64.getDecoder().decode((String)item)));
      });
    }
    if (json.getValue("addedByteValues") instanceof JsonArray) {
      json.getJsonArray("addedByteValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedByteValue(((Number)item).byteValue());
      });
    }
    if (json.getValue("addedCharValues") instanceof JsonArray) {
      json.getJsonArray("addedCharValues").forEach(item -> {
        if (item instanceof String)
          obj.addAddedCharValue(((String)item).charAt(0));
      });
    }
    if (json.getValue("addedDoubleValues") instanceof JsonArray) {
      json.getJsonArray("addedDoubleValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedDoubleValue(((Number)item).doubleValue());
      });
    }
    if (json.getValue("addedFloatValues") instanceof JsonArray) {
      json.getJsonArray("addedFloatValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedFloatValue(((Number)item).floatValue());
      });
    }
    if (json.getValue("addedHttpMethods") instanceof JsonArray) {
      json.getJsonArray("addedHttpMethods").forEach(item -> {
        if (item instanceof String)
          obj.addAddedHttpMethod(io.vertx.core.http.HttpMethod.valueOf((String)item));
      });
    }
    if (json.getValue("addedIntValues") instanceof JsonArray) {
      json.getJsonArray("addedIntValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedIntValue(((Number)item).intValue());
      });
    }
    if (json.getValue("addedJsonArrays") instanceof JsonArray) {
      json.getJsonArray("addedJsonArrays").forEach(item -> {
        if (item instanceof JsonArray)
          obj.addAddedJsonArray(((JsonArray)item).copy());
      });
    }
    if (json.getValue("addedJsonObjects") instanceof JsonArray) {
      json.getJsonArray("addedJsonObjects").forEach(item -> {
        if (item instanceof JsonObject)
          obj.addAddedJsonObject(((JsonObject)item).copy());
      });
    }
    if (json.getValue("addedLongValues") instanceof JsonArray) {
      json.getJsonArray("addedLongValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedLongValue(((Number)item).longValue());
      });
    }
    if (json.getValue("addedShortValues") instanceof JsonArray) {
      json.getJsonArray("addedShortValues").forEach(item -> {
        if (item instanceof Number)
          obj.addAddedShortValue(((Number)item).shortValue());
      });
    }
    if (json.getValue("addedStringValues") instanceof JsonArray) {
      json.getJsonArray("addedStringValues").forEach(item -> {
        if (item instanceof String)
          obj.addAddedStringValue((String)item);
      });
    }
    if (json.getValue("aggregatedDataObject") instanceof JsonObject) {
      obj.setAggregatedDataObject(new io.vertx.test.codegen.AggregatedDataObject((JsonObject)json.getValue("aggregatedDataObject")));
    }
    if (json.getValue("aggregatedDataObjects") instanceof JsonArray) {
      java.util.List<io.vertx.test.codegen.AggregatedDataObject> list = new java.util.ArrayList<>();
      json.getJsonArray("aggregatedDataObjects").forEach( item -> {
        if (item instanceof JsonObject)
          list.add(new io.vertx.test.codegen.AggregatedDataObject((JsonObject)item));
      });
      obj.setAggregatedDataObjects(list);
    }
    if (json.getValue("booleanValue") instanceof Boolean) {
      obj.setBooleanValue((Boolean)json.getValue("booleanValue"));
    }
    if (json.getValue("boxedBooleanValue") instanceof Boolean) {
      obj.setBoxedBooleanValue((Boolean)json.getValue("boxedBooleanValue"));
    }
    if (json.getValue("boxedBooleanValues") instanceof JsonArray) {
      java.util.List<java.lang.Boolean> list = new java.util.ArrayList<>();
      json.getJsonArray("boxedBooleanValues").forEach( item -> {
        if (item instanceof Boolean)
          list.add((Boolean)item);
      });
      obj.setBoxedBooleanValues(list);
    }
    if (json.getValue("boxedByteValue") instanceof Number) {
      obj.setBoxedByteValue(((Number)json.getValue("boxedByteValue")).byteValue());
    }
    if (json.getValue("boxedByteValues") instanceof JsonArray) {
      java.util.List<java.lang.Byte> list = new java.util.ArrayList<>();
      json.getJsonArray("boxedByteValues").forEach( item -> {
        if (item instanceof Number)
          list.add(((Number)item).byteValue());
      });
      obj.setBoxedByteValues(list);
    }
    if (json.getValue("boxedCharValue") instanceof String) {
      obj.setBoxedCharValue(((String)json.getValue("boxedCharValue")).charAt(0));
    }
    if (json.getValue("boxedCharValues") instanceof JsonArray) {
      java.util.List<java.lang.Character> list = new java.util.ArrayList<>();
      json.getJsonArray("boxedCharValues").forEach( item -> {
        if (item instanceof String)
          list.add(((String)item).charAt(0));
      });
      obj.setBoxedCharValues(list);
    }
    if (json.getValue("boxedDoubleValue") instanceof Number) {
      obj.setBoxedDoubleValue(((Number)json.getValue("boxedDoubleValue")).doubleValue());
    }
    if (json.getValue("boxedDoubleValues") instanceof JsonArray) {
      java.util.List<java.lang.Double> list = new java.util.ArrayList<>();
      json.getJsonArray("boxedDoubleValues").forEach( item -> {
        if (item instanceof Number)
          list.add(((Number)item).doubleValue());
      });
      obj.setBoxedDoubleValues(list);
    }
    if (json.getValue("boxedFloatValue") instanceof Number) {
      obj.setBoxedFloatValue(((Number)json.getValue("boxedFloatValue")).floatValue());
    }
    if (json.getValue("boxedFloatValues") instanceof JsonArray) {
      java.util.List<java.lang.Float> list = new java.util.ArrayList<>();
      json.getJsonArray("boxedFloatValues").forEach( item -> {
        if (item instanceof Number)
          list.add(((Number)item).floatValue());
      });
      obj.setBoxedFloatValues(list);
    }
    if (json.getValue("boxedIntValue") instanceof Number) {
      obj.setBoxedIntValue(((Number)json.getValue("boxedIntValue")).intValue());
    }
    if (json.getValue("boxedIntValues") instanceof JsonArray) {
      java.util.List<java.lang.Integer> list = new java.util.ArrayList<>();
      json.getJsonArray("boxedIntValues").forEach( item -> {
        if (item instanceof Number)
          list.add(((Number)item).intValue());
      });
      obj.setBoxedIntValues(list);
    }
    if (json.getValue("boxedLongValue") instanceof Number) {
      obj.setBoxedLongValue(((Number)json.getValue("boxedLongValue")).longValue());
    }
    if (json.getValue("boxedLongValues") instanceof JsonArray) {
      java.util.List<java.lang.Long> list = new java.util.ArrayList<>();
      json.getJsonArray("boxedLongValues").forEach( item -> {
        if (item instanceof Number)
          list.add(((Number)item).longValue());
      });
      obj.setBoxedLongValues(list);
    }
    if (json.getValue("boxedShortValue") instanceof Number) {
      obj.setBoxedShortValue(((Number)json.getValue("boxedShortValue")).shortValue());
    }
    if (json.getValue("boxedShortValues") instanceof JsonArray) {
      java.util.List<java.lang.Short> list = new java.util.ArrayList<>();
      json.getJsonArray("boxedShortValues").forEach( item -> {
        if (item instanceof Number)
          list.add(((Number)item).shortValue());
      });
      obj.setBoxedShortValues(list);
    }
    if (json.getValue("buffer") instanceof String) {
      obj.setBuffer(io.vertx.core.buffer.Buffer.buffer(java.util.Base64.getDecoder().decode((String)json.getValue("buffer"))));
    }
    if (json.getValue("buffers") instanceof JsonArray) {
      java.util.List<io.vertx.core.buffer.Buffer> list = new java.util.ArrayList<>();
      json.getJsonArray("buffers").forEach( item -> {
        if (item instanceof String)
          list.add(io.vertx.core.buffer.Buffer.buffer(java.util.Base64.getDecoder().decode((String)item)));
      });
      obj.setBuffers(list);
    }
    if (json.getValue("byteValue") instanceof Number) {
      obj.setByteValue(((Number)json.getValue("byteValue")).byteValue());
    }
    if (json.getValue("charValue") instanceof String) {
      obj.setCharValue(((String)json.getValue("charValue")).charAt(0));
    }
    if (json.getValue("doubleValue") instanceof Number) {
      obj.setDoubleValue(((Number)json.getValue("doubleValue")).doubleValue());
    }
    if (json.getValue("floatValue") instanceof Number) {
      obj.setFloatValue(((Number)json.getValue("floatValue")).floatValue());
    }
    if (json.getValue("httpMethod") instanceof String) {
      obj.setHttpMethod(io.vertx.core.http.HttpMethod.valueOf((String)json.getValue("httpMethod")));
    }
    if (json.getValue("httpMethods") instanceof JsonArray) {
      java.util.List<io.vertx.core.http.HttpMethod> list = new java.util.ArrayList<>();
      json.getJsonArray("httpMethods").forEach( item -> {
        if (item instanceof String)
          list.add(io.vertx.core.http.HttpMethod.valueOf((String)item));
      });
      obj.setHttpMethods(list);
    }
    if (json.getValue("intValue") instanceof Number) {
      obj.setIntValue(((Number)json.getValue("intValue")).intValue());
    }
    if (json.getValue("jsonArray") instanceof JsonArray) {
      obj.setJsonArray(((JsonArray)json.getValue("jsonArray")).copy());
    }
    if (json.getValue("jsonArrays") instanceof JsonArray) {
      java.util.List<io.vertx.core.json.JsonArray> list = new java.util.ArrayList<>();
      json.getJsonArray("jsonArrays").forEach( item -> {
        if (item instanceof JsonArray)
          list.add(((JsonArray)item).copy());
      });
      obj.setJsonArrays(list);
    }
    if (json.getValue("jsonObject") instanceof JsonObject) {
      obj.setJsonObject(((JsonObject)json.getValue("jsonObject")).copy());
    }
    if (json.getValue("jsonObjects") instanceof JsonArray) {
      java.util.List<io.vertx.core.json.JsonObject> list = new java.util.ArrayList<>();
      json.getJsonArray("jsonObjects").forEach( item -> {
        if (item instanceof JsonObject)
          list.add(((JsonObject)item).copy());
      });
      obj.setJsonObjects(list);
    }
    if (json.getValue("longValue") instanceof Number) {
      obj.setLongValue(((Number)json.getValue("longValue")).longValue());
    }
    if (json.getValue("shortValue") instanceof Number) {
      obj.setShortValue(((Number)json.getValue("shortValue")).shortValue());
    }
    if (json.getValue("stringValue") instanceof String) {
      obj.setStringValue((String)json.getValue("stringValue"));
    }
    if (json.getValue("stringValues") instanceof JsonArray) {
      java.util.List<java.lang.String> list = new java.util.ArrayList<>();
      json.getJsonArray("stringValues").forEach( item -> {
        if (item instanceof String)
          list.add((String)item);
      });
      obj.setStringValues(list);
    }
  }

  public static void toJson(TestDataObject obj, JsonObject json) {
    if (obj.getAddedAggregatedDataObjects() != null) {
      json.put("addedAggregatedDataObjects", new JsonArray(
          obj.getAddedAggregatedDataObjects().
              stream().
              map(item -> item.toJson()).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBooleanValues() != null) {
      json.put("addedBooleanValues", new JsonArray(
          obj.getAddedBooleanValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBoxedBooleanValues() != null) {
      json.put("addedBoxedBooleanValues", new JsonArray(
          obj.getAddedBoxedBooleanValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBoxedByteValues() != null) {
      json.put("addedBoxedByteValues", new JsonArray(
          obj.getAddedBoxedByteValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBoxedCharValues() != null) {
      json.put("addedBoxedCharValues", new JsonArray(
          obj.getAddedBoxedCharValues().
              stream().
              map(item -> Character.toString(item)).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBoxedDoubleValues() != null) {
      json.put("addedBoxedDoubleValues", new JsonArray(
          obj.getAddedBoxedDoubleValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBoxedFloatValues() != null) {
      json.put("addedBoxedFloatValues", new JsonArray(
          obj.getAddedBoxedFloatValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBoxedIntValues() != null) {
      json.put("addedBoxedIntValues", new JsonArray(
          obj.getAddedBoxedIntValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBoxedLongValues() != null) {
      json.put("addedBoxedLongValues", new JsonArray(
          obj.getAddedBoxedLongValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBoxedShortValues() != null) {
      json.put("addedBoxedShortValues", new JsonArray(
          obj.getAddedBoxedShortValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedBuffers() != null) {
      json.put("addedBuffers", new JsonArray(
          obj.getAddedBuffers().
              stream().
              map(item -> item.getBytes()).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedByteValues() != null) {
      json.put("addedByteValues", new JsonArray(
          obj.getAddedByteValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedCharValues() != null) {
      json.put("addedCharValues", new JsonArray(
          obj.getAddedCharValues().
              stream().
              map(item -> Character.toString(item)).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedDoubleValues() != null) {
      json.put("addedDoubleValues", new JsonArray(
          obj.getAddedDoubleValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedFloatValues() != null) {
      json.put("addedFloatValues", new JsonArray(
          obj.getAddedFloatValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedHttpMethods() != null) {
      json.put("addedHttpMethods", new JsonArray(
          obj.getAddedHttpMethods().
              stream().
              map(item -> item.name()).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedIntValues() != null) {
      json.put("addedIntValues", new JsonArray(
          obj.getAddedIntValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedJsonArrays() != null) {
      json.put("addedJsonArrays", new JsonArray(
          obj.getAddedJsonArrays().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedJsonObjects() != null) {
      json.put("addedJsonObjects", new JsonArray(
          obj.getAddedJsonObjects().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedLongValues() != null) {
      json.put("addedLongValues", new JsonArray(
          obj.getAddedLongValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedShortValues() != null) {
      json.put("addedShortValues", new JsonArray(
          obj.getAddedShortValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAddedStringValues() != null) {
      json.put("addedStringValues", new JsonArray(
          obj.getAddedStringValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getAggregatedDataObject() != null) {
      json.put("aggregatedDataObject", obj.getAggregatedDataObject().toJson());
    }
    if (obj.getAggregatedDataObjects() != null) {
      json.put("aggregatedDataObjects", new JsonArray(
          obj.getAggregatedDataObjects().
              stream().
              map(item -> item.toJson()).
              collect(java.util.stream.Collectors.toList())));
    }
    json.put("booleanValue", obj.isBooleanValue());
    if (obj.isBoxedBooleanValue() != null) {
      json.put("boxedBooleanValue", obj.isBoxedBooleanValue());
    }
    if (obj.getBoxedBooleanValues() != null) {
      json.put("boxedBooleanValues", new JsonArray(
          obj.getBoxedBooleanValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getBoxedByteValue() != null) {
      json.put("boxedByteValue", obj.getBoxedByteValue());
    }
    if (obj.getBoxedByteValues() != null) {
      json.put("boxedByteValues", new JsonArray(
          obj.getBoxedByteValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getBoxedCharValue() != null) {
      json.put("boxedCharValue", Character.toString(obj.getBoxedCharValue()));
    }
    if (obj.getBoxedCharValues() != null) {
      json.put("boxedCharValues", new JsonArray(
          obj.getBoxedCharValues().
              stream().
              map(item -> Character.toString(item)).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getBoxedDoubleValue() != null) {
      json.put("boxedDoubleValue", obj.getBoxedDoubleValue());
    }
    if (obj.getBoxedDoubleValues() != null) {
      json.put("boxedDoubleValues", new JsonArray(
          obj.getBoxedDoubleValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getBoxedFloatValue() != null) {
      json.put("boxedFloatValue", obj.getBoxedFloatValue());
    }
    if (obj.getBoxedFloatValues() != null) {
      json.put("boxedFloatValues", new JsonArray(
          obj.getBoxedFloatValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getBoxedIntValue() != null) {
      json.put("boxedIntValue", obj.getBoxedIntValue());
    }
    if (obj.getBoxedIntValues() != null) {
      json.put("boxedIntValues", new JsonArray(
          obj.getBoxedIntValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getBoxedLongValue() != null) {
      json.put("boxedLongValue", obj.getBoxedLongValue());
    }
    if (obj.getBoxedLongValues() != null) {
      json.put("boxedLongValues", new JsonArray(
          obj.getBoxedLongValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getBoxedShortValue() != null) {
      json.put("boxedShortValue", obj.getBoxedShortValue());
    }
    if (obj.getBoxedShortValues() != null) {
      json.put("boxedShortValues", new JsonArray(
          obj.getBoxedShortValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getBuffer() != null) {
      json.put("buffer", obj.getBuffer().getBytes());
    }
    if (obj.getBuffers() != null) {
      json.put("buffers", new JsonArray(
          obj.getBuffers().
              stream().
              map(item -> item.getBytes()).
              collect(java.util.stream.Collectors.toList())));
    }
    json.put("byteValue", obj.getByteValue());
    json.put("charValue", Character.toString(obj.getCharValue()));
    json.put("doubleValue", obj.getDoubleValue());
    json.put("floatValue", obj.getFloatValue());
    if (obj.getHttpMethod() != null) {
      json.put("httpMethod", obj.getHttpMethod().name());
    }
    if (obj.getHttpMethods() != null) {
      json.put("httpMethods", new JsonArray(
          obj.getHttpMethods().
              stream().
              map(item -> item.name()).
              collect(java.util.stream.Collectors.toList())));
    }
    json.put("intValue", obj.getIntValue());
    if (obj.getJsonArray() != null) {
      json.put("jsonArray", obj.getJsonArray());
    }
    if (obj.getJsonArrays() != null) {
      json.put("jsonArrays", new JsonArray(
          obj.getJsonArrays().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    if (obj.getJsonObject() != null) {
      json.put("jsonObject", obj.getJsonObject());
    }
    if (obj.getJsonObjects() != null) {
      json.put("jsonObjects", new JsonArray(
          obj.getJsonObjects().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
    json.put("longValue", obj.getLongValue());
    json.put("shortValue", obj.getShortValue());
    if (obj.getStringValue() != null) {
      json.put("stringValue", obj.getStringValue());
    }
    if (obj.getStringValues() != null) {
      json.put("stringValues", new JsonArray(
          obj.getStringValues().
              stream().
              map(item -> item).
              collect(java.util.stream.Collectors.toList())));
    }
  }
}