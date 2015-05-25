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

import static org.junit.Assert.*;

public class ParallelTest {
  private static abstract class Req {
  }

  private static abstract class Res {
  }

  private static class Req1 extends Req {
    public final String attr;
    public Req1(String attr) {
      this.attr = attr;
    }
  }

  private static class Res1 extends Res {
    public final Integer val;
    public Res1(int val) {
      this.val = val;
    }
  }

  private static class Req2 extends Req {
    public final String attr;
    public Req2(String attr) {
      this.attr = attr;
    }
  }

  private static class Res2 extends Res {
    public final Integer val;
    public Res2(int val) {
      this.val = val;
    }
  }

  private static class Req3 extends Req {
    public final String attr;
    public Req3(String attr) {
      this.attr = attr;
    }
  }

  private static class Res3 extends Res {
    public final Integer val;
    public Res3(int val) {
      this.val = val;
    }
  }

  @Test
  public void call() {
    // tag::call[]
    try (ExecHarness execHarness = ExecHarness.harness()) {
      Registry registry = Registries.empty();
      Req[] reqs = {new Req1("req1"), new Req2("req2"), new Req3("req3")};
      Action<Req, Res> a1 = Action.<Req, Res>of("a1", reqs[0], (ec, r) -> ec
        .blocking(() -> ActionResult.<Res>success(new Res1(1))));
      Action<Req, Res> a2 = Action.<Req, Res>of("a2", reqs[1], (ec, r) -> ec
        .blocking(() -> ActionResult.<Res>success(new Res2(2))));
      Action<Req, Res> a3 = Action.<Req, Res>of("a3", reqs[2], (ec, r) -> ec
        .blocking(() -> ActionResult.<Res>success(new Res3(3))));
      ImmutableList actions = ImmutableList.of(a1, a2, a3);
      Parallel<Req, Res> parallel = new Parallel<>();
      ExecResult<ActionResults<Res>> execResult = execHarness.yield(execControl -> parallel
        .apply(execControl, registry, actions));

      assertNotNull(execResult);
      assertNotNull(execResult.getValue());
      assertNotNull(execResult.getValue().getResults());
      assertEquals("0", execResult.getValue().getResults().get("a1").getCode());
      assertEquals(Integer.valueOf(1), ((Res1)execResult.getValue().getResults().get("a1").getData()).val);
      assertEquals("0", execResult.getValue().getResults().get("a2").getCode());
      assertEquals(Integer.valueOf(2), ((Res2)execResult.getValue().getResults().get("a2").getData()).val);
      assertEquals("0", execResult.getValue().getResults().get("a3").getCode());
      assertEquals(Integer.valueOf(3), ((Res3)execResult.getValue().getResults().get("a3").getData()).val);
    } catch (Exception ex) {
      assertNull(ex);
    }
    // end::call[]
  }
}
// end::all[]