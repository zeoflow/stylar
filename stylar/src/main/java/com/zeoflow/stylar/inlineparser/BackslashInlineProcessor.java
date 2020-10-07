package com.zeoflow.stylar.inlineparser;

import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Node;

import java.util.regex.Pattern;

/**
 * @since 4.2.0
 */
public class BackslashInlineProcessor extends InlineProcessor
{

    private static final Pattern ESCAPABLE = StylarInlineParser.ESCAPABLE;

    @Override
    public char specialCharacter()
    {
        return '\\';
    }

    @Override
    protected Node parse()
    {
        index++;
        Node node;
        if (peek() == '\n')
        {
            node = new HardLineBreak();
            index++;
        } else if (index < input.length() && ESCAPABLE.matcher(input.substring(index, index + 1)).matches())
        {
            node = text(input, index, index + 1);
            index++;
        } else
        {
            node = text("\\");
        }
        return node;
    }
}
