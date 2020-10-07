package com.zeoflow.stylar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.core.CorePlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// @since 4.0.0
public class RegistryImpl implements StylarPlugin.Registry
{

    private final List<StylarPlugin> origin;
    private final List<StylarPlugin> plugins;
    private final Set<StylarPlugin> pending;

    public RegistryImpl(@NonNull List<StylarPlugin> origin)
    {
        this.origin = origin;
        this.plugins = new ArrayList<>(origin.size());
        this.pending = new HashSet<>(3);
    }

    @Nullable
    private static <P extends StylarPlugin> P find(
        @NonNull List<StylarPlugin> plugins,
        @NonNull Class<P> type)
    {
        for (StylarPlugin plugin : plugins)
        {
            if (type.isAssignableFrom(plugin.getClass()))
            {
                //noinspection unchecked
                return (P) plugin;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public <P extends StylarPlugin> P require(@NonNull Class<P> plugin)
    {
        return get(plugin);
    }

    @Override
    public <P extends StylarPlugin> void require(
        @NonNull Class<P> plugin,
        @NonNull StylarPlugin.Action<? super P> action)
    {
        action.apply(get(plugin));
    }

    @NonNull
    public List<StylarPlugin> process()
    {
        for (StylarPlugin plugin : origin)
        {
            configure(plugin);
        }
        return plugins;
    }

    private void configure(@NonNull StylarPlugin plugin)
    {

        // important -> check if it's in plugins
        //  if it is -> no need to configure (already configured)

        if (!plugins.contains(plugin))
        {

            if (pending.contains(plugin))
            {
                throw new IllegalStateException("Cyclic dependency chain found: " + pending);
            }

            // start tracking plugins that are pending for configuration
            pending.add(plugin);

            plugin.configure(this);

            // stop pending tracking
            pending.remove(plugin);

            // check again if it's included (a child might've configured it already)
            // add to out-collection if not already present
            // this is a bit different from `find` method as it does check for exact instance
            // and not a sub-type
            if (!plugins.contains(plugin))
            {
                // core-plugin must always be the first one (if it's present)
                if (CorePlugin.class.isAssignableFrom(plugin.getClass()))
                {
                    plugins.add(0, plugin);
                } else
                {
                    plugins.add(plugin);
                }
            }
        }
    }

    @NonNull
    private <P extends StylarPlugin> P get(@NonNull Class<P> type)
    {

        // check if present already in plugins
        // find in origin, if not found -> throw, else add to out-plugins

        P plugin = find(plugins, type);

        if (plugin == null)
        {

            plugin = find(origin, type);

            if (plugin == null)
            {
                throw new IllegalStateException("Requested plugin is not added: " +
                    "" + type.getName() + ", plugins: " + origin);
            }

            configure(plugin);
        }

        return plugin;
    }
}
