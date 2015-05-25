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
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import ratpack.exec.ExecResult;
import ratpack.registry.Registries;
import ratpack.registry.Registry;
import ratpack.sep.Action;
import ratpack.sep.ActionResult;
import ratpack.sep.ActionResults;
import ratpack.test.exec.ExecHarness;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FanOutFanInTest {
  static abstract class Req {
  }

  static class Req1 extends Req {
    public final String val;
    public Req1(String val) {
      this.val = val;
    }
  }

  static class Req2 extends Req {
    public final String val;
    public Req2(String val) {
      this.val = val;
    }
  }

  static abstract class Res {
    protected Integer _val;
    public Integer val() {
      return _val;
    };
  }

  static class Res1 extends Res {
    public Res1(int val) {
      this._val = val;
    }
  }

  static class Res2 extends Res {
    public Res2(int val) {
      this._val = val;
    }
  }

  static class ResFinal extends Res {
    public ResFinal(int val) {
      this._val = val;
    }
  }

  @Test
  public void call() {
    // tag::call[]
    try (ExecHarness execHarness = ExecHarness.harness()) {
      Registry registry = Registries.empty();
      Req[] reqs = {new Req1("req1"), new Req2("req2")};
      List actions = ImmutableList.of(
        Action.of("a1", reqs[0], (ec, r) -> ec.blocking(() -> ActionResult.<Res>success(new Res1(1)))),
        Action.of("a2", reqs[1], (ec, r) -> ec.blocking(() -> ActionResult.<Res>success(new Res2(2))))
      );
      Action<ActionResults<Res>, Res> finalizer = Action.of("finalizer", null, (ec, actionResults) -> ec
        .promise(f -> {
          int count = 0;
          for (Map.Entry<String, ActionResult<Res>> entry : actionResults.getResults().entrySet()) {
            if (entry.getValue().isSuccess()) {
              count += entry.getValue().getData().val();
            }
          }
          f.success(ActionResult.success(new ResFinal(count)));
        }));
      FanOutFanIn<Req, Res, Res> fanOutFanIn = new FanOutFanIn<>();
      ExecResult<ActionResults<Res>> execResult = execHarness.yield(execControl -> fanOutFanIn
        .apply(execControl, registry, actions, finalizer));

      assertNotNull(execResult);
      assertNotNull(execResult.getValue());
      assertNotNull(execResult.getValue().getResults());
      assertEquals("0", execResult.getValue().getResults().get("finalizer").getCode());
      assertEquals(Integer.valueOf(3), ((ResFinal)execResult.getValue().getResults().get("finalizer").getData()).val());
    } catch (Exception ex) {
      assertNull(ex);
    }
    // end::call[]
  }
}
// end::all[]