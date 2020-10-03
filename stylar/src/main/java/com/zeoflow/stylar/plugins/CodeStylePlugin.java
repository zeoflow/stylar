package com.zeoflow.stylar.plugins;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.GrammarLocator;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_c;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_clike;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_clojure;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_cpp;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_csharp;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_css;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_css_extras;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_dart;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_git;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_go;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_groovy;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_java;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_javascript;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_json;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_kotlin;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_makefile;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_markdown;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_markup;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_python;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_scala;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_sql;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_swift;
import com.zeoflow.stylar.codestyle.languages.CodeStyle_yaml;

import java.util.Set;

public class CodeStylePlugin implements GrammarLocator
{

    @Nullable
    @Override
    public CodeStyle.Grammar grammar(@NonNull CodeStyle codeStyle, @NonNull String language)
    {
        switch (language)
        {
            case "c":
                return CodeStyle_c.create(codeStyle);
            case "clike":
                return CodeStyle_clike.create(codeStyle);
            case "clojure":
                return CodeStyle_clojure.create(codeStyle);
            case "cpp":
                return CodeStyle_cpp.create(codeStyle);
            case "csharp":
                return CodeStyle_csharp.create(codeStyle);
            case "css":
                return CodeStyle_css.create(codeStyle);
            case "inside":
                return CodeStyle_css_extras.create(codeStyle);
            case "dart":
                return CodeStyle_dart.create(codeStyle);
            case "git":
                return CodeStyle_git.create(codeStyle);
            case "go":
                return CodeStyle_go.create(codeStyle);
            case "groovy":
                return CodeStyle_groovy.create(codeStyle);
            case "java":
                return CodeStyle_java.create(codeStyle);
            case "javascript":
                return CodeStyle_javascript.create(codeStyle);
            case "json":
                return CodeStyle_json.create(codeStyle);
            case "kotlin":
                return CodeStyle_kotlin.create(codeStyle);
            case "makefile":
                return CodeStyle_makefile.create(codeStyle);
            case "markdown":
                return CodeStyle_markdown.create(codeStyle);
            case "markup":
                return CodeStyle_markup.create(codeStyle);
            case "python":
                return CodeStyle_python.create(codeStyle);
            case "scala":
                return CodeStyle_scala.create(codeStyle);
            case "sql":
                return CodeStyle_sql.create(codeStyle);
            case "swift":
                return CodeStyle_swift.create(codeStyle);
            case "yaml":
                return CodeStyle_yaml.create(codeStyle);
            default:
                return null;
        }
    }

    @Override
    public Set<String> languages()
    {
        return null;
    }
}