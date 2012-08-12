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

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.karlmartens.platform.util.ComparableComparator;
import net.karlmartens.platform.util.NumberComparator;
import net.karlmartens.platform.util.NumberStringComparator;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DataTableColumn {

  public enum Role {
    DOMAIN {

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("domain");
      }
    },
    DATA {

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("data");
      }
    },
    EMPHASIS {

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("emphasis");
      }
    },
    TOOLTIP {

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("tooltip");
      }
    };

    abstract JsonElement toJson();
  }

  public enum Type {

    STRING {

      @SuppressWarnings("unchecked")
      @Override
      Comparator<Object> comparator() {
        final Comparator<?> comparator = new NumberStringComparator();
        return (Comparator<Object>) comparator;
      }

      @Override
      String format(Object value) {
        return (String) value;
      }

      @Override
      String format(String pattern, Object value) {
        return format(value);
      }

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("string");
      }
    },

    NUMBER {

      @SuppressWarnings("unchecked")
      @Override
      Comparator<Object> comparator() {
        final Comparator<?> comparator = new NumberComparator();
        return (Comparator<Object>) comparator;
      }

      @Override
      String format(Object value) {
        return format("#,##0.00", value);
      }

      @Override
      String format(String pattern, Object value) {
        final DecimalFormat format = new DecimalFormat(pattern);
        return format.format(value);
      }

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("number");
      }
    },

    BOOLEAN {

      @SuppressWarnings("unchecked")
      @Override
      Comparator<Object> comparator() {
        return ComparableComparator.INSTANCE;
      }

      @Override
      String format(Object value) {
        final Boolean b = (Boolean) value;
        return b.toString();
      }

      @Override
      String format(String pattern, Object value) {
        return format(value);
      }

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("boolean");
      }
    },

    DATE {
      @SuppressWarnings("unchecked")
      @Override
      Comparator<Object> comparator() {
        return ComparableComparator.INSTANCE;
      }

      @Override
      String format(Object value) {
        return format("yyyy-MM-dd", value);
      }

      @Override
      String format(String pattern, Object value) {
        return DateTimeFormat.forPattern(pattern).print((LocalDate) value);
      }

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("date");
      }
    },

    DATE_TIME {
      @SuppressWarnings("unchecked")
      @Override
      Comparator<Object> comparator() {
        return ComparableComparator.INSTANCE;
      }

      @Override
      String format(Object value) {
        return format("yyyy-MM-dd kk:mm", value);
      }

      @Override
      String format(String pattern, Object value) {
        return DateTimeFormat.forPattern(pattern).print((LocalDateTime) value);
      }

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("dateTime");
      }
    },

    TIME_OF_DAY {
      @SuppressWarnings("unchecked")
      @Override
      Comparator<Object> comparator() {
        return ComparableComparator.INSTANCE;
      }

      @Override
      String format(Object value) {
        return format("kk:mm", value);
      }

      @Override
      String format(String pattern, Object value) {
        return DateTimeFormat.forPattern(pattern).print((LocalTime) value);
      }

      @Override
      JsonElement toJson() {
        return new JsonPrimitive("timeOfDay");
      }
    };

    abstract Comparator<Object> comparator();

    abstract String format(Object value);

    abstract String format(String pattern, Object value);

    abstract JsonElement toJson();

  }

  private final Type _type;
  private final Map<String, Object> _properties;

  private String _label;
  private String _id;
  private Role _role;
  private String _pattern;

  public DataTableColumn(Type type) {
    if (type == null)
      throw new NullPointerException();
    _type = type;
    _properties = new HashMap<String, Object>();
  }

  public Type type() {
    return _type;
  }

  public DataTableColumn label(String label) {
    _label = label;
    return this;
  }

  public String label() {
    return _label;
  }

  public DataTableColumn id(String id) {
    _id = id;
    return this;
  }

  public String id() {
    return _id;
  }

  public DataTableColumn role(Role role) {
    _role = role;
    return this;
  }

  public Role role() {
    return _role;
  }

  public DataTableColumn pattern(String pattern) {
    _pattern = pattern;
    return this;
  }

  public String pattern() {
    return _pattern;
  }

  public Object property(String name) {
    return _properties.get(name);
  }

  public DataTableColumn setProperty(String name, Object value) {
    _properties.put(name, value);
    return this;
  }

  public Map<String, Object> properties() {
    return _properties;
  }

  public DataTableColumn properties(Map<String, Object> properties) {
    _properties.putAll(properties);
    return this;
  }

  public JsonElement toJson() {
    final JsonObject json = new JsonObject();
    json.addProperty("id", _id);
    json.addProperty("label", _label);
    json.addProperty("pattern", _pattern);
    json.add("role", _role == null ? JsonNull.INSTANCE : _role.toJson());
    json.add("type", _type == null ? JsonNull.INSTANCE : _type.toJson());
    return json;
  }

  @Override
  public String toString() {
    return new Gson().toJson(toJson());
  }

  void release() {
    _properties.clear();

    _label = null;
    _id = null;
    _role = null;
    _pattern = null;
  }
}