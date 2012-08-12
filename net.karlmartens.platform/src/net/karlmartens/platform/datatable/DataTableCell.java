/**
 *   Copyright 2012 Karl Martens
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *       
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   net.karlmartens.platform, is a library of shared basic utility classes
 */

package net.karlmartens.platform.datatable;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.base.BaseLocal;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DataTableCell {

  private final Map<String, Object> _properties = new HashMap<String, Object>();

  private Object _value;
  private String _formattedValue;

  public DataTableCell value(String value) {
    return doSetValue(value);
  }

  public DataTableCell value(Number value) {
    return doSetValue(value);
  }

  public DataTableCell value(Boolean value) {
    return doSetValue(value);
  }

  public DataTableCell value(LocalDate value) {
    return doSetValue(value);
  }

  public DataTableCell value(LocalDateTime value) {
    return doSetValue(value);
  }

  public DataTableCell value(LocalTime value) {
    return doSetValue(value);
  }

  private DataTableCell doSetValue(Object value) {
    _value = value;
    return this;
  }

  public Object value() {
    return _value;
  }

  public DataTableCell formattedValue(String formattedValue) {
    _formattedValue = formattedValue;
    return this;
  }

  public String formattedValue() {
    return _formattedValue;
  }

  public Map<String, Object> properties() {
    return _properties;
  }

  public Object property(String name) {
    return _properties.get(name);
  }

  public void setProperty(String name, Object value) {
    _properties.put(name, value);
  }

  public void release() {
    _properties.clear();
    _value = null;
    _formattedValue = null;
  }

  public JsonElement toJson() {
    final JsonObject json = new JsonObject();
    json.add("v", creasteJsonElement(_value));
    json.addProperty("f", _formattedValue);
    return json;
  }

  @Override
  public String toString() {
    return new Gson().toJson(toJson());
  }

  private static JsonElement creasteJsonElement(Object o) {
    if (o == null)
      return JsonNull.INSTANCE;

    if (o instanceof String)
      return new JsonPrimitive((String) o);

    if (o instanceof Boolean)
      return new JsonPrimitive((Boolean) o);

    if (o instanceof Number)
      return new JsonPrimitive((Number) o);

    if (o instanceof BaseLocal) {
      final BaseLocal date = (BaseLocal) o;
      final JsonObject json = new JsonObject();
      json.add("year", createJsonElement(DateTimeFieldType.year(), date));
      json.add("month",
          createJsonElement(DateTimeFieldType.monthOfYear(), date));
      json.add("day", createJsonElement(DateTimeFieldType.dayOfMonth(), date));
      json.add("hour", createJsonElement(DateTimeFieldType.hourOfDay(), date));
      json.add("minute",
          createJsonElement(DateTimeFieldType.minuteOfHour(), date));
      json.add("second",
          createJsonElement(DateTimeFieldType.secondOfMinute(), date));
      json.add("millis",
          createJsonElement(DateTimeFieldType.millisOfSecond(), date));
      return json;
    }

    throw new UnsupportedOperationException();
  }

  private static JsonElement createJsonElement(DateTimeFieldType field,
      BaseLocal local) {
    if (!local.isSupported(field))
      return JsonNull.INSTANCE;

    return new JsonPrimitive(local.get(field));
  }
}