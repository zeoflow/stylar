package com.zeoflow.stylar.codestyle.bundler;

import org.jetbrains.annotations.NotNull;

public class ClassInfo
{

    public final String packageName;
    public final String className;

    public ClassInfo(@NotNull String packageName, @NotNull String className)
    {
        this.packageName = packageName;
        this.className = className;
    }
}
