package com.zeoflow.stylar.utils;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AssetsHelper
{

    public static String readFile(AssetManager assets, String fileName)
    {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(assets.open(fileName), StandardCharsets.UTF_8)))
        {

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null)
            {
                if (!content.toString().isEmpty())
                {
                    content.append("\n\n");
                }
                content.append(mLine);
            }
        } catch (IOException e)
        {
            //log the exception
        }
        return content.toString();
    }

}
