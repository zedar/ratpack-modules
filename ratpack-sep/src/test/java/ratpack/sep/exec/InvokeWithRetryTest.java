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
package ratpack.sep.exec;

// tag::all[]
import org.junit.Test;
import ratpack.exec.ExecResult;
import ratpack.registry.Registries;
import ratpack.registry.Registry;
import ratpack.sep.Action;
import ratpack.sep.ActionResult;
import ratpack.sep.ActionResults;
import ratpack.test.exec.ExecHarness;

import static org.junit.Assert.*;

public class InvokeWithRetryTest {
  private static class Req {
    public final String attr;
    public Req(String attr) {
      this.attr = attr;
    }
  }
  private static class Res {
    public final String attr;
    public Res(String attr) {
      this.attr = attr;
    }
  }

  @Test
  public void call() {
    // tag::call[]
    try (ExecHarness harness = ExecHarness.harness()) {
      Registry registry = Registries.empty();
      InvokeWithRetry<Req, Res> inv = new InvokeWithRetry<>(0);
      Req req = new Req("foo");
      ExecResult<ActionResults<Res>> execResult = harness.yield(execControl -> inv
        .apply(execControl, registry, Action.of("inv", req, (ec, r) -> ec.promise(fulfiller -> {
          if (r.attr.equals("foo")) {
            fulfiller.success(ActionResult.<Res>success(new Res("bar")));
          } else {
            fulfiller.success(ActionResult.error("ERR", "attr has to be foo"));
          }
        }))));
      assertNotNull(execResult);
      assertNotNull(execResult.getValue());
      assertNotNull(execResult.getValue().getResults());
      assertEquals("0", execResult.getValue().getResults().get("inv").getCode());
    } catch (Exception ex) {
      assertNull(ex);
    }
    // end::call[]
  }
}
// end::all[]