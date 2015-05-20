/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.sep;

import ratpack.api.Nullable;

/**
 * The result of an action execution.
 * <p>
 * Instances can be create by one of the static methods.
 *
 * @param <T> a type of data accompanying action result
 */
public class ActionResult<T> {
  private final String code;
  private final String message;
  private final Throwable error;
  private final T data;

  private ActionResult(String code, String message, Throwable error, T data) {
    this.code = code;
    this.message = message;
    this.error = error;
    this.data = data;
  }

  /**
   * An error code. If <b>0</b> no error was reported.
   *
   * @return <b>0</b> if no error or another string otherwise
   */
  public String getCode() {
    return code;
  }

  /**
   * Any message provided as part of action execution, may be {@code null}.
   * <p>
   * Message may be provided with error or successful result.
   *
   * @return any message provided as part of action execution.
   */
  @Nullable
  public String getMessage() {
    return message;
  }

  /**
   * An error thrown by an action.
   *
   * @return an error
   */
  @Nullable
  public Throwable getError() {
    return error;
  }

  /**
   * A data returned as action results
   *
   * @return the data associated with the given action result
   */
  @Nullable
  public T getData() {
    return data;
  }

  /**
   * Is action result successful?
   *
   * @return true if action result is successful
   */
  public boolean isSuccess() {
    if ("0".equals(code) && error == null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Is failed action result?
   *
   * @return true if failed action result
   */
  public boolean isError() {
    return !isSuccess();
  }

  /**
   * Creates a successful result, with no message.
   *
   * @param <T> a type of accompanying data
   * @return a successful result, with no message.
   */
  public static <T> ActionResult<T> success() {
    return new ActionResult<>("0", null, null, null);
  }

  /**
   * Creates successful result, with the given message.
   *
   * @param message a message to accompany the result
   * @param <T> a type of accompanying data
   * @return a successful result, with the given message
   */
  public static <T> ActionResult<T> success(String message) {
    return new ActionResult<>("0", message, null, null);
  }

  /**
   * Creates successful result with the data assigned
   * @param data a data accompany the result
   * @param <T> a type of accompanying data
   * @return a successful result
   */
  public static <T> ActionResult<T> success(T data) {
    return new ActionResult<>("0", null, null, data);
  }

  /**
   * Creates successful result with the given message and data
   * @param message a message to accompany the result
   * @param data a data to accompany the result
   * @param <T> a type of accompanying data
   * @return a successful result
   */
  public static <T> ActionResult<T> success(String message, T data) {
    return new ActionResult<>("0", message, null, data);
  }

  /**
   * Creates an error result, with the given message.
   *
   * @param code an error code
   * @param message a message to accompany the result
   * @param <T> a type of accompanying data
   * @return an failed result, with the given message
   */
  public static <T> ActionResult<T> error(String code, String message) {
    return new ActionResult<>(code, message, null, null);
  }

  /**
   * Creates an error result, with the given exception.
   * <p>
   * The message of the given exception will be used as the message of the result
   * @param error an exception thrown during action exception
   * @param <T> a type of accompanying data
   * @return an failed result, with the given error
   */
  public static <T> ActionResult<T> error(Throwable error) {
    return new ActionResult<>(error.toString(), error.getMessage(), error, null);
  }
}
