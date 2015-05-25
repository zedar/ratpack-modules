package ratpack.sep.exec;

import org.junit.Test;
import ratpack.exec.ExecResult;
import ratpack.registry.Registries;
import ratpack.registry.Registry;
import ratpack.sep.Action;
import ratpack.sep.ActionResult;
import ratpack.sep.ActionResults;
import ratpack.test.exec.ExecHarness;

import java.io.IOException;

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
  // tag::call[]
  public void call() {
    try (ExecHarness harness = ExecHarness.harness()) {
      Registry registry = Registries.empty();
      InvokeWithRetry<Req, Res> inv = new InvokeWithRetry<>(0);
      Req req = new Req("foo");
      ExecResult<ActionResults<Res>> execResult = harness.yield(execControl -> inv
        .apply(execControl, registry, Action.of("inv", req, ec -> ec.promise(fulfiller -> {
          if (req.attr.equals("foo")) {
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
  }
  // end::call[]
}
