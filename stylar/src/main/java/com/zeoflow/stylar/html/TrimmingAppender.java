package com.zeoflow.stylar.html;

import androidx.annotation.NonNull;

abstract class TrimmingAppender
{

    @NonNull
    static TrimmingAppender create()
    {
        return new Impl();
    }

    abstract <T extends Appendable & CharSequence> void append(
        @NonNull T output,
        @NonNull String data
    );

    static class Impl extends TrimmingAppender
    {

        // if data is fully empty (consists of white spaces) -> do not add anything
        // leading ws:
        //  - trim to one space (if at all present) append to output only if previous is ws
        // trailing ws:
        //  - if present trim to single space

        @Override
        <T extends Appendable & CharSequence> void append(
            @NonNull T output,
            @NonNull String data
        )
        {

            final int startLength = output.length();

            char c;

            boolean previousIsWhiteSpace = false;

            for (int i = 0, length = data.length(); i < length; i++)
            {

                c = data.charAt(i);

                if (Character.isWhitespace(c))
                {
                    previousIsWhiteSpace = true;
                    continue;
                }

                if (previousIsWhiteSpace)
                {
                    // validate that output has ws as last char
                    final int outputLength = output.length();
                    if (outputLength > 0
                        && !Character.isWhitespace(output.charAt(outputLength - 1)))
                    {
                        AppendableUtils.appendQuietly(output, ' ');
                    }
                }

                previousIsWhiteSpace = false;
                AppendableUtils.appendQuietly(output, c);
            }

            // additionally check if previousIsWhiteSpace is true (if data ended with ws)
            // BUT only if we have added something (otherwise the whole data is empty (white))
            if (previousIsWhiteSpace && (startLength < output.length()))
            {
                AppendableUtils.appendQuietly(output, ' ');
            }
        }
    }
}
