package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.core.Quadrant;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.item.Item;
import com.intellectualsites.rectangular.item.Material;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;

import javax.script.*;

/**
 * TODO: This.
 */
public class ScriptManager {
    
    @Getter
    private ScriptEngine engine;
    
    @Getter
    private ScriptContext context;
    
    public ScriptManager() {
        engine = new ScriptEngineManager(null).getEngineByName("nashorn");
        if (engine == null) {
            engine = new ScriptEngineManager(null).getEngineByName("JavaScript");
            Rectangular.get().getServiceManager().logger().info("Nashorn wasn't available, using the legacy engine");
        }
        ScriptContext context = new SimpleScriptContext();
    }
    
    public Bindings getBindings() {
        Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("RegionManager", Rectangular.get().getRegionManager());
        bindings.put("Logger", Rectangular.get().getServiceManager().logger());
        bindings.put("WorldManager", Rectangular.get().getWorldManager());
        bindings.put("PlayerManager", Rectangular.get().getServiceManager().getPlayerManager());
        bindings.put("Region", Region.class);
        bindings.put("Vector2", Vector2.class);
        bindings.put("RectangularPlayer", RectangularPlayer.class);
        bindings.put("Rectangle", Rectangle.class);
        bindings.put("Quadrant", Quadrant.class);
        bindings.put("Item", Item.class);
        for (Enum<?> value : Material.values()) {
            bindings.put("MATERIAL_" + value.name(), value);
        }
        return bindings;
    }
    
}
