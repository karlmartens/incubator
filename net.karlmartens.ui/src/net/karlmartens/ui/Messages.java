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
 *   net.karlmartens.ui, is a library of UI widgets
 */
package net.karlmartens.ui;

import java.util.ResourceBundle;

/**
 * @author karl
 * 
 */
public enum Messages {

  AVAILABLE("Available.TEXT"), //
  COPY("Copy.TEXT"), //
  CUT("Cut.TEXT"), //
  DELETE("Delete.TEXT"), //
  ERROR_MULTI_SELECTION_MESSAGE("Error.MultiSelection.Message"), //
  ERROR_UNSUPPORTED_TITLE("Error.Unsupported.Title"), //
  FILTER("Filter.TEXT"), //
  FILTER_ALL("Filter.All.TEXT"), //
  FILTER_TOPTEN("Filter.TopTen.TEXT"), //
  PASTE("Paste.TEXT"), //
  RESIZE_COLUMN("Resize.Column.TEXT"), //
  RESIZE_COLUMN_ALL("Resize.Column.All.TEXT"), //
  SELECT_ALL("Select.All.TEXT"), //
  SELECTED("Selected.TEXT"), //
  SHOW_HIDE_COLUMN("ShowHide.Columns.TEXT"), //
  SORT_ASCENDING("Sort.Ascending.TEXT"), //
  SORT_DESCENDING("Sort.Descending.TEXT"), //
  ;

  private final static ResourceBundle _BUNDLE = ResourceBundle
      .getBundle("net.karlmartens.ui.locale.messages");
  private final String _key;

  private Messages(String key) {
    _key = key;
  }

  public String string(Object... args) {
    final String message = _BUNDLE.getString(_key);
    return String.format(message, args);
  }
}
