package taglet;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Extends functionality of javadoc. Injects either class or its methods as javadoc code snippet.
 */
public class InjectCodeSnippetTaglet implements Taglet {
  @Override
  public boolean inField() {
    return true;
  }

  @Override
  public boolean inConstructor() {
    return true;
  }

  @Override
  public boolean inMethod() {
    return true;
  }

  @Override
  public boolean inOverview() {
    return false;
  }

  @Override
  public boolean inPackage() {
    return true;
  }

  @Override
  public boolean inType() {
    return true;
  }

  @Override
  public boolean isInlineTag() {
    return true;
  }

  @Override
  public String getName() {
    return "inject.snippet";
  }

  @Override
  public String toString(Tag tag) {
    try {
      return injectSnippet(tag);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String toString(Tag[] tags) {
    throw new RuntimeException("NOT SUPPORTED");
  }

  private String injectSnippet(Tag tag) throws IOException{
    int pos = tag.text().indexOf("#");
    final String pathToTest = "/src/test/java/" + (pos == -1 ? tag.text() : tag.text().substring(0, pos)).replace('.', '/').concat(".java");
    final File testFile = new File(".", pathToTest).getCanonicalFile();
    String text;
    if (pos == -1) {
      text = injectClass(testFile);
    } else {
      final String testName = tag.text().substring(pos + 1);
      text = injectMethod(testFile, testName);
    }

    return "<pre><code class='java'>" + text + "</code></pre>";
  }

  private String injectClass(File testFile) throws IOException {
    return new String(Files.readAllBytes(Paths.get(testFile.toURI())));
  }

  private String injectMethod(File testFile, String methodName) throws IOException {
    final Scanner scanner = new Scanner(testFile);
    List<String> lines = new ArrayList<>();
    while(scanner.hasNext()) {
      String current = scanner.findInLine(methodName);
      final boolean methodStartFound = current != null;
      if (methodStartFound) {
        scanner.nextLine();
        boolean endFound = false;
        do {
          current = scanner.nextLine();
          endFound = current.equals("  }");
          if (!endFound) {
            lines.add(current);
          }
        } while (!endFound);
      } else {
        scanner.nextLine();
      }
    }

    String res = "";
    for (String s : lines) {
      res += s + "\n";
    }
    return res;
  }

  @SuppressWarnings("unused")
  public static void register(Map tagletMap) {
    InjectCodeSnippetTaglet taglet = new InjectCodeSnippetTaglet();
    Taglet existingTaglet = (Taglet)tagletMap.get(taglet.getName());
    if (existingTaglet != null) {
      tagletMap.remove(taglet.getName());
    }
    tagletMap.put(taglet.getName(), taglet);
  }
}
