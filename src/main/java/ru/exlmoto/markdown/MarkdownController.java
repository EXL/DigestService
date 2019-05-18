package ru.exlmoto.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MarkdownController {
    @RequestMapping("/markdown")
    public String generateMarkdown(Model model) {
        model.addAttribute("markdownForm", new MarkdownForm());
        return "markdown";
    }

    @PostMapping("/markdown")
    public String submitGenerated(@ModelAttribute MarkdownForm markdownForm, Model model) {
        model.addAttribute("markdown", markdownForm.isMark() ?
                generateHtmlFromMarkdown(markdownForm.getMarkup()) :
                highlightCodeChunk(markdownForm.getMarkup(), "java"));
        return "markdown";
    }

    private String generateHtmlFromMarkdown(String markdown) {
        MutableDataSet options = new MutableDataSet();

        // uncomment to set optional extensions
        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

        // uncomment to convert soft-breaks to hard breaks
        //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(markdown);

        return renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
    }

    private String highlightCodeChunk(String codeChunk, String programingLanguage) {
        PythonInterpreter interpreter = new PythonInterpreter();

        // Set a variable with the content you want to work with
        interpreter.set("code", codeChunk);

        interpreter.exec(
                "from pygments import highlight\n"
                 + "from pygments.lexers import get_lexer_by_name\n"
                 + "from pygments.formatters import HtmlFormatter\n"
                 + "lexer = get_lexer_by_name('%lang%', stripall=True)\n".replace("%lang%", programingLanguage)
                 + "formatter = HtmlFormatter(linenos=True, cssclass='hgl')\n"
                 + "result = highlight(code, lexer, formatter)");

        return interpreter.get("result", String.class);
    }
}
